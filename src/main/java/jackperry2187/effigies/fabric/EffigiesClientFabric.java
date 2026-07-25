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
//? if mc12011 {
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class EffigiesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //? if mc12011 {
        BlockEntityRenderers.register(ModBlockEntities.pikeHead(), PikeHeadBlockEntityRenderer::new);
        //?} else {
        /*BlockEntityRendererRegistry.register(ModBlockEntities.pikeHead(), PikeHeadBlockEntityRenderer::new);
        *///?}

        // Register config sync receiver
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE, (payload, context) -> {
            ConfigSettings.applySyncedValues(payload);
        });

        // Register pike item tooltips
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof PikeItem pikeItem) {
                int radius = pikeItem.getTier().chunkRadius();
                if (radius < 0) {
                    lines.add(Component.translatable("item.effigies.pike.tooltip.disabled").withStyle(ChatFormatting.GRAY));
                } else {
                    lines.add(Component.translatable("item.effigies.pike.tooltip.prevents_spawns").withStyle(ChatFormatting.GRAY));
                    if (radius == 0) {
                        lines.add(Component.translatable("item.effigies.pike.tooltip.chunk_only").withStyle(ChatFormatting.GRAY));
                    } else {
                        lines.add(Component.translatable("item.effigies.pike.tooltip.chunk_radius", radius).withStyle(ChatFormatting.GRAY));
                    }
                }
                lines.add(Component.translatable("item.effigies.pike.tooltip.activate").withStyle(ChatFormatting.DARK_GRAY));
            }
            if (stack.getItem() instanceof AntiSpearItem) {
                lines.add(Component.translatable("item.effigies.anti_spear.tooltip").withStyle(ChatFormatting.DARK_PURPLE));
            }
            if (stack.is(ModBlocks.antiPike().asItem())) {
                lines.add(Component.translatable("item.effigies.anti_pike.tooltip.spawns").withStyle(ChatFormatting.DARK_PURPLE));
                lines.add(Component.translatable("item.effigies.anti_pike.tooltip.bypass").withStyle(ChatFormatting.RED));
                lines.add(Component.translatable("item.effigies.pike.tooltip.activate").withStyle(ChatFormatting.DARK_GRAY));
            }
        });
    }
}
//?}
