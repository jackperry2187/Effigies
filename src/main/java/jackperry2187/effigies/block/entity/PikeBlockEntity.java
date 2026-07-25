package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.block.PikeHeadBlock;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.ModList;
*///?}

public class PikeBlockEntity extends BlockEntity {
    public PikeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.pike(), pos, state);
    }

    public static boolean isValidHeadItem(ItemStack stack) {
        return getHeadTypeFromItem(stack) != null;
    }

    public static boolean isValidHeadBlock(BlockState state) {
        return state.getBlock() instanceof PikeHeadBlock;
    }

    @Nullable
    public static BlockState getHeadBlockStateForItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        Block block = blockItem.getBlock();
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        String entityId = ConfigSettings.getEntityIdForBlock(blockId);
        if (entityId == null) {
            return null;
        }
        return block.defaultBlockState();
    }

    @Nullable
    public static EntityType<?> getHeadTypeFromItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        Block block = blockItem.getBlock();
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        return resolveEntityType(blockId);
    }

    @Nullable
    public static EntityType<?> getHeadTypeFromBlockState(BlockState state) {
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        return resolveEntityType(blockId);
    }

    /**
     * Resolves the entity type from a PikeHeadBlockEntity at the given position.
     * Used when the head above a pike is a PikeHeadBlock that stores the original block ID.
     */
    @Nullable
    public static EntityType<?> getHeadTypeFromWorld(Level level, BlockPos headPos) {
        BlockEntity be = level.getBlockEntity(headPos);
        if (!(be instanceof PikeHeadBlockEntity pikeHead)) return null;
        String storedBlockId = pikeHead.getStoredBlockId();
        if (storedBlockId == null || storedBlockId.isEmpty()) return null;
        return resolveEntityType(storedBlockId);
    }

    /**
     * Resolves a block ID to its mapped EntityType via config, or null if not configured
     * or the entity type cannot be found in the registry.
     */
    @Nullable
    private static EntityType<?> resolveEntityType(String blockId) {
        String entityId = ConfigSettings.getEntityIdForBlock(blockId);
        if (entityId == null) {
            return null;
        }
        String[] parts = entityId.split(":", 2);
        if (parts.length != 2) {
            Effigies.LOGGER.error("Invalid entity ID format in block-entity mapping: {}", entityId);
            return null;
        }
        Identifier id = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            Effigies.LOGGER.error("Entity type not found in registry for mapping {}={}: {}", blockId, entityId, id);
            return null;
        }
        return BuiltInRegistries.ENTITY_TYPE.getValue(id);
    }

    private static boolean isModLoaded(String modId) {
        //? if fabric {
        return FabricLoader.getInstance().isModLoaded(modId);
        //?} else {
        /*return ModList.get().isLoaded(modId);
        *///?}
    }

    /**
     * Validates all block-to-entity mappings against the registries.
     * Should be called on server start when all registries are frozen.
     * Logs errors for any block or entity IDs that don't exist in the registries.
     */
    public static void validateMappings() {
        Map<String, String> mappings = ConfigSettings.getBlockToEntityMappings();
        int invalidCount = 0;
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            String blockIdStr = entry.getKey();
            String entityIdStr = entry.getValue();

            String[] blockParts = blockIdStr.split(":", 2);
            String[] entityParts = entityIdStr.split(":", 2);

            Identifier blockId = Identifier.fromNamespaceAndPath(blockParts[0], blockParts[1]);
            Identifier entityId = Identifier.fromNamespaceAndPath(entityParts[0], entityParts[1]);
            boolean blockExists = BuiltInRegistries.BLOCK.containsKey(blockId);
            boolean entityExists = BuiltInRegistries.ENTITY_TYPE.containsKey(entityId);

            if (!blockExists) {
                invalidCount++;
                if (!blockParts[0].equals("minecraft") && !isModLoaded(blockParts[0])) {
                    Effigies.LOGGER.error("Mod '{}' is not installed. Block '{}' in mapping '{}={}' will not work.", blockParts[0], blockIdStr, blockIdStr, entityIdStr);
                } else {
                    Effigies.LOGGER.error("Block '{}' not found in registry. Mapping '{}={}' will not work.", blockIdStr, blockIdStr, entityIdStr);
                }
            }
            if (!entityExists) {
                invalidCount++;
                if (!entityParts[0].equals("minecraft") && !isModLoaded(entityParts[0])) {
                    Effigies.LOGGER.error("Mod '{}' is not installed. Entity type '{}' in mapping '{}={}' will not work.", entityParts[0], entityIdStr, blockIdStr, entityIdStr);
                } else {
                    Effigies.LOGGER.error("Entity type '{}' not found in registry. Mapping '{}={}' will not work.", entityIdStr, blockIdStr, entityIdStr);
                }
            }
        }
        if (invalidCount > 0) {
            Effigies.LOGGER.error("Found {} invalid block-to-entity mapping reference(s). Check your Effigies config file.", invalidCount);
        } else {
            Effigies.LOGGER.info("All {} block-to-entity mappings validated successfully against registries.", mappings.size());
        }
    }
}
