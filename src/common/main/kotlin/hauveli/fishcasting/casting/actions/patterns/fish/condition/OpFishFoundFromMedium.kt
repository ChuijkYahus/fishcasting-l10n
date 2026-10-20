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
import com.li64.tide.data.fishing.conditions.types.FishingMediumCondition
import com.li64.tide.data.fishing.mediums.FishingMedium
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.getFishData
import hauveli.fishcasting.casting.iota.getMedium
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromMedium : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getFishData(env.world, 0, argc)
        val someMedium = args.getMedium(1, argc)

        val relevantConditions = target.conditions()
            .filter {
                it is FishingMediumCondition
            }
        if (relevantConditions.isEmpty())
            return listOf(NullIota())

        //val medium = someMedium.mediumIdFromOrdinal()
        val medium = someMedium.medium

        val foundInMedium = relevantConditions.all { condition ->
            when (condition) {
                is FishingMediumCondition -> {
                    // todo: I kind of wanna use tide:water (medium.fishingMedium.id()) ...
                    // Fishcasting.LOGGER.info("medium was: ${medium.name} but condition was ${condition.mediumId}, do I use ${medium.fishingMedium.toString()} or ${medium.fishingMedium.id()}")
                    condition.mediumId == medium.name.lowercase()
                }

                else -> true // ugh
            }
        }

        return listOf(BooleanIota(foundInMedium))
    }
}