package hauveli.fishcasting.casting.actions.spells.fish

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexDamageTypes
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.item.TideItemData
import hauveli.fishcasting.casting.iota.getAliveFish
import hauveli.fishcasting.features.fish.cursed.CursedEntity
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity

object OpClobberFishToDeath : SpellAction {
    override val argc = 1


    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val entity = args.getEntity(env.world, 0, OpFishifyItem.argc)
        env.assertEntityInRange(entity)
        val target = args.getAliveFish(env.world, 0, OpFishifyItem.argc)

        return SpellAction.Result(
            Spell(entity),
            MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.Companion.cloud(entity.position().add(0.0, entity.eyeHeight / 2.0, 0.0), 1.0))
        )
    }

    fun killFishItemEntity(fishEntity: ItemEntity) {
        if (fishEntity.item.`is`(FishcastingTags.CURSED_MOSTLY_INDESTRUCTIBLE_ITEM))
            return
        TideItemData.FISH_LENGTH.set(fishEntity.item, null)
        TideItemData.IS_BUCKETABLE.set(fishEntity.item, null)
    }

    private data class Spell(val fishEntity: Entity) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            if (fishEntity is ItemEntity) {
                killFishItemEntity(fishEntity)
            } else {
                // not entirely happy with this but it does the job, I would prefer if the damage source were set to the caster...
                val caster = env.castingEntity
                if (caster is Entity)
                    fishEntity.hurt(caster.damageSources().magic(), 1f)

                if (fishEntity !is CursedEntity) {
                    fishEntity.kill()
                }
            }
        }
    }
}