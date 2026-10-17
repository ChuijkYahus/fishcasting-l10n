package hauveli.fishcasting.features.fish.goop

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob


class GoopRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<Mob, GoopModel>(
    context,
    GoopModel(context),
    0.3f
) {
    override fun render(p0: Mob, p1: Float, p2: Float, p3: PoseStack, p4: MultiBufferSource, p5: Int) {
        // todo: deform it as it moves somehow... Maybe it'd be easier to animate in blockbench and then interpolate?
        super.render(p0, p1, p2, p3, p4, p5)
    }

    override fun getTextureLocation(p0: Mob): ResourceLocation {
        return GoopModel.LAYER_LOCATION.model
    }
}
