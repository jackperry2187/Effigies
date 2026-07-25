package jackperry2187.effigies.block;

import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

//? if fabric {
//?} else {
/*import jackperry2187.effigies.Effigies;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
*///?}

public class PikeHeadBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

    //? if fabric {
    public PikeHeadBlock(Identifier id) {
        super(BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, id))
            .strength(1.0f)
            .noOcclusion()
            .sound(SoundType.STONE)
            .pushReaction(PushReaction.DESTROY));
    }
    //?} else {
    /*public PikeHeadBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    public static BlockBehaviour.Properties neoForgeProperties() {
        return BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Effigies.MOD_ID, "pike_head")))
            .strength(1.0f)
            .noOcclusion()
            .sound(SoundType.STONE)
            .pushReaction(PushReaction.DESTROY);
    }
    *///?}

    //? if fabric {
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    //?} else {
    /*@Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PikeHeadBlockEntity pikeHead) {
            BlockState storedState = pikeHead.getStoredBlockState();
            if (storedState != null) {
                return storedState.getSoundType();
            }
        }
        return super.getSoundType(state, level, pos, entity);
    }
    *///?}

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PikeHeadBlockEntity pikeHead) {
                ItemStack drop = pikeHead.getStoredItemStack();
                if (!drop.isEmpty()) {
                    Block.popResource(level, pos, drop);
                }
                pikeHead.setStoredHead("", 0);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public PikeHeadBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PikeHeadBlockEntity(pos, state);
    }
}
