class Sequential(Module):
  __parameters__ = []
  training : bool
  __annotations__["0"] = __torch__.torch.nn.modules.conv.___torch_mangle_0.Conv2d
  __annotations__["1"] = __torch__.torch.nn.modules.pooling.___torch_mangle_1.MaxPool2d
  __annotations__["2"] = __torch__.torch.nn.modules.activation.___torch_mangle_2.LeakyReLU
  def forward(self: __torch__.torch.nn.modules.container.___torch_mangle_3.Sequential,
    argument_1: Tensor) -> Tensor:
    _0 = getattr(self, "1")
    _1 = (getattr(self, "0")).forward(argument_1, )
    _2 = (getattr(self, "2")).forward((_0).forward(_1, ), )
    return _2
