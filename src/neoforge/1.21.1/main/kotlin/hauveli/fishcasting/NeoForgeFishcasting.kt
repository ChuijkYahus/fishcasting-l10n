package hauveli.fishcasting

//import hauveli.fishcasting.common.registries.FishcastingEntities
import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.forge.cap.HexCapabilities
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticIotaHolder
import com.li64.tide.registries.TideEntityTypes
import com.li64.tide.registries.TideFish
import com.li64.tide.registries.entities.fish.SmoothSwimmingFish
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.client.NeoForgeFishcastingClient
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.fish.cursed.CursedModel
import hauveli.fishcasting.features.fish.cursed.CursedRenderer
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingItems.ToTideFishingHookEntity
import hauveli.fishcasting.datagen.NeoForgeFishcastingDatagen
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.features.fish.perhosgata.PerhosgataEntity
import hauveli.fishcasting.registry.FishcastingBrainsweepeeIngredients
import hauveli.fishcasting.registry.FishcastingEntities
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.BiConsumer
import java.util.function.Consumer

@Mod(Fishcasting.MODID)
class NeoForgeFishcasting(modBus: IEventBus, container: ModContainer) {

    init {
        // thank you
        // https://github.com/VazkiiMods/Botania/blob/1.18.x/Forge/src/main/java/vazkii/botania/forge/ForgeCommonInitializer.java
        fun <T> bind(
            registry: ResourceKey<out Registry<T>>,
            source: Consumer<BiConsumer<T, ResourceLocation>>
        ) {
            modBus.addListener({ event: RegisterEvent ->
                if (registry == event.registryKey) {
                    source.accept(BiConsumer { t: T, rl: ResourceLocation ->
                        event.register<T>(
                            registry,
                            rl
                        ) { t }
                    })
                }
            })
        }
        // I am not a big fan of how neoforge does events....
        // at least it works now........
        // NeoForge.EVENT_BUS.register(NeoForgeFishcasting)

        bind(HexRegistries.BRAINSWEEPEE_INGREDIENT, FishcastingBrainsweepeeIngredients::registerBrainsweepeeIngredients)

        /*
        // I don't know how much I have to do to register an attribute but I don't like what I've done
        modBus.addListener<EntityAttributeModificationEvent?>(Consumer { event: EntityAttributeModificationEvent? ->
            event!!.add(EntityType.PLAYER, BOBBER_RADIUS as Holder<Attribute?>)
        })
        */
        modBus.apply {
            addListener(NeoForgeFishcastingClient::init)
            addListener(NeoForgeFishcastingDatagen::init)
            addListener(NeoForgeFishcastingServer::init)
            addListener(::registerEntityAttributes)
            addListener(::registerCaps)
            addListener(::registerCreativeModeTabItems)
            addListener(::registerMoonPhaseFishies)
        }

        Fishcasting.init()
    }

    // ugh I couldn't do anything better than this
    fun registerEntityAttributes(event: EntityAttributeCreationEvent) {
        event.put(
            FishcastingEntities.EDIFIED_FISH.value,
            SmoothSwimmingFish.createMobAttributes().build()
        )
        event.put(
            FishcastingEntities.CURSED.value,
            Axolotl.createAttributes().build()
        )
        event.put(
            FishcastingEntities.PERHOSGATA.value,
            SmoothSwimmingFish.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 65.0).build()
        )
        event.put(
            FishcastingEntities.BLESSED.value,
            Villager.createAttributes().build()
        )
    }

    // equivalent for this on fabric is done via a mixin to registering the cap...
    fun registerCaps(event: RegisterCapabilitiesEvent) {
        event.registerEntity<ADIotaHolder?, Void?, TideFishingHook>(
            HexCapabilities.Entity.IOTA,
            TideEntityTypes.FISHING_BOBBER,
            ICapabilityProvider { entity: TideFishingHook, ctx: Void? -> ToTideFishingHookEntity(entity) }
        )
    }

    fun registerCreativeModeTabItems(event: BuildCreativeModeTabContentsEvent) {
        FishcastingItems.registerItemCreativeTab(event, event.tab);
    }


    fun registerMoonPhaseFishies(event: RegisterCapabilitiesEvent ) {

        // hee hee hee...
        // todo:
        // I'll have to fix this in dev 53 and/or dev 54
        val mindsReflection = HexPattern.fromAngleString("qaq", HexDir.NORTH_EAST)
        val compassPurification = HexPattern.fromAngleString("aa", HexDir.EAST)
        val alidadesPurification = HexPattern.fromAngleString("wa", HexDir.NORTH_EAST)
        val archersDistillation = HexPattern.fromAngleString("wqaawdd", HexDir.EAST)
        val sillyIota = ListIota(
            TreeList.from(
                listOf(
                    PatternIota(mindsReflection),
                    PatternIota(compassPurification),
                    PatternIota(mindsReflection),
                    PatternIota(alidadesPurification),
                    PatternIota(archersDistillation),
                )
            )
        )


        event.registerItem(
            HexCapabilities.Item.IOTA,
            {stack, ctx ->
                CapStaticIotaHolder({
                    sillyIota
                }, stack)
            },
            TideFish.MANTA_RAY
        )
    }


    // I decided against it but I'm keeping it here just in case
    /*
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
     */

    companion object {
        internal val container: ModContainer
            get() = ModList.get().getModContainerById(Fishcasting.MODID).get()

        /*
        // I dont think this is even firing...
        @SubscribeEvent
        fun registerAttributeHolder(event: ServerStartedEvent) {
            FishcastingAttributes.registerHolder(event.server.allLevels.first())
        }

         */
    }
}
