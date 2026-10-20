package hauveli.fishcasting.casting.actions.patterns.fish.condition

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import com.li64.tide.data.fishing.conditions.types.TimeOfDayCondition
import hauveli.fishcasting.casting.iota.getDaytime
import hauveli.fishcasting.casting.iota.getFishData


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromDaytime : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getFishData(env.world, 0, argc)
        val someDaytime = args.getDaytime(1, argc)

        val relevantConditions = target.conditions()
            .filter {
                it is TimeOfDayCondition
            }
        if (relevantConditions.isEmpty())
            return listOf(NullIota()) // if it has no conditions for the dimension, it should be true?

        val timeOfDay = someDaytime.toLong()  // I'm not so sure this is the best option going forwards........ what about other mediums from other mods?

        val foundInTime = relevantConditions.all { condition ->
            when (condition) {
                is TimeOfDayCondition -> {
                    condition.ranges.any { it.contains(timeOfDay) }
                }

                else -> true // ugh
            }
        }

        return listOf(BooleanIota(foundInTime))
    }
}