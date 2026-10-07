package dev.powahbeyondnitro.mixin;

import dev.powahbeyondnitro.TieredMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import owmii.powah.block.cable.CableTile;

/** Keep cable networks tier-specific, including after reload through Powah's BE factory. */
@Mixin(value = CableTile.class, remap = false)
public abstract class CableTileMixin {
    @Inject(method = "canConnectTo", at = @At("HEAD"), cancellable = true)
    private void powahbeyondnitro$connect(CableTile other, CallbackInfoReturnable<Boolean> result) {
        var self = (CableTile) (Object) this;
        var a = self.getBlockState().getBlock();
        var b = other.getBlockState().getBlock();
        if (a instanceof TieredMachine || b instanceof TieredMachine) result.setReturnValue(a == b);
    }
}
