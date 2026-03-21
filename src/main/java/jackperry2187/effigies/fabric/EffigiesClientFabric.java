//? if fabric {
package jackperry2187.effigies.fabric;

import jackperry2187.effigies.client.PikeHeadBlockEntityRenderer;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.config.ConfigSyncPayload;
import jackperry2187.effigies.item.AntiSpearItem;
import jackperry2187.effigies.item.PikeItem;
import jackperry2187.effigies.registry.ModBlocks;
import jackperry2187.effigies.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EffigiesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.pikeHead(), PikeHeadBlockEntityRenderer::new);

        // Register config sync receiver
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            ConfigSettings.applySyncedValues(payload);
        });

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
            if (stack.getItem() instanceof AntiSpearItem) {
                lines.add(Text.translatable("item.effigies.anti_spear.tooltip").formatted(Formatting.DARK_PURPLE));
            }
            if (stack.isOf(ModBlocks.antiPike().asItem())) {
                lines.add(Text.translatable("item.effigies.anti_pike.tooltip.spawns").formatted(Formatting.DARK_PURPLE));
                lines.add(Text.translatable("item.effigies.anti_pike.tooltip.bypass").formatted(Formatting.RED));
                lines.add(Text.translatable("item.effigies.pike.tooltip.activate").formatted(Formatting.DARK_GRAY));
            }
        });
    }
}
//?}
