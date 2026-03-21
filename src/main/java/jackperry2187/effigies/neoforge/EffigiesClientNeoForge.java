//? if neoforge {
/*package jackperry2187.effigies.neoforge;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.client.PikeHeadBlockEntityRenderer;
import jackperry2187.effigies.item.AntiSpearItem;
import jackperry2187.effigies.item.PikeItem;
import jackperry2187.effigies.registry.ModBlocks;
import jackperry2187.effigies.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Effigies.MOD_ID, value = Dist.CLIENT)
public class EffigiesClientNeoForge {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof PikeItem pikeItem) {
            int radius = pikeItem.getTier().chunkRadius();
            if (radius < 0) {
                event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.disabled").withStyle(ChatFormatting.GRAY));
            } else {
                event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.prevents_spawns").withStyle(ChatFormatting.GRAY));
                if (radius == 0) {
                    event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.chunk_only").withStyle(ChatFormatting.GRAY));
                } else {
                    event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.chunk_radius", radius).withStyle(ChatFormatting.GRAY));
                }
            }
            event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.activate").withStyle(ChatFormatting.DARK_GRAY));
        }
        if (event.getItemStack().getItem() instanceof AntiSpearItem) {
            event.getToolTip().add(Component.translatable("item.effigies.anti_spear.tooltip").withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (event.getItemStack().is(ModBlocks.antiPike().asItem())) {
            event.getToolTip().add(Component.translatable("item.effigies.anti_pike.tooltip.spawns").withStyle(ChatFormatting.DARK_PURPLE));
            event.getToolTip().add(Component.translatable("item.effigies.anti_pike.tooltip.bypass").withStyle(ChatFormatting.RED));
            event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.activate").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.pikeHead(), PikeHeadBlockEntityRenderer::new);
    }
}
*///?}
