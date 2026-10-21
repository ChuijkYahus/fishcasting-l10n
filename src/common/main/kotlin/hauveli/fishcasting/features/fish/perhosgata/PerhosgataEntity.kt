package hauveli.fishcasting.features.fish.perhosgata

import at.petrak.hexcasting.common.lib.HexParticles
import com.li64.tide.registries.entities.fish.AbstractTideFish
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.registry.FishcastingAdvancements
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3


class PerhosgataEntity(
    entityType: EntityType<out PerhosgataEntity?>?,
    level: Level?
) : AbstractTideFish(entityType, level) {
    var renderingOnThisTickIsDone: Long = 0L

    // mechanics things as I find them
    override fun getAirSupply(): Int {
        return 10 // super.getAirSupply()
    }

    override fun calculateFallDamage(p0: Float, p1: Float): Int {
        return 0 // super.calculateFallDamage(p0, p1)
    }

    override fun isInLava(): Boolean {
        return false // super.isInLava()
    }

    override fun fireImmune(): Boolean {
        return true // super.fireImmune()
    }

    override fun onGround(): Boolean {
        return false //super.onGround()
    }

    override fun isInWater(): Boolean {
        return true // super.isInWater()
    }

    override fun isUnderWater(): Boolean {
        return true // super.isUnderWater()
    }

    // this is kinda bad but idk how to do a better job at it with less effort
    // possible issues are recursion and repeated deep searches...
    // I just check 2 length without checking if it is a duplicate because it fulfils the requirements I want
    // (at least 2 unique entities at some point in a chain, started by a player)
    // but this is not at all explicit in the code so I'm writing this here haha...
    val MAX_CHAIN_LENGTH = 2
    fun getChainStarterOrNull(entity: PerhosgataEntity, alreadySeen: MutableList<PerhosgataEntity>): ServerPlayer? {
        if (alreadySeen.size > MAX_CHAIN_LENGTH)
            return null
        return when (val lastHurtBy = entity.lastHurtByMob) {
            is PerhosgataEntity -> {
                alreadySeen.addLast(entity)
                getChainStarterOrNull(lastHurtBy, alreadySeen)
            }
            is ServerPlayer -> {
                if(alreadySeen.size == MAX_CHAIN_LENGTH) {
                    lastHurtBy
                } else null
            }
            else -> null
        }
    }

    // this fucking megasucks I think but it works so I'll just live with this for now
    override fun hurt(damageSource: DamageSource, p1: Float): Boolean {
        val someEntity = damageSource.entity
        if (!this.isDeadOrDying
            && this.health > 0f
            && someEntity is PerhosgataEntity) {
            this.health = 0f
            this.lastHurtByMob = someEntity
            this.die(damageSource)
        }
        return super.hurt(damageSource, p1)
    }

    override fun die(damageSource: DamageSource) {
        super.die(damageSource) // unsure if I wanna do this still...
        if (this.level().isClientSide)
            return
        if (this.tickCount == 0 // this might seem weird but,
            && this.position().equals(Vec3.ZERO)) { // FishDisplays have the entity's tick count set to 0 and pos to Vec3.ZERO
            return
        }

        // I think maybe I should propagate forward instead of backwards.... but this doesn't happen so frequently
        // so for now, I can get away with it. Tested spawning like 1000 and it was ok.
        val maybePlayer = getChainStarterOrNull(this, mutableListOf(this))
        if (maybePlayer is ServerPlayer) {
            FishcastingAdvancements.tryGrantingAdvancement(
                maybePlayer, // garuanteed by the check...
                FishcastingAdvancements.CHAIN_REACTION
            )
        }

        this.level().explode(this, this.x, this.y, this.z, 1.0f, Level.ExplosionInteraction.MOB)
    }

    override fun canBeCollidedWith(): Boolean {
        return false // super.canBeCollidedWith()
    }

    override fun isPushable(): Boolean {
        return false // super.isPushable()
    }

    override fun doPush(p0: Entity) {
        // super.doPush(p0)
    }

    // visual/sound stuff as I find them
    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.GENERIC_EXPLODE.value() // SoundEvents.EMPTY // super.getDeathSound()
    }

    override fun getHurtSound(p0: DamageSource): SoundEvent? {
        return SoundEvents.EMPTY // super.getHurtSound(p0)
    }

    override fun playHurtSound(p0: DamageSource) {
        // super.playHurtSound(p0)
    }

    override fun playStepSound(p0: BlockPos, p1: BlockState) {
        // super.playStepSound(p0, p1)
    }

    override fun doWaterSplashEffect() {
        // super.doWaterSplashEffect()
    }

    override fun canSpawnSprintParticle(): Boolean {
        return false // super.canSpawnSprintParticle()
    }

    override fun spawnSprintParticle() {
        // super.spawnSprintParticle()
    }

    override fun getFlopSound(): SoundEvent? {
        return SoundEvents.EMPTY // super.getFlopSound()
    }

    override fun getSwimSound(): SoundEvent {
        return SoundEvents.EMPTY // super.getSwimSound()
    }

    override fun getSwimSplashSound(): SoundEvent {
        return SoundEvents.EMPTY // super.getSwimSplashSound()
    }

    override fun getSwimHighSpeedSplashSound(): SoundEvent {
        return SoundEvents.EMPTY // super.getSwimHighSpeedSplashSound()
    }

    override fun travel(movement: Vec3) {
        if (this.isEffectiveAi) {
            this.move(MoverType.SELF, this.deltaMovement)
            this.deltaMovement = this.deltaMovement.scale(0.9)
            this.deltaMovement = this.deltaMovement.add(0.0, -0.0035, 0.0)
        } else super.travel(movement)
    }

    // todo: add goal to attract it to specific blocks (light sources)
    override fun registerGoals() {
        this.goalSelector.addGoal(0, PerhosgataRandomMovementGoal(this))
    }

    // this goal is from Tide's jelly fish
    internal class PerhosgataRandomMovementGoal(private val perhosgataEntity: PerhosgataEntity) : Goal() {
        override fun canUse(): Boolean {
            return true
        }

        override fun tick() {
            val rand = this.perhosgataEntity.getRandom()
            val timeSinceLastAction = this.perhosgataEntity.getNoActionTime()
            if (timeSinceLastAction > 100) {
                this.perhosgataEntity.deltaMovement = Vec3.ZERO
            } else if (rand.nextInt(reducedTickDelay(25)) == 0) {
                val floatBetweenZeroAndTwoPi = rand.nextFloat() * (Math.PI.toFloat() * 2f)
                this.perhosgataEntity.deltaMovement = Vec3(
                    (Mth.cos(floatBetweenZeroAndTwoPi) * 0.16f).toDouble(),
                    (rand.nextFloat() * 0.32f).toDouble(),
                    (Mth.sin(floatBetweenZeroAndTwoPi) * 0.16f).toDouble()
                )
            }
        }
    }
}