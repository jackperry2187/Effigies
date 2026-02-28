package jackperry2187.effigies.block;

import jackperry2187.effigies.PikeRegistry;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import jackperry2187.effigies.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
*///?}

public class PikeBlock extends Block
    //? if fabric {
    implements BlockEntityProvider
    //?} else {
    /*implements EntityBlock
    *///?}
{
    //? if fabric {
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    //?} else {
    /*public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    *///?}
    //? if fabric {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    //?} else {
    /*private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    *///?}

    private final PikeTier tier;

    //? if fabric {
    public PikeBlock(PikeTier tier, Identifier id) {
        super(tier.blockProperties(id));
        this.tier = tier;
        setDefaultState(getStateManager().getDefaultState().with(ACTIVATED, false));
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
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.isSneaking()) {
            return ActionResult.PASS;
        }
        BlockPos headPos = pos.up();
        BlockState headState = world.getBlockState(headPos);
        if (!PikeBlockEntity.isValidHeadBlock(headState)) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            BlockEntity headBe = world.getBlockEntity(headPos);
            ItemStack drop = ItemStack.EMPTY;
            if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                drop = pikeHead.getStoredItemStack();
                pikeHead.setStoredHead("", 0);
            }
            world.setBlockState(pos, state.with(ACTIVATED, false), Block.NOTIFY_ALL);
            world.removeBlock(headPos, false);
            if (!drop.isEmpty()) {
                Block.dropStack(world, headPos, drop);
            }
            if (world instanceof ServerWorld serverWorld) {
                PikeRegistry.unregisterPike(serverWorld, pos);
                serverWorld.spawnParticles(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.5,
                    pos.getZ() + 0.5,
                    15, 0.3, 0.4, 0.3, 0.02
                );
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUseWithItem(
        ItemStack stack,
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        if (player.isSneaking()) {
            BlockPos headPos = pos.up();
            BlockState headState = world.getBlockState(headPos);
            if (PikeBlockEntity.isValidHeadBlock(headState)) {
                if (!world.isClient()) {
                    BlockEntity headBe = world.getBlockEntity(headPos);
                    ItemStack drop = ItemStack.EMPTY;
                    if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                        drop = pikeHead.getStoredItemStack();
                        pikeHead.setStoredHead("", 0);
                    }
                    world.setBlockState(pos, state.with(ACTIVATED, false), Block.NOTIFY_ALL);
                    world.removeBlock(headPos, false);
                    if (!drop.isEmpty()) {
                        Block.dropStack(world, headPos, drop);
                    }
                    if (world instanceof ServerWorld serverWorld) {
                        PikeRegistry.unregisterPike(serverWorld, pos);
                        serverWorld.spawnParticles(
                            ParticleTypes.SMOKE,
                            pos.getX() + 0.5,
                            pos.getY() + 1.5,
                            pos.getZ() + 0.5,
                            15, 0.3, 0.4, 0.3, 0.02
                        );
                    }
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        EntityType<?> headType = PikeBlockEntity.getHeadTypeFromItem(stack);
        if (headType == null) {
            return ActionResult.PASS;
        }
        BlockPos headPos = pos.up();
        if (PikeBlockEntity.isValidHeadBlock(world.getBlockState(headPos)) || !canPlaceHead(world, headPos)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            String blockId = Registries.BLOCK.getId(blockItem.getBlock()).toString();
            int rotation = MathHelper.floor((double) (player.getYaw() * 16.0F / 360.0F) + 0.5D) & 15;

            world.setBlockState(headPos, ModBlocks.pikeHead().getDefaultState(), Block.NOTIFY_LISTENERS);
            BlockEntity be = world.getBlockEntity(headPos);
            if (be instanceof PikeHeadBlockEntity pikeHead) {
                pikeHead.setStoredHead(blockId, rotation);
            }
            BlockSoundGroup soundGroup = blockItem.getBlock().getDefaultState().getSoundGroup();
            world.playSound(null, headPos, soundGroup.getPlaceSound(), SoundCategory.BLOCKS,
                (soundGroup.getVolume() + 1.0F) / 2.0F, soundGroup.getPitch() * 0.8F);
            updateActivatedState(world, pos, state);
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            // Unregister from pike registry if it was active
            if (state.get(ACTIVATED) && world instanceof ServerWorld serverWorld) {
                PikeRegistry.unregisterPike(serverWorld, pos);
            }
            breakHeadAbove(world, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
        // Unregister from pike registry (we check the block state before it's destroyed)
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof PikeBlock && state.get(ACTIVATED)) {
            PikeRegistry.unregisterPike(world, pos);
        }
        breakHeadAbove(world, pos);
        super.onDestroyedByExplosion(world, pos, explosion);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            updateActivatedState(world, pos, state);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, block, wireOrientation, notify);
        if (!world.isClient()) {
            updateActivatedState(world, pos, state);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
        BlockState state,
        WorldView world,
        ScheduledTickView tickView,
        BlockPos pos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        Random random
    ) {
        // Note: State updates are handled by neighborUpdate -> updateActivatedState
        // to ensure messages are sent properly. Don't update state here.
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Nullable
    @Override
    public PikeBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PikeBlockEntity(pos, state);
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
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
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
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
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

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            // Unregister from pike registry if it was active
            if (state.getValue(ACTIVATED) && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                PikeRegistry.unregisterPike(serverLevel, pos);
            }
            breakHeadAbove(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // Unregister from pike registry if it was active
            if (state.getValue(ACTIVATED)) {
                PikeRegistry.unregisterPike(serverLevel, pos);
            }
            breakHeadAbove(serverLevel, pos);
        }
        super.destroy(level, pos, state);
    }

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
    *///?}

    //? if fabric {
    private void updateActivatedState(World world, BlockPos pos, BlockState state) {
        BlockPos headPos = pos.up();
        BlockState headState = world.getBlockState(headPos);
        boolean active = PikeBlockEntity.isValidHeadBlock(headState);
        BlockState currentWorldState = world.getBlockState(pos);
        boolean wasActive = currentWorldState.get(ACTIVATED);
        if (wasActive != active) {
            world.setBlockState(pos, currentWorldState.with(ACTIVATED, active), Block.NOTIFY_ALL);
            if (world instanceof ServerWorld serverWorld) {
                if (active) {
                    EntityType<?> headType = PikeBlockEntity.getHeadTypeFromWorld(world, headPos);
                    if (headType != null) {
                        // Register pike in the registry for efficient spawn blocking
                        PikeRegistry.registerPike(serverWorld, pos, tier, headType);
                        // Spawn activation particles
                        serverWorld.spawnParticles(
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
                    // Unregister pike from the registry
                    PikeRegistry.unregisterPike(serverWorld, pos);
                    // Spawn deactivation particles
                    serverWorld.spawnParticles(
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

    private void breakHeadAbove(World world, BlockPos pos) {
        BlockPos headPos = pos.up();
        BlockState headState = world.getBlockState(headPos);
        if (PikeBlockEntity.isValidHeadBlock(headState)) {
            BlockEntity be = world.getBlockEntity(headPos);
            if (be instanceof PikeHeadBlockEntity pikeHead) {
                ItemStack drop = pikeHead.getStoredItemStack();
                pikeHead.setStoredHead("", 0);
                if (drop != null && !drop.isEmpty()) {
                    Block.dropStack(world, headPos, drop);
                }
            }
            world.removeBlock(headPos, false);
        }
    }

    private boolean canPlaceHead(World world, BlockPos headPos) {
        BlockState state = world.getBlockState(headPos);
        return state.isAir() || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA;
    }

    // Debug messages - commented out for release
    // private void sendActivationMessage(PlayerEntity player, EntityType<?> headType) {
    //     String entityName = Registries.ENTITY_TYPE.getId(headType).getPath();
    //     int chunkRadius = tier.chunkRadius();
    //     String radiusText = chunkRadius == 0 ? "same chunk only" : chunkRadius + " chunk" + (chunkRadius > 1 ? "s" : "") + " radius";
    //     player.sendMessage(
    //         Text.literal("[Effigies] ").formatted(Formatting.GOLD)
    //             .append(Text.literal("Now preventing ").formatted(Formatting.GREEN))
    //             .append(Text.literal(entityName).formatted(Formatting.YELLOW))
    //             .append(Text.literal(" spawns within ").formatted(Formatting.GREEN))
    //             .append(Text.literal(radiusText).formatted(Formatting.AQUA)),
    //         false
    //     );
    // }

    // private void sendDeactivationMessage(PlayerEntity player, EntityType<?> headType) {
    //     String entityName = headType != null ? Registries.ENTITY_TYPE.getId(headType).getPath() : "unknown";
    //     player.sendMessage(
    //         Text.literal("[Effigies] ").formatted(Formatting.GOLD)
    //             .append(Text.literal("No longer preventing ").formatted(Formatting.RED))
    //             .append(Text.literal(entityName).formatted(Formatting.YELLOW))
    //             .append(Text.literal(" spawns.").formatted(Formatting.RED)),
    //         false
    //     );
    // }

    // private void sendDeactivationMessageGeneric(PlayerEntity player) {
    //     player.sendMessage(
    //         Text.literal("[Effigies] ").formatted(Formatting.GOLD)
    //             .append(Text.literal("Pike deactivated - head removed.").formatted(Formatting.RED)),
    //         false
    //     );
    // }

    //?} else {
    /*private void updateActivatedState(Level level, BlockPos pos, BlockState state) {
        BlockPos headPos = pos.above();
        BlockState headState = level.getBlockState(headPos);
        boolean active = PikeBlockEntity.isValidHeadBlock(headState);
        BlockState currentWorldState = level.getBlockState(pos);
        boolean wasActive = currentWorldState.getValue(ACTIVATED);
        if (wasActive != active) {
            level.setBlock(pos, currentWorldState.setValue(ACTIVATED, active), Block.UPDATE_ALL);
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                if (active) {
                    EntityType<?> headType = PikeBlockEntity.getHeadTypeFromWorld(level, headPos);
                    if (headType != null) {
                        // Register pike in the registry for efficient spawn blocking
                        PikeRegistry.registerPike(serverLevel, pos, tier, headType);
                        // Spawn activation particles
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
                    // Unregister pike from the registry
                    PikeRegistry.unregisterPike(serverLevel, pos);
                    // Spawn deactivation particles
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

    *///?}
}
