package dev.powahbeyondnitro.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.powahbeyondnitro.TieredMachine;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import owmii.powah.client.model.CableModel;
import owmii.powah.block.cable.CableTile;

@Mixin(value = CableModel.class, remap = false)
public class CableModelMixin {
    @ModifyExpressionValue(method = "renderType", at = @At(value = "INVOKE",
            target = "Lowmii/powah/Powah;id(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation powahbeyondnitro$texture(ResourceLocation original, @Local(argsOnly = true) CableTile tile) {
        if (tile.getBlockState().getBlock() instanceof TieredMachine machine && original.getPath().contains("nitro")) {
            return ResourceLocation.fromNamespaceAndPath("powahbeyondnitro", original.getPath().replace("nitro", machine.beyondTier().id()));
        }
        return original;
    }
}

