package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
*///?}

public class PikeBlockEntity extends BlockEntity {
    public PikeBlockEntity(BlockPos pos, BlockState state) {
        //? if fabric {
        super(ModBlockEntities.pike(), pos, state);
        //?} else {
        /*super(ModBlockEntities.pike(), pos, state);
        *///?}
    }

    public static boolean isValidHeadItem(ItemStack stack) {
        return getHeadTypeFromItem(stack) != null;
    }

    public static boolean isValidHeadBlock(BlockState state) {
        return getHeadTypeFromBlockState(state) != null;
    }

    @Nullable
    public static BlockState getHeadBlockStateForItem(ItemStack stack) {
        //? if fabric {
        if (stack.isOf(Items.SKELETON_SKULL)) {
            return Blocks.SKELETON_SKULL.getDefaultState();
        }
        if (stack.isOf(Items.WITHER_SKELETON_SKULL)) {
            return Blocks.WITHER_SKELETON_SKULL.getDefaultState();
        }
        if (stack.isOf(Items.ZOMBIE_HEAD)) {
            return Blocks.ZOMBIE_HEAD.getDefaultState();
        }
        if (stack.isOf(Items.CREEPER_HEAD)) {
            return Blocks.CREEPER_HEAD.getDefaultState();
        }
        if (stack.isOf(Items.PIGLIN_HEAD)) {
            return Blocks.PIGLIN_HEAD.getDefaultState();
        }
        //?} else {
        /*if (stack.is(Items.SKELETON_SKULL)) {
            return Blocks.SKELETON_SKULL.defaultBlockState();
        }
        if (stack.is(Items.WITHER_SKELETON_SKULL)) {
            return Blocks.WITHER_SKELETON_SKULL.defaultBlockState();
        }
        if (stack.is(Items.ZOMBIE_HEAD)) {
            return Blocks.ZOMBIE_HEAD.defaultBlockState();
        }
        if (stack.is(Items.CREEPER_HEAD)) {
            return Blocks.CREEPER_HEAD.defaultBlockState();
        }
        if (stack.is(Items.PIGLIN_HEAD)) {
            return Blocks.PIGLIN_HEAD.defaultBlockState();
        }
        *///?}
        return null;
    }

    @Nullable
    public static EntityType<?> getHeadTypeFromItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        //? if fabric {
        if (stack.isOf(Items.SKELETON_SKULL)) {
            return EntityType.SKELETON;
        }
        if (stack.isOf(Items.WITHER_SKELETON_SKULL)) {
            return EntityType.WITHER_SKELETON;
        }
        if (stack.isOf(Items.ZOMBIE_HEAD)) {
            return EntityType.ZOMBIE;
        }
        if (stack.isOf(Items.CREEPER_HEAD)) {
            return EntityType.CREEPER;
        }
        if (stack.isOf(Items.PIGLIN_HEAD)) {
            return EntityType.PIGLIN;
        }
        //?} else {
        /*if (stack.is(Items.SKELETON_SKULL)) {
            return EntityType.SKELETON;
        }
        if (stack.is(Items.WITHER_SKELETON_SKULL)) {
            return EntityType.WITHER_SKELETON;
        }
        if (stack.is(Items.ZOMBIE_HEAD)) {
            return EntityType.ZOMBIE;
        }
        if (stack.is(Items.CREEPER_HEAD)) {
            return EntityType.CREEPER;
        }
        if (stack.is(Items.PIGLIN_HEAD)) {
            return EntityType.PIGLIN;
        }
        *///?}
        return null;
    }

    @Nullable
    public static EntityType<?> getHeadTypeFromBlockState(BlockState state) {
        //? if fabric {
        if (state.getBlock() instanceof SkullBlock skullBlock) {
            SkullBlock.SkullType type = skullBlock.getSkullType();
            return getHeadTypeFromSkullType(type);
        }
        //?} else {
        /*if (state.getBlock() instanceof SkullBlock skullBlock) {
            SkullBlock.Type type = skullBlock.getType();
            return getHeadTypeFromSkullType(type);
        }
        *///?}
        return null;
    }

    //? if fabric {
    @Nullable
    private static EntityType<?> getHeadTypeFromSkullType(SkullBlock.SkullType type) {
        if (type == SkullBlock.Type.SKELETON) {
            return EntityType.SKELETON;
        }
        if (type == SkullBlock.Type.WITHER_SKELETON) {
            return EntityType.WITHER_SKELETON;
        }
        if (type == SkullBlock.Type.ZOMBIE) {
            return EntityType.ZOMBIE;
        }
        if (type == SkullBlock.Type.CREEPER) {
            return EntityType.CREEPER;
        }
        if (type == SkullBlock.Type.PIGLIN) {
            return EntityType.PIGLIN;
        }
        return null;
    }
    //?} else {
    /*@Nullable
    private static EntityType<?> getHeadTypeFromSkullType(SkullBlock.Type type) {
        if (type == SkullBlock.Types.SKELETON) {
            return EntityType.SKELETON;
        }
        if (type == SkullBlock.Types.WITHER_SKELETON) {
            return EntityType.WITHER_SKELETON;
        }
        if (type == SkullBlock.Types.ZOMBIE) {
            return EntityType.ZOMBIE;
        }
        if (type == SkullBlock.Types.CREEPER) {
            return EntityType.CREEPER;
        }
        if (type == SkullBlock.Types.PIGLIN) {
            return EntityType.PIGLIN;
        }
        return null;
    }
    *///?}
}
