package hauveli.fishcasting.mixin;


import at.petrak.hexcasting.common.items.pigment.ItemUUIDPigment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(targets = "at.petrak.hexcasting.common.items.pigment.ItemUUIDPigment$MyColorProvider")
public abstract class TestingColorMixin {

    @Shadow
    @Final
    @Mutable
    private int[] colors;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void replaceColors(UUID owner, CallbackInfo ci) {
        this.colors = new int[]{
                0xFFFF0000,
                0xFF00FF00,
                0xFF0000FF,
                0xFFFFFF00
        };
    }
}