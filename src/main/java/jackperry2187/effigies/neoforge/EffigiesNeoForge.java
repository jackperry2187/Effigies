//? if neoforge {
/*package jackperry2187.effigies.neoforge;

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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.gametest.framework.GameTestServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Effigies.MOD_ID)
public class EffigiesNeoForge {
    public EffigiesNeoForge(IEventBus modBus) {
        Effigies.LOGGER.info("Initializing Effigies...");
        
        // Initialize config first - creates file if needed and loads values
        InitializeConfig.generateConfigFile();
        ConfigSettings.initialize();
        
        // Register blocks, items, and other content
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModCreativeTabs.register(modBus);
        registerGameTests(modBus);

        NeoForge.EVENT_BUS.register(SpawnPreventionHandler.class);

        if (FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            modBus.addListener(EffigiesClientNeoForge::onRegisterRenderers);
        }

        modBus.addListener(EffigiesNeoForge::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(EffigiesNeoForge::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(EffigiesNeoForge::onServerStarting);
        
        Effigies.LOGGER.info("Effigies initialized successfully!");
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(Effigies.MOD_ID);
        registrar.playToClient(
            ConfigSyncPayload.TYPE,
            ConfigSyncPayload.STREAM_CODEC,
            (payload, context) -> context.enqueueWork(() -> ConfigSettings.applySyncedValues(payload))
        );
    }

    private static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!(player.level().getServer() instanceof GameTestServer)) {
                PacketDistributor.sendToPlayer(player, ConfigSyncPayload.fromCurrentConfig());
            }

            MinecraftServer server = player.level().getServer();
            GrimoireTracker.giveGrimoireIfNeeded(server, player);
        }
    }

    private static void onServerStarting(ServerStartingEvent event) {
        ConfigSettings.reload();
        PikeBlockEntity.validateMappings();
    }

    private static void registerGameTests(IEventBus modBus) {
        try {
            Class<?> testBootstrap = Class.forName("jackperry2187.effigies.gametest.EffigiesNeoForgeGameTests");
            testBootstrap.getMethod("register", IEventBus.class).invoke(null, modBus);
        } catch (ClassNotFoundException ignored) {
            // The test source set is absent from production runs.
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to register Effigies GameTests", exception);
        }
    }
}
*///?}
