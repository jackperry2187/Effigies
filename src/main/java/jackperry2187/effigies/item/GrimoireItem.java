package jackperry2187.effigies.item;

import jackperry2187.effigies.client.GrimoireScreenOpener;

//? if fabric {
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
//?} else {
/*import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
*///?}

public class GrimoireItem extends Item {
    private static final int NAME_COLOR = 0x6A0DAD;

    //? if fabric {
    public GrimoireItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(NAME_COLOR));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            GrimoireScreenOpener.open();
        }
        return ActionResult.SUCCESS;
    }
    //?} else {
    /*public GrimoireItem(Item.Properties properties) {
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
    *///?}
}
