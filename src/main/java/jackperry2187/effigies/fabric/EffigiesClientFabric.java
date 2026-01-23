//? if fabric {
package jackperry2187.effigies.fabric;

import jackperry2187.effigies.item.PikeItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EffigiesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Render types are handled via data-driven models in 1.21+
        // The block models specify "render_type": "cutout" for transparency

        // Register pike item tooltips
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof PikeItem pikeItem) {
                // Line 1: "Prevents mob spawns in"
                lines.add(Text.translatable("item.effigies.pike.tooltip.prevents_spawns").formatted(Formatting.GRAY));

                // Line 2: Range description
                if (pikeItem.getTier().chunkRadius() == 0) {
                    // Wooden pike - just the chunk it's in
                    lines.add(Text.translatable("item.effigies.pike.tooltip.chunk_only").formatted(Formatting.GRAY));
                } else {
                    // Other pikes - chunk + chunk radius
                    lines.add(Text.translatable("item.effigies.pike.tooltip.chunk_radius", pikeItem.getTier().chunkRadius()).formatted(Formatting.GRAY));
                }

                // Line 3: Activation instruction
                lines.add(Text.translatable("item.effigies.pike.tooltip.activate").formatted(Formatting.DARK_GRAY));
            }
        });
    }
}
//?}
