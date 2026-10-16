package hauveli.fishcasting.mixin.unlucky_bait;

import com.li64.tide.data.item.TideItemData;
import com.li64.tide.data.rods.BaitContents;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.li64.tide.util.BaitUtils;
import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TideFishingHook.class)
public abstract class UnluckyConsumesBaitTideFishingHookMixin {
    @Shadow
    protected ItemStack rod;


    // not super happy about it but writing this mixin was less headache than injecting inside the switch case at the right position...
    @Inject(
            method = "retrieve(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRetrieve(ItemStack rod, ServerLevel level, Player player,
                            CallbackInfoReturnable<Integer> cir) {
        TideFishingHook hook = (TideFishingHook) (Object) this;
        if (player == null || player.fishing == null) return;
        if (hook.getCatchType() != TideFishingHook.CatchType.ITEM) return;
        if (player.isCreative() || !BaitUtils.hasBait(rod)) return;
        if (!BaitUtils.hasBait(FishcastingItems.UNLUCKY_BAIT.getValue(), rod)) return;

        BaitContents.Mutable contents = new BaitContents.Mutable(
                TideItemData.BAIT_CONTENTS.getOrDefault(rod, new BaitContents())
        );
        contents.shrinkAll();
        TideItemData.BAIT_CONTENTS.set(rod, contents.toImmutable());
    }
}