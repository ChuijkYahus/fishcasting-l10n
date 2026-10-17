package hauveli.fishcasting.features.fish.goop

import com.li64.tide.Tide
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.item.TideItemData
import com.li64.tide.registries.entities.fish.AmphibiousFish
import hauveli.fishcasting.registry.FishcastingAdvancements.tryGrantingAdvancement
import hauveli.fishcasting.registry.FishcastingAdvancements
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.Bucketable
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import java.security.MessageDigest
import java.util.function.Function


// melted axolotl fish
class GoopEntity(
    entityType: EntityType<out AmphibiousFish>,
    level: Level
) : AmphibiousFish(entityType, level), Bucketable, FishLengthHolder {
    private val bucketItem: Item
    private var length: Double

    override fun aiStep() {
        // this.setOnGround(false) // no flopping please, does this mess with friction?
        super.aiStep()
    }

    override fun getSpeed(): Float {
        return 0.1f * super.getSpeed()
    }

    override fun getHeadRotSpeed(): Int {
        return 1
    }

    override fun getBucketItemStack(): ItemStack {
        return bucketItem.defaultInstance
    }

    override fun getPickupSound(): SoundEvent {
        return SoundEvents.SLIME_SQUISH_SMALL
    }

    // these next 4 are so its goes ouch all the time
    override fun getMaxAirSupply(): Int {
        return 0
    }

    // todo: CHECK THIS in create and vanilla
    override fun getPassengerRidingPosition(p0: Entity): Vec3 {
        return super.getPassengerRidingPosition(p0).add(Vec3(0.0,1.0,0.0))
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (this.level().isClientSide) {
            return super.hurt(source, amount)
        }

        this.gameEvent(GameEvent.ENTITY_DAMAGE)
        return super.hurt(source, 0f)
    }

    init {
        this.isNoGravity = false
        this.setCanPickUpLoot(false)
        //this.setInvulnerable(true);
        this.speed = 0.1f
        // this.attributes.getInstance(Attributes.STEP_HEIGHT)?.baseValue = 0.1

        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/AbstractTideFish.java#L46
        val key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        val fishItem = BuiltInRegistries.ITEM.getOptional(key).orElseThrow()
        this.bucketItem = BuiltInRegistries.ITEM.getOptional(key.withSuffix("_bucket")).orElseThrow()
        // sure whatever
        this.length = FishData.get(fishItem).map(Function {
                data: FishData ->
            data.getRandomLength(getRandom())
        }).orElse(0.0) ?: 0.0
    }

    override fun `tide$getLength`(): Double {
        return this.length
    }

    override fun `tide$setLength`(length: Double) {
        this.length = length
    }

    override fun isBaby(): Boolean {
        return false
    }
}