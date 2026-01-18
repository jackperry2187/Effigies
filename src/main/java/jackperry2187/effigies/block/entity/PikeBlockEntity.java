package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

public class PikeBlockEntity extends BlockEntity {
    private static final String HEAD_TYPE_KEY = "HeadType";

    @Nullable
    private EntityType<?> headType;

    public PikeBlockEntity(BlockPos pos, BlockState state) {
        //? if fabric {
        super(ModBlockEntities.pike(), pos, state);
        //?} else {
        /*super(ModBlockEntities.pike(), pos, state);
        *///?}
    }

    public boolean hasHead() {
        return headType != null;
    }

    @Nullable
    public EntityType<?> getHeadType() {
        return headType;
    }

    public void setHeadType(EntityType<?> type) {
        headType = type;
        //? if fabric {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.NOTIFY_ALL);
        }
        //?} else {
        /*setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        *///?}
    }

    public void clearHead() {
        headType = null;
        //? if fabric {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.NOTIFY_ALL);
        }
        //?} else {
        /*setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        *///?}
    }

    public ItemStack createHeadStack() {
        if (headType == null) {
            return ItemStack.EMPTY;
        }
        Item headItem = getHeadItemForType(headType);
        if (headItem == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(headItem);
    }

    //? if fabric {
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (headType != null) {
            Identifier id = Registries.ENTITY_TYPE.getId(headType);
            view.putString(HEAD_TYPE_KEY, id.toString());
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        headType = null;
        String idString = view.getString(HEAD_TYPE_KEY, "");
        if (!idString.isEmpty()) {
            Identifier id = Identifier.tryParse(idString);
            if (id != null) {
                headType = Registries.ENTITY_TYPE.get(id);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
    //?} else {
    /*@Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (headType != null) {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(headType);
            output.putString(HEAD_TYPE_KEY, id.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        headType = null;
        String idString = input.getStringOr(HEAD_TYPE_KEY, "");
        if (!idString.isEmpty()) {
            Identifier id = Identifier.tryParse(idString);
            if (id != null) {
                headType = BuiltInRegistries.ENTITY_TYPE.getValue(id);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
    *///?}

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
        if (stack.isOf(Items.DRAGON_HEAD)) {
            return EntityType.ENDER_DRAGON;
        }
        if (stack.isOf(Items.PLAYER_HEAD)) {
            return EntityType.PLAYER;
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
        if (stack.is(Items.DRAGON_HEAD)) {
            return EntityType.ENDER_DRAGON;
        }
        if (stack.is(Items.PLAYER_HEAD)) {
            return EntityType.PLAYER;
        }
        *///?}
        return null;
    }

    private static Item getHeadItemForType(EntityType<?> type) {
        if (type == EntityType.SKELETON) {
            return Items.SKELETON_SKULL;
        }
        if (type == EntityType.WITHER_SKELETON) {
            return Items.WITHER_SKELETON_SKULL;
        }
        if (type == EntityType.ZOMBIE) {
            return Items.ZOMBIE_HEAD;
        }
        if (type == EntityType.CREEPER) {
            return Items.CREEPER_HEAD;
        }
        if (type == EntityType.PIGLIN) {
            return Items.PIGLIN_HEAD;
        }
        if (type == EntityType.ENDER_DRAGON) {
            return Items.DRAGON_HEAD;
        }
        if (type == EntityType.PLAYER) {
            return Items.PLAYER_HEAD;
        }
        return Items.AIR;
    }
}
