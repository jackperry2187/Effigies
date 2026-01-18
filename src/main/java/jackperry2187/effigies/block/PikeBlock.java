package jackperry2187.effigies.block;

import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
//?} else {
/*import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    /*public PikeBlock(PikeTier tier) {
        super(tier.blockProperties());
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
        if (!(world.getBlockEntity(pos) instanceof PikeBlockEntity pike) || !pike.hasHead()) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            ItemStack drop = pike.createHeadStack();
            pike.clearHead();
            world.setBlockState(pos, state.with(ACTIVATED, false), Block.NOTIFY_ALL);
            // Try to give the item directly to the player, drop if inventory is full
            if (!player.getInventory().insertStack(drop)) {
                Block.dropStack(world, pos, drop);
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
        if (!(world.getBlockEntity(pos) instanceof PikeBlockEntity pike)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (pike.hasHead()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        EntityType<?> headType = PikeBlockEntity.getHeadTypeFromItem(stack);
        if (headType == null) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            pike.setHeadType(headType);
            world.setBlockState(pos, state.with(ACTIVATED, true), Block.NOTIFY_ALL);
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            // Send feedback message to the player
            String entityName = Registries.ENTITY_TYPE.getId(headType).getPath();
            int chunkRadius = tier.chunkRadius();
            String radiusText = chunkRadius == 0 ? "same chunk only" : chunkRadius + " chunk" + (chunkRadius > 1 ? "s" : "") + " radius";
            player.sendMessage(
                Text.literal("[Effigies] ").formatted(Formatting.GOLD)
                    .append(Text.literal("Now preventing ").formatted(Formatting.GREEN))
                    .append(Text.literal(entityName).formatted(Formatting.YELLOW))
                    .append(Text.literal(" spawns within ").formatted(Formatting.GREEN))
                    .append(Text.literal(radiusText).formatted(Formatting.AQUA)),
                false
            );
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof PikeBlockEntity pike && pike.hasHead()) {
            Block.dropStack(world, pos, pike.createHeadStack());
            pike.clearHead();
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
        if (world.getBlockEntity(pos) instanceof PikeBlockEntity pike && pike.hasHead()) {
            Block.dropStack(world, pos, pike.createHeadStack());
            pike.clearHead();
        }
        super.onDestroyedByExplosion(world, pos, explosion);
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
        if (!(level.getBlockEntity(pos) instanceof PikeBlockEntity pike) || !pike.hasHead()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            ItemStack drop = pike.createHeadStack();
            pike.clearHead();
            level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);
            // Try to give the item directly to the player, drop if inventory is full
            if (!player.getInventory().add(drop)) {
                popResource(level, pos, drop);
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
        if (!(level.getBlockEntity(pos) instanceof PikeBlockEntity pike)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (pike.hasHead()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        EntityType<?> headType = PikeBlockEntity.getHeadTypeFromItem(stack);
        if (headType == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!level.isClientSide()) {
            pike.setHeadType(headType);
            level.setBlock(pos, state.setValue(ACTIVATED, true), Block.UPDATE_ALL);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            // Send feedback message to the player
            String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(headType).getPath();
            int chunkRadius = tier.chunkRadius();
            String radiusText = chunkRadius == 0 ? "same chunk only" : chunkRadius + " chunk" + (chunkRadius > 1 ? "s" : "") + " radius";
            player.displayClientMessage(
                Component.literal("[Effigies] ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Now preventing ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(entityName).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" spawns within ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(radiusText).withStyle(ChatFormatting.AQUA)),
                false
            );
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PikeBlockEntity pike && pike.hasHead()) {
            popResource(level, pos, pike.createHeadStack());
            pike.clearHead();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level serverLevel && !serverLevel.isClientSide()
            && serverLevel.getBlockEntity(pos) instanceof PikeBlockEntity pike && pike.hasHead()) {
            popResource(serverLevel, pos, pike.createHeadStack());
            pike.clearHead();
        }
        super.destroy(level, pos, state);
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
}
