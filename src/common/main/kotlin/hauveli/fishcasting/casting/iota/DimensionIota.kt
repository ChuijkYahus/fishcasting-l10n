package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class DimensionIota : EnvironmentIota {
    val value: ResourceKey<Level>

    constructor(dimension: ResourceKey<Level>) : super(Supplier { FishcastingIotaTypes.DIMENSION.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.value = dimension
    }

    override fun isTruthy(p0: CastingEnvironment?): Boolean {
        return true
    }

    override fun toleratesOther(otherIota: Iota?): Boolean {
        return typesMatch(this, otherIota)
                && otherIota is DimensionIota
                && this.value == otherIota.value
    }

    override fun display(): Component {
        return (InlineDimensionData(value.location().toString())).displayWithTextAndInline()
    }

    override fun hashCode(): Int {
        return value.hashCode() // what am I supposed to put here.....
    }

    companion object {

        var TYPE: IotaType<DimensionIota> = object : IotaType<DimensionIota>() {

            val CODEC: MapCodec<DimensionIota> =
                Codec.STRING
                    .fieldOf("dimension")
                    .xmap(
                        { string ->
                            DimensionIota(
                                ResourceKey.create(
                                    Registries.DIMENSION,
                                    ResourceLocation.parse(string)
                                )
                            )
                        },
                        { iota ->
                            iota.value.location().toString()
                        }
                    )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DimensionIota> =
                StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    { it.value.location().toString() },
                    { string ->
                        DimensionIota(
                            ResourceKey.create(
                                Registries.DIMENSION,
                                ResourceLocation.parse(string)
                            )
                        )
                    }
                )

            override fun validate(
                iota: DimensionIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<DimensionIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DimensionIota> {
                return STREAM_CODEC
            }

            override fun color(): Int {
                return EnvironmentIota.TYPE.color()
            }
        }
    }

    override fun equals(iotaToCompare: Any?): Boolean {
        if (this === iotaToCompare) return true
        if (javaClass != iotaToCompare?.javaClass) return false
        if (!super.equals(iotaToCompare)) return false

        iotaToCompare as DimensionIota

        return value == iotaToCompare.value
    }
}