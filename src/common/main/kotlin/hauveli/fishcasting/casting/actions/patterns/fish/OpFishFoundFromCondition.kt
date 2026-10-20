package hauveli.fishcasting.casting.actions.patterns.fish

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromCondition : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        TODO()
        /*
        val target = args.getFishData(env.world, 0, argc) // just so I mishap on the entity being out of ambit before going deeper
        val someEnv = args.getEnvironment(1, argc)
        val someIota = args[1]

        return when (someEnv) {
            is EnvironmentValue.Biome -> OpFishFoundFromBiome.execute(args, env)
            is EnvironmentValue.Climate -> OpFishFoundFromClimate.execute(args, env)
            is EnvironmentValue.Daytime -> OpFishFoundFromTimeOfDay.execute(args, env)
            is EnvironmentValue.Depth -> OpFishFoundFromDepth.execute(args, env)
            is EnvironmentValue.Dimension -> OpFishFoundFromDimension.execute(args, env)
            is EnvironmentValue.Medium -> OpFishFoundFromMedium.execute(args, env)
            is EnvironmentValue.MoonPhase -> OpFishFoundFromMoonPhase.execute(args, env)
            is EnvironmentValue.Structure -> OpFishFoundFromStructure.execute(args, env)
            is EnvironmentValue.Weather -> OpFishFoundFromWeather.execute(args, env)
        }

         */
    }
}