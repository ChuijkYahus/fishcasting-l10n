package hauveli.fishcasting.mixin;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class ClingyFishingHookTideFishingHookMixin {
    @Shadow
    public abstract ItemStack getHook();

    @Shadow
    private int nibble;

    @Shadow
    private int timeUntilHooked;

    @Shadow
    @NotNull
    public abstract RandomSource getRandom();

    @Inject(method = "catchingFish", at = @At("TAIL"))
    private void fishcasting$clingy(CallbackInfo ci) {
        if (!this.getHook().is(FishcastingTags.NO_NIBBLE_TIMEOUT_HOOKS))
            return;
        // checking nibble first is probably smarter...
        if (this.nibble == 1) {
            this.nibble = 2;
        }
        if (this.timeUntilHooked > 0) {
            if (this.getRandom().nextFloat() > 0.50f) // about 50% slower...
                this.timeUntilHooked++;
        }
    }

}
