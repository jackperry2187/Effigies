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
                int radius = pikeItem.getTier().chunkRadius();
                if (radius < 0) {
                    lines.add(Text.translatable("item.effigies.pike.tooltip.disabled").formatted(Formatting.GRAY));
                } else {
                    lines.add(Text.translatable("item.effigies.pike.tooltip.prevents_spawns").formatted(Formatting.GRAY));
                    if (radius == 0) {
                        lines.add(Text.translatable("item.effigies.pike.tooltip.chunk_only").formatted(Formatting.GRAY));
                    } else {
                        lines.add(Text.translatable("item.effigies.pike.tooltip.chunk_radius", radius).formatted(Formatting.GRAY));
                    }
                }
                lines.add(Text.translatable("item.effigies.pike.tooltip.activate").formatted(Formatting.DARK_GRAY));
            }
        });
    }
}
//?}
