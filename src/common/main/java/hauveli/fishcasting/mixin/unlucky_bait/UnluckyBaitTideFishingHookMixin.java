package hauveli.fishcasting.mixin.unlucky_bait;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.li64.tide.util.BaitUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TideFishingHook.class)
public abstract class UnluckyBaitTideFishingHookMixin {
    @Shadow
    protected ItemStack rod;

    @ModifyExpressionValue(
            method = "getContext",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;floor(F)I"
            )
    )
    private int fishcasting$modifyFishingLuck(int original) {
        if (!fishcasting$shouldGetMoreJunk())
            return original;
        return -50000;
    }

    // todo: use a tag for this? hmm...
    @Unique
    private boolean fishcasting$shouldGetMoreJunk() {
        return BaitUtils.hasBait(FishcastingItems.UNLUCKY_BAIT.getValue(), this.rod);
    }
}
