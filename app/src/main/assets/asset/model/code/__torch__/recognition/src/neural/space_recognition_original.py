class CNN(Module):
  __parameters__ = []
  training : bool
  block1 : __torch__.torch.nn.modules.container.Sequential
  block2 : __torch__.torch.nn.modules.container.___torch_mangle_3.Sequential
  block3 : __torch__.torch.nn.modules.container.___torch_mangle_6.Sequential
  def forward(self: __torch__.recognition.src.neural.space_recognition_original.CNN,
    input: Tensor) -> Tensor:
    _0 = self.block3
    _1 = (self.block2).forward((self.block1).forward(input, ), )
    input0 = torch.view(_1, [-1, 1568])
    return (_0).forward(input0, )
