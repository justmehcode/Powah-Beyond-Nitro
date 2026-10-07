package dev.powahbeyondnitro.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.powahbeyondnitro.TieredMachine;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import owmii.powah.client.render.tile.ReactorPartRenderer;
import owmii.powah.block.reactor.ReactorPartTile;

@Mixin(value = ReactorPartRenderer.class, remap = false)
public class ReactorPartRendererMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE",
            target = "Lowmii/powah/Powah;id(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation powahbeyondnitro$texture(ResourceLocation original, @Local(argsOnly = true) ReactorPartTile tile) {
        if (tile.getBlockState().getBlock() instanceof TieredMachine machine && original.getPath().contains("nitro")) {
            return ResourceLocation.fromNamespaceAndPath("powahbeyondnitro", original.getPath().replace("nitro", machine.beyondTier().id()));
        }
        return original;
    }
}

