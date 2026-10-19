package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.interop.inline.daytime.InlineDaytimeData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class DaytimeIota : EnvironmentIota {
    val value: Long

    constructor(daytime: Long) : super(Supplier { FishcastingIotaTypes.DAYTIME.value }) {
        this.value = daytime
    }

    override fun isTruthy(p0: CastingEnvironment?): Boolean {
        return true
    }

    override fun toleratesOther(otherIota: Iota?): Boolean {
        return typesMatch(this, otherIota)
                && otherIota is DaytimeIota
                && this.value == otherIota.value
    }

    override fun display(): Component {
        return (InlineDaytimeData(value)).displayWithTextAndInline() // baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    companion object {

        var TYPE: IotaType<DaytimeIota> = object : IotaType<DaytimeIota>() {

            val CODEC: MapCodec<DaytimeIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.LONG.fieldOf("daytime")
                            .forGetter { it.value }
                    ).apply(
                        inst,
                        ::DaytimeIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DaytimeIota> =
                StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    { it.value },
                    ::DaytimeIota
                )

            override fun validate(
                iota: DaytimeIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<DaytimeIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DaytimeIota> {
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

        iotaToCompare as DaytimeIota

        return value == iotaToCompare.value
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value.toInt()
        return result
    }
}