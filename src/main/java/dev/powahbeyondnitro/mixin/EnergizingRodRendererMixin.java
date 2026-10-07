package dev.powahbeyondnitro.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.powahbeyondnitro.TieredMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import owmii.powah.client.render.tile.EnergizingRodRenderer;
import owmii.powah.block.energizing.EnergizingRodTile;

@Mixin(value = EnergizingRodRenderer.class, remap = false)
public class EnergizingRodRendererMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lowmii/powah/block/Tier;getColor()I"))
    private int powahbeyondnitro$color(int original, @Local(argsOnly = true) EnergizingRodTile tile) {
        return tile.getBlockState().getBlock() instanceof TieredMachine machine ? machine.beyondTier().color : original;
    }
}

