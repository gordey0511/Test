class MaxPool2d(Module):
  __parameters__ = []
  training : bool
  def forward(self: __torch__.torch.nn.modules.pooling.MaxPool2d,
    argument_1: Tensor) -> Tensor:
    input = torch.max_pool2d(argument_1, [2, 2], [2, 2], [0, 0], [1, 1], False)
    return input
