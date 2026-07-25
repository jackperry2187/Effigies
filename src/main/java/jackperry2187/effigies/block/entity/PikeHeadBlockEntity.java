package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
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

public class PikeHeadBlockEntity extends BlockEntity {
    private String storedBlockId = "";
    private int rotation = 0;

    public PikeHeadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.pikeHead(), pos, state);
    }

    public String getStoredBlockId() {
        return storedBlockId;
    }

    public int getRotation() {
        return rotation;
    }

    public void setStoredHead(String blockId, int rotation) {
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
}
