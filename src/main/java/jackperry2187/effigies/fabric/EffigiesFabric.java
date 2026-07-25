//? if fabric {
package jackperry2187.effigies.fabric;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.GrimoireTracker;
import jackperry2187.effigies.SpawnPreventionHandler;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.config.ConfigSyncPayload;
import jackperry2187.effigies.config.InitializeConfig;
import jackperry2187.effigies.registry.ModBlockEntities;
import jackperry2187.effigies.registry.ModBlocks;
import jackperry2187.effigies.registry.ModCreativeTabs;
import jackperry2187.effigies.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

public class EffigiesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Effigies.LOGGER.info("Initializing Effigies...");
        
        // Initialize config first - creates file if needed and loads values
        InitializeConfig.generateConfigFile();
        ConfigSettings.initialize();
        
        // Register blocks, items, and other content
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModCreativeTabs.register();
        SpawnPreventionHandler.registerFabric();

        // Register config sync payload and send to players on join
        //? if mc12011 {
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.STREAM_CODEC);
        //?} else {
        /*PayloadTypeRegistry.clientboundPlay().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.STREAM_CODEC);
        *///?}
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayNetworking.send(handler.player, ConfigSyncPayload.fromCurrentConfig());

            ServerPlayer player = handler.player;
            GrimoireTracker tracker = GrimoireTracker.get(server);
            if (ConfigSettings.giveGrimoireOnJoin && !tracker.hasReceivedGrimoire(player.getUUID())) {
                ItemStack grimoire = new ItemStack(ModItems.grimoire());
                if (!player.getInventory().add(grimoire)) {
                    player.drop(grimoire, false);
                }
                tracker.markGrimoireGiven(player.getUUID());
                Effigies.LOGGER.info("Gave grimoire to player {}", player.getName().getString());
            }
        });

        // Reload config from file on server start to restore local values after multiplayer
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigSettings.reload();
            PikeBlockEntity.validateMappings();
        });
        
        Effigies.LOGGER.info("Effigies initialized successfully!");
    }
}
//?}
