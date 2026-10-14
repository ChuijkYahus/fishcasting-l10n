package hauveli.fishcasting.mixin.bugfixes.tide;

import com.li64.tide.data.TideTags;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hauveli.fishcasting.Fishcasting;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TideFishingHook.class)
public abstract class FixDoubledBaitUsageTideFishingHookMixin {

    // private boolean fishcasting$hasUsedBait = false;

    // fucking EVIL anti-bait waste mixin to fix the doubled bait usage bug on the midas/villager rods
    // this shouldn't cause issues (so long as I'm not mistaken in understanding the messages by the developer and the code)
    @ModifyExpressionValue(
            method = "retrieve(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/li64/tide/util/BaitUtils;hasBait(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean fishcasting$onlyFishEatBait(
            boolean hasBait,
            @Local(ordinal = 1) ItemStack stack
    ) {
        // could use hasUsedBait to check if I've caught a bait already because the hook is reset each cast
        // but by default, catching non-fish does not consume bait, which would make this a change in behaviour instead of a bugfix if I did that...
        return hasBait && stack.is(TideTags.Items.FISH);
    }
}