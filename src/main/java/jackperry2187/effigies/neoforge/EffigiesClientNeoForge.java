//? if neoforge {
/*package jackperry2187.effigies.neoforge;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.item.PikeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Effigies.MOD_ID, value = Dist.CLIENT)
public class EffigiesClientNeoForge {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof PikeItem pikeItem) {
            // Line 1: "Prevents mob spawns in"
            event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.prevents_spawns").withStyle(ChatFormatting.GRAY));

            // Line 2: Range description
            if (pikeItem.getTier().chunkRadius() == 0) {
                // Wooden pike - just the chunk it's in
                event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.chunk_only").withStyle(ChatFormatting.GRAY));
            } else {
                // Other pikes - chunk + chunk radius
                event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.chunk_radius", pikeItem.getTier().chunkRadius()).withStyle(ChatFormatting.GRAY));
            }

            // Line 3: Activation instruction
            event.getToolTip().add(Component.translatable("item.effigies.pike.tooltip.activate").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
*///?}
