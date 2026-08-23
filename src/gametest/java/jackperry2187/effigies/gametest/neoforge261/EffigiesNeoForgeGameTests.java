package jackperry2187.effigies.gametest;

import java.util.function.Consumer;

import com.mojang.serialization.MapCodec;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.PikeRegistry;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.SpawnPreventionHandler;

import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EffigiesNeoForgeGameTests {
    private static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
        DeferredRegister.create(Registries.TEST_FUNCTION, Effigies.MOD_ID);

    private EffigiesNeoForgeGameTests() {
    }

    public static void register(IEventBus modBus) {
        registerFunction("registry_and_default_mappings", EffigiesGameTests::registryAndDefaultMappings);
        registerFunction("data_resources_load", EffigiesGameTests::dataResourcesLoad);
        registerFunction("data_resources_behave_as_declared", EffigiesGameTests::dataResourcesBehaveAsDeclared);
        registerFunction("pike_registry_radius_and_entity_matching", EffigiesGameTests::pikeRegistryRadiusAndEntityMatching);
        registerFunction("pike_registry_covers_all_tiers_and_lifecycle", EffigiesGameTests::pikeRegistryCoversAllTiersAndLifecycle);
        registerFunction("pike_chunk_load_ignores_inactive_and_invalid_heads", EffigiesGameTests::pikeChunkLoadIgnoresInactiveAndInvalidHeads);
        registerFunction("pike_registry_honors_disabled_radius", EffigiesGameTests::pikeRegistryHonorsDisabledRadius);
        registerFunction("pike_activation_registers_stored_head", EffigiesGameTests::pikeActivationRegistersStoredHead);
        registerFunction("pike_head_removal_deactivates_and_drops_stored_item", EffigiesGameTests::pikeHeadRemovalDeactivatesAndDropsStoredItem);
        registerFunction("pike_placement_and_breaking_drops_head", EffigiesGameTests::pikePlacementAndBreakingDropsHead);
        registerFunction("anti_pike_placement_and_breaking_drops_head", EffigiesGameTests::antiPikePlacementAndBreakingDropsHead);
        registerFunction("pike_head_breaking_drops_stored_item", EffigiesGameTests::pikeHeadBreakingDropsStoredItem);
        registerFunction("pike_chunk_unload_and_reload_rehydrates_registry", EffigiesGameTests::pikeChunkUnloadAndReloadRehydratesRegistry);
        registerFunction("spawn_prevention_event_cancels_matching_mob", EffigiesNeoForgeGameTests::spawnPreventionEventCancelsMatchingMob);
        registerFunction("dimension_whitelist_accepts_only_configured_dimension", EffigiesGameTests::dimensionWhitelistAcceptsOnlyConfiguredDimension);
        registerFunction("anti_pike_spawns_with_nearby_player_and_bypasses_pike", EffigiesGameTests::antiPikeSpawnsWithNearbyPlayerAndBypassesPike);
        registerFunction("anti_pike_head_removal_deactivates_and_drops_stored_item", EffigiesGameTests::antiPikeHeadRemovalDeactivatesAndDropsStoredItem);
        registerFunction("anti_pike_honors_activation_range_and_spawn_delay", EffigiesGameTests::antiPikeHonorsActivationRangeAndSpawnDelay);
        registerFunction("anti_pike_honors_nearby_entity_limit", EffigiesGameTests::antiPikeHonorsNearbyEntityLimit);
        registerFunction("anti_pike_honors_spawn_range", EffigiesGameTests::antiPikeHonorsSpawnRange);
        registerFunction("anti_pike_skips_inactive_and_missing_head", EffigiesGameTests::antiPikeSkipsInactiveAndMissingHead);
        registerFunction("anti_pike_rejects_invalid_spawn_locations_and_equal_delay", EffigiesGameTests::antiPikeRejectsInvalidSpawnLocationsAndEqualDelay);
        registerFunction("pike_head_stores_and_resolves_original_block", EffigiesGameTests::pikeHeadStoresAndResolvesOriginalBlock);
        registerFunction("block_entity_state_round_trips_through_persistence", EffigiesGameTests::blockEntityStateRoundTripsThroughPersistence);
        registerFunction("config_sync_applies_and_restores_values", EffigiesGameTests::configSyncAppliesAndRestoresValues);
        registerFunction("config_sync_round_trips_through_codec", EffigiesGameTests::configSyncRoundTripsThroughCodec);
        registerFunction("config_file_loads_new_settings_and_regenerates_on_version_mismatch", EffigiesGameTests::configFileLoadsNewSettingsAndRegeneratesOnVersionMismatch);
        registerFunction("grimoire_on_join_honors_config_and_tracks_first_join", EffigiesGameTests::grimoireOnJoinHonorsConfigAndTracksFirstJoin);
        registerFunction("grimoire_tracker_persists_and_drops_when_inventory_is_full", EffigiesGameTests::grimoireTrackerPersistsAndDropsWhenInventoryIsFull);
        TEST_FUNCTIONS.register(modBus);
        modBus.addListener(EffigiesNeoForgeGameTests::registerTests);
    }

    public static void registerTests(RegisterGameTestsEvent event) {
        registerTest(event, "registry_and_default_mappings");
        registerTest(event, "data_resources_load");
        registerTest(event, "data_resources_behave_as_declared");
        registerTest(event, "pike_registry_radius_and_entity_matching");
        registerTest(event, "pike_registry_covers_all_tiers_and_lifecycle");
        registerTest(event, "pike_chunk_load_ignores_inactive_and_invalid_heads");
        registerTest(event, "pike_registry_honors_disabled_radius");
        registerTest(event, "pike_activation_registers_stored_head");
        registerTest(event, "pike_head_removal_deactivates_and_drops_stored_item");
        registerTest(event, "pike_placement_and_breaking_drops_head");
        registerTest(event, "anti_pike_placement_and_breaking_drops_head");
        registerTest(event, "pike_head_breaking_drops_stored_item");
        registerTest(event, "pike_chunk_unload_and_reload_rehydrates_registry");
        registerTest(event, "spawn_prevention_event_cancels_matching_mob");
        registerTest(event, "dimension_whitelist_accepts_only_configured_dimension");
        registerTest(event, "anti_pike_spawns_with_nearby_player_and_bypasses_pike");
        registerTest(event, "anti_pike_head_removal_deactivates_and_drops_stored_item");
        registerTest(event, "anti_pike_honors_activation_range_and_spawn_delay");
        registerTest(event, "anti_pike_honors_nearby_entity_limit");
        registerTest(event, "anti_pike_honors_spawn_range");
        registerTest(event, "anti_pike_skips_inactive_and_missing_head");
        registerTest(event, "anti_pike_rejects_invalid_spawn_locations_and_equal_delay");
        registerTest(event, "pike_head_stores_and_resolves_original_block");
        registerTest(event, "block_entity_state_round_trips_through_persistence");
        registerTest(event, "config_sync_applies_and_restores_values");
        registerTest(event, "config_sync_round_trips_through_codec");
        registerTest(event, "config_file_loads_new_settings_and_regenerates_on_version_mismatch");
        registerTest(event, "grimoire_on_join_honors_config_and_tracks_first_join");
        registerTest(event, "grimoire_tracker_persists_and_drops_when_inventory_is_full");
    }

    private static void registerFunction(String name, Consumer<GameTestHelper> function) {
        TEST_FUNCTIONS.register("gametest/" + name, () -> helper -> {
            EffigiesGameTests.resetSharedTestState();
            function.accept(helper);
        });
    }

    public static void spawnPreventionEventCancelsMatchingMob(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pikePos = helper.absolutePos(new BlockPos(2, 1, 2));
        Entity entity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "skeleton"))
            .create(level, EntitySpawnReason.EVENT);
        Entity allowedEntity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "zombie"))
            .create(level, EntitySpawnReason.EVENT);
        entity.setPos(pikePos.getX() + 1.5, pikePos.getY(), pikePos.getZ() + 1.5);
        allowedEntity.setPos(pikePos.getX() + 1.5, pikePos.getY(), pikePos.getZ() + 1.5);
        PikeRegistry.registerPike(level, pikePos, PikeTier.STONE, entity.getType());
        try {
            EntityJoinLevelEvent event = new EntityJoinLevelEvent(entity, level, false);
            NeoForge.EVENT_BUS.post(event);
            helper.assertTrue(event.isCanceled(), "NeoForge should cancel a matching mob join event");
            EntityJoinLevelEvent allowedEvent = new EntityJoinLevelEvent(allowedEntity, level, false);
            NeoForge.EVENT_BUS.post(allowedEvent);
            helper.assertTrue(!allowedEvent.isCanceled(), "NeoForge should allow a nonmatching mob join event");
        } finally {
            PikeRegistry.unregisterPike(level, pikePos);
        }
        helper.succeed();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerTest(
        RegisterGameTestsEvent event,
        String name
    ) {
        Identifier id = Effigies.id("gametest/" + name);
        ResourceKey<Consumer<GameTestHelper>> functionKey = ResourceKey.create(Registries.TEST_FUNCTION, id);
        Holder isolatedEnvironment = event.registerEnvironment(
            Effigies.id("gametest/" + name + "_isolated"),
            new NoOpEnvironment()
        );

        TestData testData = new TestData(isolatedEnvironment, Identifier.fromNamespaceAndPath("minecraft", "empty"), 80, 0, true, Rotation.NONE);
        event.registerTest(id, new FunctionGameTestInstance(functionKey, testData));
    }

    private static final class NoOpEnvironment implements TestEnvironmentDefinition<Void> {
        @Override
        public Void setup(ServerLevel level) {
            return null;
        }

        @Override
        public void teardown(ServerLevel level, Void state) {
        }

        @Override
        public MapCodec<? extends TestEnvironmentDefinition<Void>> codec() {
            return MapCodec.unit(this);
        }
    }
}
