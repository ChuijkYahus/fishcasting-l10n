package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.interop.inline.depth.InlineDepthData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class DepthIota : EnvironmentIota {
    val value: Int

    constructor(yLevel: Int) : super(Supplier { FishcastingIotaTypes.DEPTH.value }) {
        this.value = yLevel
    }

    override fun isTruthy(p0: CastingEnvironment?): Boolean {
        return true
    }

    override fun toleratesOther(otherIota: Iota?): Boolean {
        return typesMatch(this, otherIota)
                && otherIota is DepthIota
                && this.value == otherIota.value
    }

    override fun display(): Component {
        return (InlineDepthData(value)).displayWithTextAndInline() // baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    companion object {

        var TYPE: IotaType<DepthIota> = object : IotaType<DepthIota>() {

            val CODEC: MapCodec<DepthIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.INT.fieldOf("depth")
                            .forGetter { it.value }
                    ).apply(
                        inst,
                        ::DepthIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DepthIota> =
                StreamCodec.composite(
                    ByteBufCodecs.INT,
                    { it.value },
                    ::DepthIota
                )

            override fun validate(
                iota: DepthIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<DepthIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DepthIota> {
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

        iotaToCompare as DepthIota

        return value == iotaToCompare.value
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        return result
    }
}