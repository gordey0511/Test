package com.math4.user.braillyrecognition;

        import android.Manifest;
        import android.app.Activity;
        import android.content.Context;
        import android.content.pm.ActivityInfo;
        import android.content.pm.PackageManager;
        import android.content.res.Configuration;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.icu.text.UnicodeSetSpanner;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.ViewGroup.LayoutParams;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.view.View;

        import android.hardware.Camera;
        import android.hardware.Camera.Size;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import com.google.android.material.bottomsheet.BottomSheetDialog;

        import org.pytorch.IValue;
        import org.pytorch.Module;
        import org.pytorch.Tensor;
        import org.pytorch.torchvision.TensorImageUtils;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;

public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback
{
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView preview;
    private Button shotBtn;
    TextView result;
    BottomSheetDialog bottomSheetDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // наше SurfaceView имеет имя SurfaceView01
        preview = findViewById(R.id.SurfaceView01);

        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // кнопка имеет имя Button01
        shotBtn = findViewById(R.id.Button01);
        shotBtn.setOnClickListener(this);

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_bottom_sheet_result, null);//Главные функции
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        result=view.findViewById(R.id.textResult);
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        camera = Camera.open();
    }

    @Override
    protected void onPause()
    {
        super.onPause();


        if (camera != null)
        {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        //camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        try
        {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }



        Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;

        int previewSurfaceWidth = preview.getWidth();
        int previewSurfaceHeight = preview.getHeight();

        LayoutParams lp = preview.getLayoutParams();

        // здесь корректируем размер отображаемого preview, чтобы не было искажений

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        {
            // портретный вид
            camera.setDisplayOrientation(90);
            lp.height = previewSurfaceHeight;
            lp.width = (int) (previewSurfaceHeight / aspect);
        }
        else
        {
            // ландшафтный
            camera.setDisplayOrientation(0);
            lp.width = previewSurfaceWidth;
            lp.height = (int) (previewSurfaceWidth / aspect);
        }

        preview.setLayoutParams(lp);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Override
    public void onClick(View v)
    {
        Toast.makeText(getApplicationContext(),"Загрузка",Toast.LENGTH_SHORT).show();

        if (v == shotBtn)
        {
            // либо делаем снимок непосредственно здесь
            // 	либо включаем обработчик автофокуса

            camera.autoFocus(this);
//            camera.takePicture(null, null, null, this);
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // сохраняем полученные jpg в папке /sdcard/CameraExample/
        // имя файла - System.currentTimeMillis()
        Log.e("CHECKCHECK", "hi");

        try
        {
//            File saveDir = new File(Environment.getExternalStorageDirectory().getPath());
//
//            if (!saveDir.exists())
//            {
//                saveDir.mkdirs();
//            }
//
//            FileOutputStream os = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis()));
//            os.write(paramArrayOfByte);
//            os.close();

            Module module=null;

//            try {
//            Toast.makeText(getApplicationContext(),assetFilePath(this, "model.pt").toString(),Toast.LENGTH_LONG).show();
            Log.e("checkAsset",assetFilePath(this, "model.pt").toString());
            module = Module.load(assetFilePath(this, "model.pt"));
//            }catch (IOException e){
//            }
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(paramArrayOfByte , 0, paramArrayOfByte.length);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 86, 86, false);
            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap,
                    TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
//            Toast.makeText(getApplicationContext(),"IEBALPOLINKU",Toast.LENGTH_LONG).show();



            Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
            float[] predicted = outputTensor.getDataAsFloatArray();

            int letter = 0;
            for (int i = 0; i < predicted.length; i++) {
                letter = predicted[i] > predicted[letter] ? i : letter;
            }



            result.setText(((char)letter+"").toString());



//            Log.e("checkcheckLetter",(char)letter+"");
//

        }
        catch (Exception e)
        {

        }

        bottomSheetDialog.show();
        // после того, как снимок сделан, показ превью отключается. необходимо включить его
        paramCamera.startPreview();
    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
        if (paramBoolean)
        {
            // если удалось сфокусироваться, делаем снимок
            paramCamera.takePicture(null, null, null, this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // здесь можно обрабатывать изображение, показываемое в preview
    }
}