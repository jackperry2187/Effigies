package jackperry2187.effigies.block;

import jackperry2187.effigies.block.entity.AntiPikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import jackperry2187.effigies.registry.ModBlockEntities;
import jackperry2187.effigies.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
*///?}

public class AntiPikeBlock extends Block
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
    private static final VoxelShape SHAPE = Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    //?} else {
    /*private static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    *///?}

    //? if fabric {
    public AntiPikeBlock(Identifier id) {
        super(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(50.0F, 1200.0F)
            .sounds(BlockSoundGroup.NETHERITE)
            .nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(ACTIVATED, false));
    }
    //?} else {
    /*public AntiPikeBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    public static BlockBehaviour.Properties neoForgeProperties() {
        return BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("effigies", "anti_pike")))
            .strength(50.0F, 1200.0F)
            .sound(SoundType.NETHERITE_BLOCK)
            .noOcclusion();
    }
    *///?}

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
                serverWorld.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
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
                        serverWorld.spawnParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
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
            breakHeadAbove(world, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
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
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Nullable
    @Override
    public AntiPikeBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AntiPikeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient() && type == ModBlockEntities.antiPike()) {
            return (w, p, s, be) -> ((AntiPikeBlockEntity) be).serverTick((ServerWorld) w, p, s);
        }
        return null;
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
                serverLevel.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
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
                        serverLevel.sendParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
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
            breakHeadAbove(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
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
        return super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Nullable
    @Override
    public AntiPikeBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AntiPikeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide() && type == ModBlockEntities.antiPike()) {
            return (l, p, s, be) -> ((AntiPikeBlockEntity) be).serverTick((net.minecraft.server.level.ServerLevel) l, p, s);
        }
        return null;
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
                    serverWorld.spawnParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        pos.getX() + 0.5,
                        pos.getY() + 1.5,
                        pos.getZ() + 0.5,
                        15, 0.3, 0.4, 0.3, 0.02
                    );
                } else {
                    serverWorld.spawnParticles(
                        ParticleTypes.SMOKE,
                        pos.getX() + 0.5,
                        pos.getY() + 1.5,
                        pos.getZ() + 0.5,
                        15, 0.3, 0.4, 0.3, 0.02
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
                    serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        pos.getX() + 0.5,
                        pos.getY() + 1.5,
                        pos.getZ() + 0.5,
                        15, 0.3, 0.4, 0.3, 0.02
                    );
                } else {
                    serverLevel.sendParticles(
                        ParticleTypes.SMOKE,
                        pos.getX() + 0.5,
                        pos.getY() + 1.5,
                        pos.getZ() + 0.5,
                        15, 0.3, 0.4, 0.3, 0.02
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

    *///?}
}
