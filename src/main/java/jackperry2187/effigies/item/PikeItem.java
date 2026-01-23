package jackperry2187.effigies.item;

import jackperry2187.effigies.PikeTier;

//? if fabric {
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
//?} else {
/*import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
*///?}

public class PikeItem extends BlockItem {
    private final PikeTier tier;

    //? if fabric {
    public PikeItem(Block block, Item.Settings settings, PikeTier tier) {
        super(block, settings);
        this.tier = tier;
    }
    //?} else {
    /*public PikeItem(Block block, Item.Properties properties, PikeTier tier) {
        super(block, properties);
        this.tier = tier;
    }
    *///?}

    public PikeTier getTier() {
        return tier;
    }
}
