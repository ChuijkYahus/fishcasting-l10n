package hauveli.fishcasting.casting.actions.spells.fish

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.Tide
import com.li64.tide.config.TideConfig
import com.li64.tide.config.TideServerConfig
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.item.TideDataComponents
import com.li64.tide.data.item.TideItemData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.getAliveFish
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity

object OpFishifyItem : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val entity = args.getEntity(env.world, 0, argc)
        env.assertEntityInRange(entity)
        val target = args.getAliveFish(env.world, 0, argc)

        return SpellAction.Result(
            Spell(entity, target),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.cloud(entity.position().add(0.0, entity.eyeHeight / 2.0, 0.0), 1.0))
        )
    }

    private data class Spell(val target: Entity, val fishData: FishData) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            // ummmm... casting Entity has to be a player to get a UseOnContext... What do I do?
            var length: Double = 0.0
            // itemEntities are not FishLengthHolders.... uhh...
            // todo: fix this
            if (target is FishLengthHolder) {
                length = target.`tide$getLength`() // holyyy thank you tide dev
            }
            /*
            // I don't know when or what this will look like
            var shiny: Boolean = false
            if (target is FishShinyHolder) {
                length = target.`tide$getShiny`()
            }
             */
            val componentCustomName: Component? = target.customName
            // 2.1 removes time from it? less work for me I suppose, Spoiled can just convert dead fish to some other generic item or something (by proxy making them unbucketable)
            val currentTimeStamp = target.level().gameTime

            val entityHolder = fishData.display().get().entityHolder()
            val entityToSpawn = entityHolder.value().create(target.level())
            // I don't think this can be reached? but I am not going to take the risk of having something annoying to debug.
            if (entityToSpawn == null) {
                Fishcasting.LOGGER.warn("Fishcasting: AHHH HOW DID WE GET HERE??? report this to the developer please")
                return
            }
            if (entityToSpawn is FishLengthHolder && length > 0.0) {
                entityToSpawn.`tide$setLength`(length)
            }
            entityToSpawn.setPos(target.position())
            entityToSpawn.deltaMovement = target.deltaMovement
            target.level().addFreshEntity(entityToSpawn)

            env.castingEntity?.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f)
            (target as ItemEntity).item.shrink(1)
        }
    }
}
