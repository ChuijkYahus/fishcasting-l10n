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

    // to anyone whom it may concern, I am testing this to see which colors I may want to request in the future.
    @Inject(method = "<init>", at = @At("RETURN"))
    private void replaceColors(UUID owner, CallbackInfo ci) {
        this.colors = new int[] {
                0xFF5D7A76,
                0xFFBF87A0,
                0xFF8F5BA6,
                0xFF6649A6,
                0xFF594167,
                0xFF330F45,
                0xFFB3B7D7,
                0xFFDCD2E6,
                0xFFC3DDEA,
                0xFF4658B0,
                0xFF100F14,
                0xFFA76F88,
                0xFF709878,
                0xFFF1E9EC,
                0xFFCBCEE4
        };

        /*
        this.colors = new int[] {
                0xFF00D7B9,
                0xFFFF4799,
                0xFFB102FF,
                0xFF4B00EF,
                0xFF6A00A8,
                0xFF380054,
                0xFF8B98FF,
                0xFFDCB9FF,
                0xFFAEE4FF,
                0xFF002AF6,
                0xFF070023,
                0xFFFF177F,
                0xFF09FF3A,
                0xFFFFDBE9,
                0xFFB0B9FF
        };
         */
    }


}