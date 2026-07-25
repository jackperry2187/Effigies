package jackperry2187.effigies.item;

import jackperry2187.effigies.PikeTier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PikeItem extends BlockItem {
    private final PikeTier tier;

    public PikeItem(Block block, Item.Properties properties, PikeTier tier) {
        super(block, properties);
        this.tier = tier;
    }

    public PikeTier getTier() {
        return tier;
    }
}
