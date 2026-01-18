//? if fabric {
package jackperry2187.effigies.fabric;

import net.fabricmc.api.ClientModInitializer;

public class EffigiesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Render types are handled via data-driven models in 1.21+
        // The block models specify "render_type": "cutout" for transparency
    }
}
//?}
