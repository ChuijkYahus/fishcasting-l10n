package hauveli.fishcasting.casting.actions.patterns.bobber

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.registries.entities.misc.fishing.HookAccessor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player


object OpGetOwnersBobber : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // Should cost at least something, I feel like, but not much.

    // I don't understand why java complains where kotlin is happy
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        // I dont understand why argc is used instead of getFirst/getLast but I will not break the convention
        // in favor of applying it for no reason at all and also likely incorrectly
        val caster = env.castingEntity
        val serverLevel = env.world
        val somePlayer = args.getPlayer(serverLevel, 0, OpGetBobbersCatch.argc)

        // Too far, only check if not owned by self
        if (caster is ServerPlayer && !somePlayer.`is`(caster)) {
            env.assertEntityInRange(somePlayer)
        }

        // These should not error, as they are expected behaviour despite returning null
        val activeHook = HookAccessor.getHook(somePlayer) ?: return listOf(NullIota())

        val optEntity = activeHook.selfAndPassengers.findFirst()
        if (optEntity.isEmpty) {
            return listOf(NullIota())
        }

        return listOf(EntityIota(optEntity.get()))
    }
}