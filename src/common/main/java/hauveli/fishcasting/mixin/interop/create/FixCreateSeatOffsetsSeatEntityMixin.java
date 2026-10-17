package hauveli.fishcasting.mixin.interop.create;

//import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import hauveli.fishcasting.registry.FishcastingEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(SeatEntity.class)

// wow cool! But I sort of wonder if I shouldn't go about this in a different way...
@Pseudo
@Mixin(
        targets = "com.simibubi.create.content.contraptions.actors.seat.SeatEntity"
)
public class FixCreateSeatOffsetsSeatEntityMixin {

    @Inject(
            method = "getCustomEntitySeatOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void customSeatOffset(
            Entity entity,
            CallbackInfoReturnable<Double> cir
    ) {
        EntityType<?> type = entity.getType();
        if (type == FishcastingEntities.BLESSED.getValue()) {
            cir.setReturnValue(0.11);
        } else if (type == FishcastingEntities.EDIFIED_FISH.getValue()) {
            cir.setReturnValue(0.1);
        } else if (type == FishcastingEntities.CURSED.getValue()) {
            cir.setReturnValue(0.09);
        } else if (type == FishcastingEntities.PERHOSGATA.getValue()) {
            cir.setReturnValue(0.21);
        }
    }
}
