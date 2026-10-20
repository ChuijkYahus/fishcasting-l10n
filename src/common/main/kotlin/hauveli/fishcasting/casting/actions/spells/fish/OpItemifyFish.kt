package hauveli.fishcasting.casting.actions.spells.fish

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.item.TideItemData
import com.li64.tide.registries.entities.fish.AmphibiousFish
import hauveli.fishcasting.casting.iota.getAliveFish
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity


object OpItemifyFish : SpellAction {
    override val argc = 1

    private fun isFishWithItem(entity: Entity): Boolean {
        if (entity is AmphibiousFish)
            return true
        return false
    }

    // todo: more flexible fish entity <-> item check somehow, maybe configurable by a json?
    // I'd prefer if it were automatic though...
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val entity = args.getEntity(env.world, 0, OpFishifyItem.argc)
        env.assertEntityInRange(entity)
        val target = args.getAliveFish(env.world, 0, OpFishifyItem.argc)

        return SpellAction.Result(
            Spell(entity, target),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.cloud(entity.position().add(0.0, entity.eyeHeight / 2.0, 0.0), 1.0))
        )
    }

    private data class Spell(val target: Entity, val fishData: FishData) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {

            val itemEntity = ItemEntity(EntityType.ITEM, target.level())
            itemEntity.item = fishData.fish().value().defaultInstance // todo: dont just use default instance
            itemEntity.setPos(target.position())
            itemEntity.deltaMovement = target.deltaMovement

            if (target is FishLengthHolder) {
                TideItemData.FISH_LENGTH.set(itemEntity.item, target.`tide$getLength`())
            }
            target.level().addFreshEntity(itemEntity)
            target.discard()
        }
    }
}
