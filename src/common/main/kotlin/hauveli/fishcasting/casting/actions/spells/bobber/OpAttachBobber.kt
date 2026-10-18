package hauveli.fishcasting.casting.actions.spells.bobber

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapEntityTooFarAway
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.getBobber
import hauveli.fishcasting.casting.iota.getHookable
import hauveli.fishcasting.mixin.SetHookedEntityTideFishingHookAccessor
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.projectile.FishingHook

object OpAttachBobber : SpellAction {
    override val argc = 2


    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
        val serverLevel = env.world

        // I think these do the entity Iota check for me
        val someBobber = args.getBobber(serverLevel, 0) // first? I think args must have 1 argument so this is safe?
        val someHookable = args.getHookable(serverLevel, 1)
        // Not an entity
        /*
        if (maybeTargetEntityIota !is EntityIota) {
            throw MishapInvalidIota.ofType(maybeTargetEntityIota, OpGetBobbersCatch.argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

         */
        // Too far, only check if not owned by self
        if (caster is ServerPlayer && !someBobber.playerOwner.`is`(caster)) {
            env.assertEntityInRange(someBobber)
        }

        // These should not error, as they are expected behaviour despite returning null
        val justReeled = getFishOnHook(someBobber)
        val haul = someBobber.hookedIn
        val catch = someBobber.catchType
        if (justReeled != null
            || haul != null
            || catch != TideFishingHook.CatchType.NOTHING) {
            // throw MishapAlreadyHooked() // "expected a place to attach an entity, found [other entity]"? "The bobber rejected the []"? "The hook
            // todo: better mishap
            throw MishapBadEntity.of(someBobber, "fishcasting.already_hooked")
        }

        if (!someHookable.boundingBox.intersects(someHookable.boundingBox)) {
            throw MishapEntityTooFarAway(someHookable) // todo: make it clear that the bobber has to intersect the target...
        }

        return SpellAction.Result(
            Spell(someBobber, someHookable),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.cloud(someHookable.position().add(0.0, someHookable.eyeHeight / 2.0, 0.0), 1.0))
        )
    }


    fun getFishOnHook(hook: TideFishingHook): ItemEntity? {
        // certain identify the correct entity?
        for (fishbert in hook.level().getEntitiesOfClass(
            ItemEntity::class.java,
            hook.boundingBox.inflate(10.0)
        )) {
            // TODO: somehow obtain when player began fishing minigame?
            val marginOfError = 500
            val opData = FishData.get(fishbert.item)
            if (fishbert.tickCount == 0
                && fishbert.tags
                    .containsAll(listOf<String>(Fishcasting.FISHBERT_TAG, hook.playerOwner.getStringUUID()))
            ) {
                return fishbert
            }
        }
        return null
    }

    private data class Spell(val bobber: TideFishingHook, val target: Entity) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            (bobber as SetHookedEntityTideFishingHookAccessor).`fishcasting$setHookedEntity`(target)
        }
    }
}