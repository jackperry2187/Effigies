package jackperry2187.effigies.block;

import jackperry2187.effigies.PikeRegistry;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import jackperry2187.effigies.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

//? if fabric {
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Explosion;
//?} else {
/*import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
*///?}

public class PikeBlock extends Block implements EntityBlock {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    private final PikeTier tier;

    //? if fabric {
    public PikeBlock(PikeTier tier, Identifier id) {
        super(tier.blockProperties(id));
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }
    //?} else {
    /*public PikeBlock(PikeTier tier, BlockBehaviour.Properties props) {
        super(props);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }
    *///?}

    public PikeTier tier() {
        return tier;
    }

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
    *///?}

    //? if fabric {
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        BlockPos headPos = pos.above();
        BlockState headState = level.getBlockState(headPos);
        if (!PikeBlockEntity.isValidHeadBlock(headState)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            BlockEntity headBe = level.getBlockEntity(headPos);
            ItemStack drop = ItemStack.EMPTY;
            if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                drop = pikeHead.getStoredItemStack();
                pikeHead.setStoredHead("", 0);
            }
            level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);
            level.removeBlock(headPos, false);
            if (!drop.isEmpty()) {
                Block.popResource(level, headPos, drop);
            }
            if (level instanceof ServerLevel serverLevel) {
                PikeRegistry.unregisterPike(serverLevel, pos);
                serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.5,
                    pos.getZ() + 0.5,
                    15, 0.3, 0.4, 0.3, 0.02
                );
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (player.isShiftKeyDown()) {
            BlockPos headPos = pos.above();
            BlockState headState = level.getBlockState(headPos);
            if (PikeBlockEntity.isValidHeadBlock(headState)) {
                if (!level.isClientSide()) {
                    BlockEntity headBe = level.getBlockEntity(headPos);
                    ItemStack drop = ItemStack.EMPTY;
                    if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                        drop = pikeHead.getStoredItemStack();
                        pikeHead.setStoredHead("", 0);
                    }
                    level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);
                    level.removeBlock(headPos, false);
                    if (!drop.isEmpty()) {
                        Block.popResource(level, headPos, drop);
                    }
                    if (level instanceof ServerLevel serverLevel) {
                        PikeRegistry.unregisterPike(serverLevel, pos);
                        serverLevel.sendParticles(
                            ParticleTypes.SMOKE,
                            pos.getX() + 0.5,
                            pos.getY() + 1.5,
                            pos.getZ() + 0.5,
                            15, 0.3, 0.4, 0.3, 0.02
                        );
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        EntityType<?> headType = PikeBlockEntity.getHeadTypeFromItem(stack);
        if (headType == null) {
            return InteractionResult.PASS;
        }
        BlockPos headPos = pos.above();
        if (PikeBlockEntity.isValidHeadBlock(level.getBlockState(headPos)) || !canPlaceHead(level, headPos)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            String blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString();
            int rotation = Mth.floor((double) (player.getYRot() * 16.0F / 360.0F) + 0.5D) & 15;

            level.setBlock(headPos, ModBlocks.pikeHead().defaultBlockState(), Block.UPDATE_CLIENTS);
            BlockEntity be = level.getBlockEntity(headPos);
            if (be instanceof PikeHeadBlockEntity pikeHead) {
                pikeHead.setStoredHead(blockId, rotation);
            }
            SoundType soundType = blockItem.getBlock().defaultBlockState().getSoundType();
            level.playSound(null, headPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            updateActivatedState(level, pos, state);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        BlockPos headPos = pos.above();
        BlockState headState = level.getBlockState(headPos);
        if (!PikeBlockEntity.isValidHeadBlock(headState)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            BlockEntity headBe = level.getBlockEntity(headPos);
            ItemStack drop = ItemStack.EMPTY;
            if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                drop = pikeHead.getStoredItemStack();
                pikeHead.setStoredHead("", 0);
            }
            level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);
            level.removeBlock(headPos, false);
            if (!drop.isEmpty()) {
                Block.popResource(level, headPos, drop);
            }
            if (level instanceof ServerLevel serverLevel) {
                PikeRegistry.unregisterPike(serverLevel, pos);
                serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.5,
                    pos.getZ() + 0.5,
                    15, 0.3, 0.4, 0.3, 0.02
                );
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (player.isShiftKeyDown()) {
            BlockPos headPos = pos.above();
            BlockState headState = level.getBlockState(headPos);
            if (PikeBlockEntity.isValidHeadBlock(headState)) {
                if (!level.isClientSide()) {
                    BlockEntity headBe = level.getBlockEntity(headPos);
                    ItemStack drop = ItemStack.EMPTY;
                    if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                        drop = pikeHead.getStoredItemStack();
                        pikeHead.setStoredHead("", 0);
                    }
                    level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);
                    level.removeBlock(headPos, false);
                    if (!drop.isEmpty()) {
                        Block.popResource(level, headPos, drop);
                    }
                    if (level instanceof ServerLevel serverLevel) {
                        PikeRegistry.unregisterPike(serverLevel, pos);
                        serverLevel.sendParticles(
                            ParticleTypes.SMOKE,
                            pos.getX() + 0.5,
                            pos.getY() + 1.5,
                            pos.getZ() + 0.5,
                            15, 0.3, 0.4, 0.3, 0.02
                        );
                    }
                }
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        EntityType<?> headType = PikeBlockEntity.getHeadTypeFromItem(stack);
        if (headType == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        BlockPos headPos = pos.above();
        if (PikeBlockEntity.isValidHeadBlock(level.getBlockState(headPos)) || !canPlaceHead(level, headPos)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!level.isClientSide()) {
            String blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString();
            int rotation = Mth.floor((double) (player.getYRot() * 16.0F / 360.0F) + 0.5D) & 15;

            level.setBlock(headPos, ModBlocks.pikeHead().defaultBlockState(), Block.UPDATE_CLIENTS);
            BlockEntity be = level.getBlockEntity(headPos);
            if (be instanceof PikeHeadBlockEntity pikeHead) {
                pikeHead.setStoredHead(blockId, rotation);
            }
            SoundType soundType = blockItem.getBlock().defaultBlockState().getSoundType();
            level.playSound(null, headPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            updateActivatedState(level, pos, state);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
    *///?}

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            if (state.getValue(ACTIVATED) && level instanceof ServerLevel serverLevel) {
                PikeRegistry.unregisterPike(serverLevel, pos);
            }
            breakHeadAbove(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    //? if fabric {
    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PikeBlock && state.getValue(ACTIVATED)) {
            PikeRegistry.unregisterPike(level, pos);
        }
        breakHeadAbove(level, pos);
        super.wasExploded(level, pos, explosion);
    }
    //?} else {
    /*@Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            if (state.getValue(ACTIVATED)) {
                PikeRegistry.unregisterPike(serverLevel, pos);
            }
            breakHeadAbove(serverLevel, pos);
        }
        super.destroy(level, pos, state);
    }
    *///?}

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            updateActivatedState(level, pos, state);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        if (!level.isClientSide()) {
            updateActivatedState(level, pos, state);
        }
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader levelReader,
        ScheduledTickAccess tickAccess,
        BlockPos pos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        RandomSource random
    ) {
        // Note: State updates are handled by neighborChanged -> updateActivatedState
        // to ensure messages are sent properly. Don't update state here.
        return super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Nullable
    @Override
    public PikeBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PikeBlockEntity(pos, state);
    }

    private void updateActivatedState(Level level, BlockPos pos, BlockState state) {
        BlockPos headPos = pos.above();
        BlockState headState = level.getBlockState(headPos);
        boolean active = PikeBlockEntity.isValidHeadBlock(headState);
        BlockState currentWorldState = level.getBlockState(pos);
        boolean wasActive = currentWorldState.getValue(ACTIVATED);
        if (wasActive != active) {
            level.setBlock(pos, currentWorldState.setValue(ACTIVATED, active), Block.UPDATE_ALL);
            if (level instanceof ServerLevel serverLevel) {
                if (active) {
                    EntityType<?> headType = PikeBlockEntity.getHeadTypeFromWorld(level, headPos);
                    if (headType != null) {
                        PikeRegistry.registerPike(serverLevel, pos, tier, headType);
                        serverLevel.sendParticles(
                            ParticleTypes.FLAME,
                            pos.getX() + 0.5,
                            pos.getY() + 1.5,
                            pos.getZ() + 0.5,
                            15,    // particle count
                            0.3,   // deltaX (horizontal spread)
                            0.4,   // deltaY (vertical spread)
                            0.3,   // deltaZ (horizontal spread)
                            0.02   // speed
                        );
                    }
                } else {
                    PikeRegistry.unregisterPike(serverLevel, pos);
                    serverLevel.sendParticles(
                        ParticleTypes.SMOKE,
                        pos.getX() + 0.5,
                        pos.getY() + 1.5,
                        pos.getZ() + 0.5,
                        15,    // particle count
                        0.3,   // deltaX (horizontal spread)
                        0.4,   // deltaY (vertical spread)
                        0.3,   // deltaZ (horizontal spread)
                        0.02   // speed
                    );
                }
            }
        }
    }

    private void breakHeadAbove(Level level, BlockPos pos) {
        BlockPos headPos = pos.above();
        BlockState headState = level.getBlockState(headPos);
        if (PikeBlockEntity.isValidHeadBlock(headState)) {
            BlockEntity be = level.getBlockEntity(headPos);
            if (be instanceof PikeHeadBlockEntity pikeHead) {
                ItemStack drop = pikeHead.getStoredItemStack();
                pikeHead.setStoredHead("", 0);
                if (!drop.isEmpty()) {
                    Block.popResource(level, headPos, drop);
                }
            }
            level.removeBlock(headPos, false);
        }
    }

    private boolean canPlaceHead(Level level, BlockPos headPos) {
        BlockState state = level.getBlockState(headPos);
        return state.isAir() || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA;
    }

    // Debug messages - commented out for release
    // private void sendActivationMessage(Player player, EntityType<?> headType) {
    //     String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(headType).getPath();
    //     int chunkRadius = tier.chunkRadius();
    //     String radiusText = chunkRadius == 0 ? "same chunk only" : chunkRadius + " chunk" + (chunkRadius > 1 ? "s" : "") + " radius";
    //     player.displayClientMessage(
    //         Component.literal("[Effigies] ").withStyle(ChatFormatting.GOLD)
    //             .append(Component.literal("Now preventing ").withStyle(ChatFormatting.GREEN))
    //             .append(Component.literal(entityName).withStyle(ChatFormatting.YELLOW))
    //             .append(Component.literal(" spawns within ").withStyle(ChatFormatting.GREEN))
    //             .append(Component.literal(radiusText).withStyle(ChatFormatting.AQUA)),
    //         false
    //     );
    // }

    // private void sendDeactivationMessage(Player player, EntityType<?> headType) {
    //     String entityName = headType != null ? BuiltInRegistries.ENTITY_TYPE.getKey(headType).getPath() : "unknown";
    //     player.displayClientMessage(
    //         Component.literal("[Effigies] ").withStyle(ChatFormatting.GOLD)
    //             .append(Component.literal("No longer preventing ").withStyle(ChatFormatting.RED))
    //             .append(Component.literal(entityName).withStyle(ChatFormatting.YELLOW))
    //             .append(Component.literal(" spawns.").withStyle(ChatFormatting.RED)),
    //         false
    //     );
    // }

    // private void sendDeactivationMessageGeneric(Player player) {
    //     player.displayClientMessage(
    //         Component.literal("[Effigies] ").withStyle(ChatFormatting.GOLD)
    //             .append(Component.literal("Pike deactivated - head removed.").withStyle(ChatFormatting.RED)),
    //         false
    //     );
    // }
}
