package hauveli.fishcasting.client

import at.petrak.hexcasting.client.render.GaslightingTracker
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.xplat.IClientXplatAbstractions
import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.interop.inline.InlineFishcastingClient
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.renderer.item.ItemPropertyFunction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.math.abs


object FishcastingClient {
    fun init() {
        // todo: figure out why neoforge hates this
        // FishcastingItems.registerItemModelProperties()
        InlineFishcastingClient.init()
        registerGaslight4(FishcastingItems.SHEPHERDS_CASTING_ROD.value)
        registerGaslight4(FishcastingItems.BLESSED_FOCUS_BOBBER.value)

        registerRodWithCastProperty(FishcastingItems.SHEPHERDS_CASTING_ROD.value)
        registerRodWithCastProperty(FishcastingItems.ROCKY_CASTING_ROD.value)

        registerItemPropertyForScryingVision(FishcastingItems.PERHOSGATA.value)
        registerItemPropertyForScryingVision(FishcastingItems.PERHOSGATA_BUCKET.value)
    }

    // private apparently
    // https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/client/RegisterClientStuff.java#L148
    fun registerGaslight4(item: Item) {
        IClientXplatAbstractions.INSTANCE.registerItemProperty(
            item,
            GaslightingTracker.GASLIGHTING_PRED,
            { stack, level, holder, holderID ->
                abs(GaslightingTracker.getGaslightingAmount() % 4f) })
    }

    fun registerRodWithCastProperty(item: Item) {
        ItemProperties.register(
            item,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION// ?????? fucking neoforge
        )
    }

    fun registerItemPropertyForScryingVision(item: Item) {
        ItemProperties.register(
            item,
            Fishcasting.id("no_scry_sight"), // todo: put this somewhere...
            {
                    stack: ItemStack?, level: ClientLevel?, entity: LivingEntity?, seed: Int ->
                val player = Minecraft.getInstance().player // thank god this is on the client side of things
                if (player == null) {
                    1f
                } else {
                    if (player.getAttributeValue(HexAttributes.SCRY_SIGHT) > 0.0
                        && player.getAttributeValue(HexAttributes.FEEBLE_MIND) <= 0.0)
                        0f
                    else
                        1f
                }
            }
        )
    }
}