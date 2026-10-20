package hauveli.fishcasting.casting.actions.patterns.fish.condition

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.conditions.types.DimensionsCondition
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.getDimension
import hauveli.fishcasting.casting.iota.getFishData
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromDimension : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getFishData(env.world, 0, argc)
        val someDimension = args.getDimension(1, argc)

        val relevantConditions = target.conditions()
            .filter {
                it is DimensionsCondition
            }
        if (relevantConditions.isEmpty())
            return listOf(NullIota())

        val dimension = someDimension.value

        val foundInDimension = relevantConditions.all { condition ->
            when (condition) {
                is DimensionsCondition -> {
                    condition.dimensions.contains(dimension)
                }

                else -> true // ugh
            }
        }

        return listOf(BooleanIota(foundInDimension))
    }
}