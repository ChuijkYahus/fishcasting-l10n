package hauveli.fishcasting.features.fish.goop

import com.li64.tide.registries.entities.models.FishModel
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider


class GoopModel(context: EntityRendererProvider.Context) : FishModel( context, LAYER_LOCATION ) {

    /*
    override fun setupAnim(
        fish: Mob,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        // basedo n cave crawler
        // todo: make it look at players?
        super.setupAnim(fish, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
    }
    */

    override fun flipInAir(): Boolean {
        return false
    }

    val outside: ModelPart
    val inside: ModelPart

    init {
        this.outside = root().getChild("outside")
        this.inside = this.outside.getChild("inside")
    }

    companion object {
        // So that I can re-remember that this is what the first argument in "model layer location" is meant to be
        private val TEXTURE = id("textures/entity/goop.png")

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        //@Override
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val outside = partdefinition.addOrReplaceChild(
                "outside",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -4.0f, -5.0f, 6.0f, 4.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val inside = outside.addOrReplaceChild(
                "inside",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-2.0f, -3.0f, -3.5f, 4.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}