package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.fishing.conditions.types.WeatherType
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

// TODO: get SNOW as well, currently this can be done via temperature + precipitation, but that's not ideal...
object OpGetPrecipitation : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val blockPos = args.getBlockPos(0, argc)
        env.assertPosInRange(blockPos)


        val precipitationAtPos = getPrecipitation(env.world, blockPos)
        return listOf(BooleanIota(precipitationAtPos))
    }

    fun getPrecipitation(level: ServerLevel, blockPos: BlockPos): Boolean {
        if (!level.isRaining && !level.isThundering)
            return false

        return level.isRainingAt(blockPos)
    }
}