//? if fabric {
package jackperry2187.effigies.fabric;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.SpawnPreventionHandler;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.config.InitializeConfig;
import jackperry2187.effigies.registry.ModBlockEntities;
import jackperry2187.effigies.registry.ModBlocks;
import jackperry2187.effigies.registry.ModCreativeTabs;
import jackperry2187.effigies.registry.ModItems;
import net.fabricmc.api.ModInitializer;

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
        
        Effigies.LOGGER.info("Effigies initialized successfully!");
    }
}
//?}
