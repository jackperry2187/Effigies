package jackperry2187.effigies.item;

import jackperry2187.effigies.client.GrimoireScreenOpener;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GrimoireItem extends Item {
    private static final int NAME_COLOR = 0x6A0DAD;

    public GrimoireItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(NAME_COLOR));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            GrimoireScreenOpener.open();
        }
        return InteractionResult.SUCCESS;
    }
}
