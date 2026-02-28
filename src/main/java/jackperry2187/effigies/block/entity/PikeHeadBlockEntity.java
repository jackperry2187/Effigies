package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

public class PikeHeadBlockEntity extends BlockEntity {
    private String storedBlockId = "";
    private int rotation = 0;

    public PikeHeadBlockEntity(BlockPos pos, BlockState state) {
        //? if fabric {
        super(ModBlockEntities.pikeHead(), pos, state);
        //?} else {
        /*super(ModBlockEntities.pikeHead(), pos, state);
        *///?}
    }

    public String getStoredBlockId() {
        return storedBlockId;
    }

    public int getRotation() {
        return rotation;
    }

    //? if fabric {
    public void setStoredHead(String blockId, int rotation) {
        this.storedBlockId = blockId;
        this.rotation = rotation;
        markDirty();
        if (getWorld() != null) {
            getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Nullable
    public BlockState getStoredBlockState() {
        if (storedBlockId == null || storedBlockId.isEmpty()) return null;
        Identifier id = Identifier.tryParse(storedBlockId);
        if (id == null || !Registries.BLOCK.containsId(id)) return null;
        return Registries.BLOCK.get(id).getDefaultState();
    }

    public ItemStack getStoredItemStack() {
        if (storedBlockId == null || storedBlockId.isEmpty()) return ItemStack.EMPTY;
        BlockState state = getStoredBlockState();
        if (state == null) return ItemStack.EMPTY;
        return new ItemStack(state.getBlock().asItem());
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putString("StoredBlockId", storedBlockId);
        view.putInt("Rotation", rotation);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        storedBlockId = view.getString("StoredBlockId", "");
        rotation = view.getInt("Rotation", 0);
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
    /*public void setStoredHead(String blockId, int rotation) {
        this.storedBlockId = blockId;
        this.rotation = rotation;
        setChanged();
        if (getLevel() != null) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Nullable
    public BlockState getStoredBlockState() {
        if (storedBlockId == null || storedBlockId.isEmpty()) return null;
        Identifier id = Identifier.tryParse(storedBlockId);
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) return null;
        return BuiltInRegistries.BLOCK.getValue(id).defaultBlockState();
    }

    public ItemStack getStoredItemStack() {
        if (storedBlockId == null || storedBlockId.isEmpty()) return ItemStack.EMPTY;
        BlockState state = getStoredBlockState();
        if (state == null) return ItemStack.EMPTY;
        return new ItemStack(state.getBlock().asItem());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("StoredBlockId", storedBlockId);
        output.putInt("Rotation", rotation);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedBlockId = input.getStringOr("StoredBlockId", "");
        rotation = input.getIntOr("Rotation", 0);
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
}
