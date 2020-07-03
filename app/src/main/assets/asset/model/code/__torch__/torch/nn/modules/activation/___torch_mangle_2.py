class LeakyReLU(Module):
  __parameters__ = []
  training : bool
  def forward(self: __torch__.torch.nn.modules.activation.___torch_mangle_2.LeakyReLU,
    argument_1: Tensor) -> Tensor:
    return torch.leaky_relu(argument_1, 0.01)
