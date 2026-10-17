package hauveli.fishcasting.mixin.interop.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

// todo: I couldn't be bothered to put the energy in to make it work right now so I'll do it later, but this is probably in the right direction ish
// todo2: if Create devs merge https://github.com/Creators-of-Create/Create/issues/9218
// then I win.
// it is labelled for 6.0.10, 6.0.11 so I am a bit hopeful that I won't need this mixin...
// wow cool! But I sort of wonder if I shouldn't go about this in a different way...
@Pseudo
@Mixin(
        targets = "com.simibubi.create.content.equipment.hats.CreateHatArmorLayer"
)
public abstract class FixCreateHatArmorLayerMixin {

    /*
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;isEmpty()Z",
                    shift = At.Shift.BEFORE
            )
    )
    private void fishcasting$renderCustomModel(
            CallbackInfo ci,
            @Local List<ModelPart> partsToHead,
            @Local EntityModel<?> entityModel
    ) {
        Fishcasting.LOGGER.info("Checking....");
        if (entityModel instanceof CursedModel cursedModel) {
            Fishcasting.LOGGER.info("well, cursed model sees it...");
            partsToHead.addAll(
                    List.of(cursedModel.getHead())
            );
        }
    }


    @Inject(
            method = "registerOn",
            at = @At(value = "HEAD")
    )
    private static void fishcasting$registerCustomModel(
            EntityRenderer<?> entityRenderer,
            CallbackInfo ci
    ) {
        Fishcasting.LOGGER.info("Checking....");
        if (!(entityRenderer instanceof FishRenderer<?> fishRenderer)) {
            return;
        }

        FishModel fishModel = fishRenderer.getModel();

        if (!(fishModel instanceof FishModel)) {
            return;
        }
        CreateHatArmorLayer<?, ?> layer = new CreateHatArmorLayer<>(fishRenderer);
        fishRenderer.addLayer((CreateHatArmorLayer) layer);
    }
     */
}
