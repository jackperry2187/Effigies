package jackperry2187.effigies.gametest;

import jackperry2187.effigies.PikeRegistry;
import jackperry2187.effigies.PikeTier;

import java.lang.reflect.Method;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;

public final class EffigiesFabricGameTests implements CustomTestMethodInvoker {
    @GameTest(environment = "effigies:isolated/registry_and_default_mappings", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void registryAndDefaultMappings(GameTestHelper helper) {
        EffigiesGameTests.registryAndDefaultMappings(helper);
    }

    @GameTest(environment = "effigies:isolated/data_resources_load", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void dataResourcesLoad(GameTestHelper helper) {
        EffigiesGameTests.dataResourcesLoad(helper);
    }

    @GameTest(environment = "effigies:isolated/data_resources_behave_as_declared", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void dataResourcesBehaveAsDeclared(GameTestHelper helper) {
        EffigiesGameTests.dataResourcesBehaveAsDeclared(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_registry_radius_and_entity_matching", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeRegistryRadiusAndEntityMatching(GameTestHelper helper) {
        EffigiesGameTests.pikeRegistryRadiusAndEntityMatching(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_registry_covers_all_tiers_and_lifecycle", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeRegistryCoversAllTiersAndLifecycle(GameTestHelper helper) {
        EffigiesGameTests.pikeRegistryCoversAllTiersAndLifecycle(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_chunk_load_ignores_inactive_and_invalid_heads", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeChunkLoadIgnoresInactiveAndInvalidHeads(GameTestHelper helper) {
        EffigiesGameTests.pikeChunkLoadIgnoresInactiveAndInvalidHeads(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_registry_honors_disabled_radius", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeRegistryHonorsDisabledRadius(GameTestHelper helper) {
        EffigiesGameTests.pikeRegistryHonorsDisabledRadius(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_activation_registers_stored_head", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeActivationRegistersStoredHead(GameTestHelper helper) {
        EffigiesGameTests.pikeActivationRegistersStoredHead(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_head_removal_deactivates_and_drops_stored_item", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeHeadRemovalDeactivatesAndDropsStoredItem(GameTestHelper helper) {
        EffigiesGameTests.pikeHeadRemovalDeactivatesAndDropsStoredItem(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_placement_and_breaking_drops_head", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikePlacementAndBreakingDropsHead(GameTestHelper helper) {
        EffigiesGameTests.pikePlacementAndBreakingDropsHead(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_placement_and_breaking_drops_head", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikePlacementAndBreakingDropsHead(GameTestHelper helper) {
        EffigiesGameTests.antiPikePlacementAndBreakingDropsHead(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_head_breaking_drops_stored_item", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeHeadBreakingDropsStoredItem(GameTestHelper helper) {
        EffigiesGameTests.pikeHeadBreakingDropsStoredItem(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_chunk_unload_and_reload_rehydrates_registry", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeChunkUnloadAndReloadRehydratesRegistry(GameTestHelper helper) {
        EffigiesGameTests.pikeChunkUnloadAndReloadRehydratesRegistry(helper);
    }

    @GameTest(environment = "effigies:isolated/fabric_entity_load_blocks_matching_mob_only", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void fabricEntityLoadBlocksMatchingMobOnly(GameTestHelper helper) {
        synchronized (EffigiesGameTests.TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos pikePos = helper.absolutePos(new BlockPos(2, 1, 2));
            Entity matching = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("skeleton"))
                .create(level, EntitySpawnReason.EVENT);
            Entity allowed = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("zombie"))
                .create(level, EntitySpawnReason.EVENT);
            matching.tickCount = 0;
            allowed.tickCount = 0;
            matching.setPos(pikePos.getX() + 1.5, pikePos.getY(), pikePos.getZ() + 1.5);
            allowed.setPos(pikePos.getX() + 1.5, pikePos.getY(), pikePos.getZ() + 1.5);
            PikeRegistry.registerPike(level, pikePos, PikeTier.WOODEN, matching.getType());
            try {
                level.addFreshEntity(matching);
                level.addFreshEntity(allowed);
            } finally {
                PikeRegistry.unregisterPike(level, pikePos);
            }
            helper.runAfterDelay(1, () -> {
                try {
                    helper.assertTrue(level.getEntity(matching.getUUID()) == null,
                        "Fabric entity-load handling should remove a matching mob");
                    helper.assertTrue(level.getEntity(allowed.getUUID()) == allowed,
                        "Fabric entity-load handling should allow a nonmatching mob");
                } finally {
                    matching.discard();
                    allowed.discard();
                }
                helper.succeed();
            });
            return;
        }
    }

    @GameTest(environment = "effigies:isolated/dimension_whitelist_accepts_only_configured_dimension", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void dimensionWhitelistAcceptsOnlyConfiguredDimension(GameTestHelper helper) {
        EffigiesGameTests.dimensionWhitelistAcceptsOnlyConfiguredDimension(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_spawns_with_nearby_player_and_bypasses_pike", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeSpawnsWithNearbyPlayerAndBypassesPike(GameTestHelper helper) {
        EffigiesGameTests.antiPikeSpawnsWithNearbyPlayerAndBypassesPike(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_head_removal_deactivates_and_drops_stored_item", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeHeadRemovalDeactivatesAndDropsStoredItem(GameTestHelper helper) {
        EffigiesGameTests.antiPikeHeadRemovalDeactivatesAndDropsStoredItem(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_honors_activation_range_and_spawn_delay", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeHonorsActivationRangeAndSpawnDelay(GameTestHelper helper) {
        EffigiesGameTests.antiPikeHonorsActivationRangeAndSpawnDelay(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_honors_nearby_entity_limit", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeHonorsNearbyEntityLimit(GameTestHelper helper) {
        EffigiesGameTests.antiPikeHonorsNearbyEntityLimit(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_honors_spawn_range", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeHonorsSpawnRange(GameTestHelper helper) {
        EffigiesGameTests.antiPikeHonorsSpawnRange(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_skips_inactive_and_missing_head", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeSkipsInactiveAndMissingHead(GameTestHelper helper) {
        EffigiesGameTests.antiPikeSkipsInactiveAndMissingHead(helper);
    }

    @GameTest(environment = "effigies:isolated/anti_pike_rejects_invalid_spawn_locations_and_equal_delay", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void antiPikeRejectsInvalidSpawnLocationsAndEqualDelay(GameTestHelper helper) {
        EffigiesGameTests.antiPikeRejectsInvalidSpawnLocationsAndEqualDelay(helper);
    }

    @GameTest(environment = "effigies:isolated/pike_head_stores_and_resolves_original_block", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void pikeHeadStoresAndResolvesOriginalBlock(GameTestHelper helper) {
        EffigiesGameTests.pikeHeadStoresAndResolvesOriginalBlock(helper);
    }

    @GameTest(environment = "effigies:isolated/block_entity_state_round_trips_through_persistence", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void blockEntityStateRoundTripsThroughPersistence(GameTestHelper helper) {
        EffigiesGameTests.blockEntityStateRoundTripsThroughPersistence(helper);
    }

    @GameTest(environment = "effigies:isolated/config_sync_applies_and_restores_values", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void configSyncAppliesAndRestoresValues(GameTestHelper helper) {
        EffigiesGameTests.configSyncAppliesAndRestoresValues(helper);
    }

    @GameTest(environment = "effigies:isolated/config_sync_round_trips_through_codec", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void configSyncRoundTripsThroughCodec(GameTestHelper helper) {
        EffigiesGameTests.configSyncRoundTripsThroughCodec(helper);
    }

    @GameTest(environment = "effigies:isolated/config_file_loads_new_settings_and_regenerates_on_version_mismatch", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void configFileLoadsNewSettingsAndRegeneratesOnVersionMismatch(GameTestHelper helper) {
        EffigiesGameTests.configFileLoadsNewSettingsAndRegeneratesOnVersionMismatch(helper);
    }

    @GameTest(environment = "effigies:isolated/grimoire_tracker_persists_and_drops_when_inventory_is_full", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void grimoireTrackerPersistsAndDropsWhenInventoryIsFull(GameTestHelper helper) {
        EffigiesGameTests.grimoireTrackerPersistsAndDropsWhenInventoryIsFull(helper);
    }

    @GameTest(environment = "effigies:isolated/grimoire_on_join_honors_config_and_tracks_first_join", structure = "fabric-gametest-api-v1:empty", maxTicks = 80)
    public void grimoireOnJoinHonorsConfigAndTracksFirstJoin(GameTestHelper helper) {
        EffigiesGameTests.grimoireOnJoinHonorsConfigAndTracksFirstJoin(helper);
    }

    private static Identifier minecraftId(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method) throws ReflectiveOperationException {
        EffigiesGameTests.resetSharedTestState();
        method.invoke(this, context);
    }
}
