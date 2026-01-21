//? if fabric {
package jackperry2187.effigies.fabric;

import jackperry2187.effigies.SpawnPreventionHandler;
import jackperry2187.effigies.registry.ModBlockEntities;
import jackperry2187.effigies.registry.ModBlocks;
import jackperry2187.effigies.registry.ModCreativeTabs;
import jackperry2187.effigies.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class EffigiesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModCreativeTabs.register();
        SpawnPreventionHandler.registerFabric();
    }
}
//?}
