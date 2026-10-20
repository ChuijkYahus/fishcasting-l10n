package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.interop.inline.climate.InlineClimateData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class ClimateIota : EnvironmentIota {
    val value: Float

    constructor(temp: Float) : super(Supplier { FishcastingIotaTypes.CLIMATE.value }) {
        this.value = temp
    }

    override fun isTruthy(p0: CastingEnvironment?): Boolean {
        return true
    }

    override fun display(): Component {
        return (InlineClimateData(value)).displayWithTextAndInline() // baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    companion object {

        var TYPE: IotaType<ClimateIota> = object : IotaType<ClimateIota>() {

            val CODEC: MapCodec<ClimateIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.FLOAT.fieldOf("climate")
                            .forGetter { it.value }
                    ).apply(
                        inst,
                        ::ClimateIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ClimateIota> =
                StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    { it.value },
                    ::ClimateIota
                )

            override fun codec(): MapCodec<ClimateIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ClimateIota> {
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

        iotaToCompare as ClimateIota

        return value == iotaToCompare.value
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value.toInt()
        return result
    }
}