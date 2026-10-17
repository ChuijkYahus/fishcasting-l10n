package hauveli.fishcasting.interop.emi

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.mod.HexTags
import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import net.minecraft.resources.ResourceLocation

@EmiEntrypoint
class FishcastingEMIPlugin : EmiPlugin {
    override fun register(registry: EmiRegistry) {
    }

    companion object {


        fun init() {}
    }
}