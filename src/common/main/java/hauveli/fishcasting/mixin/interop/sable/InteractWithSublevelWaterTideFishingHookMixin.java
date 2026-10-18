package hauveli.fishcasting.mixin.interop.sable;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class InteractWithSublevelWaterTideFishingHookMixin {
    @Shadow
    private Entity hookedIn;

    @Shadow
    public abstract ItemStack getHook();

    @Shadow
    public abstract ItemStack getLine();

    @Shadow
    public abstract Player getPlayerOwner();

    @Unique
    private Integer maxDistanceSquared = 16 * 16;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void fishcasting$forceHookToBeNearby(CallbackInfo ci) {
        TideFishingHook hook = (TideFishingHook) (Object) this;

        hook.position();
    }
}
