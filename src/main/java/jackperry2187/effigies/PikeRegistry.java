package jackperry2187.effigies;

import jackperry2187.effigies.block.PikeBlock;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import jackperry2187.effigies.config.ConfigSettings;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Registry for tracking active pikes efficiently.
 * Uses a per-dimension, chunk-indexed data structure for fast spatial queries.
 */
public final class PikeRegistry {
    private PikeRegistry() {}

    /**
     * Data class representing an active pike.
     */
    public record PikeData(
        BlockPos pos,
        PikeTier tier,
        EntityType<?> entityType,
        int chunkX,
        int chunkZ
    ) {}

    // Map: Dimension Registry Key -> (Map: Chunk Long Position -> List<PikeData>)
    private static final Map<ResourceKey<Level>, Map<Long, List<PikeData>>> pikesByDimension = new ConcurrentHashMap<>();

    /**
     * Gets the maximum chunk radius across all pike tiers.
     * Uses config values when available, falls back to hardcoded defaults.
     */
    private static int getMaxChunkRadius() {
        // Use config values when initialized for consistency
        if (ConfigSettings.isInitialized()) {
            return ConfigSettings.getMaxPikeRadius();
        }
        // Fallback to computing from enum defaults (shouldn't happen in normal flow)
        int maxRadius = 0;
        for (PikeTier tier : PikeTier.values()) {
            maxRadius = Math.max(maxRadius, tier.defaultChunkRadius());
        }
        return maxRadius;
    }

    /**
     * Helper method to compute chunk key from chunk coordinates.
     */
    private static long chunkKey(int chunkX, int chunkZ) {
        //? if mc12011 {
        return ChunkPos.asLong(chunkX, chunkZ);
        //?} else {
        /*return ChunkPos.pack(chunkX, chunkZ);
        *///?}
    }

    /**
     * Registers an active pike in the registry.
     * Called when a pike becomes active (head is placed).
     */
    public static void registerPike(ServerLevel level, BlockPos pos, PikeTier tier, EntityType<?> entityType) {
        ResourceKey<Level> dimension = level.dimension();
        
        Map<Long, List<PikeData>> dimensionPikes = pikesByDimension.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>());
        
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long chunkKey = chunkKey(chunkX, chunkZ);
        
        List<PikeData> chunkPikes = dimensionPikes.computeIfAbsent(chunkKey, k -> new ArrayList<>());
        
        // Check if already registered (avoid duplicates)
        synchronized (chunkPikes) {
            for (PikeData existing : chunkPikes) {
                if (existing.pos().equals(pos)) {
                    return; // Already registered
                }
            }
            chunkPikes.add(new PikeData(pos, tier, entityType, chunkX, chunkZ));
        }
    }

    /**
     * Unregisters a pike from the registry.
     * Called when a pike is broken or deactivated (head is removed).
     */
    public static void unregisterPike(ServerLevel level, BlockPos pos) {
        ResourceKey<Level> dimension = level.dimension();
        
        Map<Long, List<PikeData>> dimensionPikes = pikesByDimension.get(dimension);
        if (dimensionPikes == null) {
            return;
        }
        
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long chunkKey = chunkKey(chunkX, chunkZ);
        
        List<PikeData> chunkPikes = dimensionPikes.get(chunkKey);
        if (chunkPikes == null) {
            return;
        }
        
        synchronized (chunkPikes) {
            chunkPikes.removeIf(pike -> pike.pos().equals(pos));
        }
    }

    /**
     * Updates a pike's entity type in the registry.
     * Called when the head on a pike changes.
     */
    public static void updatePikeEntityType(ServerLevel level, BlockPos pos, EntityType<?> newEntityType) {
        ResourceKey<Level> dimension = level.dimension();
        
        Map<Long, List<PikeData>> dimensionPikes = pikesByDimension.get(dimension);
        if (dimensionPikes == null) {
            return;
        }
        
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long chunkKey = chunkKey(chunkX, chunkZ);
        
        List<PikeData> chunkPikes = dimensionPikes.get(chunkKey);
        if (chunkPikes == null) {
            return;
        }
        
        synchronized (chunkPikes) {
            for (int i = 0; i < chunkPikes.size(); i++) {
                PikeData pike = chunkPikes.get(i);
                if (pike.pos().equals(pos)) {
                    chunkPikes.set(i, new PikeData(pos, pike.tier(), newEntityType, chunkX, chunkZ));
                    return;
                }
            }
        }
    }

    /**
     * Clears all pikes in a specific chunk when it's unloaded.
     */
    public static void onChunkUnload(ServerLevel level, int chunkX, int chunkZ) {
        ResourceKey<Level> dimension = level.dimension();
        
        Map<Long, List<PikeData>> dimensionPikes = pikesByDimension.get(dimension);
        if (dimensionPikes == null) {
            return;
        }
        
        long chunkKey = chunkKey(chunkX, chunkZ);
        dimensionPikes.remove(chunkKey);
    }

    /**
     * Scans a chunk for active pikes and registers them.
     * Called when a chunk is loaded.
     */
    public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
        //? if mc12011 {
        onChunkUnload(level, chunk.getPos().x, chunk.getPos().z);
        //?} else {
        /*onChunkUnload(level, chunk.getPos().x(), chunk.getPos().z());
        *///?}
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (!(blockEntity instanceof PikeBlockEntity)) {
                continue;
            }
            BlockPos pos = blockEntity.getBlockPos();
            // Use chunk.getBlockState() instead of level.getBlockState() to avoid
            // potential issues during chunk loading
            BlockState state = chunk.getBlockState(pos);
            if (!(state.getBlock() instanceof PikeBlock pikeBlock)) {
                continue;
            }
            // Only register if the pike is activated
            if (!state.getValue(PikeBlock.ACTIVATED)) {
                continue;
            }
            BlockPos headPos = pos.above();
            EntityType<?> entityType = null;

            // The head above is a PikeHeadBlock that stores the original block ID
            // in its block entity — resolve entity type from that stored data
            BlockEntity headBe = chunk.getBlockEntity(headPos);
            if (headBe instanceof PikeHeadBlockEntity pikeHead) {
                BlockState storedState = pikeHead.getStoredBlockState();
                if (storedState != null) {
                    entityType = PikeBlockEntity.getHeadTypeFromBlockState(storedState);
                }
            }

            // Fallback: direct block state lookup (for any non-PikeHeadBlock heads)
            if (entityType == null) {
                BlockState headState = chunk.getBlockState(headPos);
                entityType = PikeBlockEntity.getHeadTypeFromBlockState(headState);
            }

            if (entityType == null) {
                continue;
            }
            registerPike(level, pos, pikeBlock.tier(), entityType);
        }
    }

    /**
     * Clears the entire registry for a dimension.
     * Called when a world is unloaded.
     */
    public static void onWorldUnload(ResourceKey<Level> dimension) {
        pikesByDimension.remove(dimension);
    }

    /**
     * Clears the entire registry.
     * Called when the server stops.
     */
    public static void clearAll() {
        pikesByDimension.clear();
    }

    /**
     * Checks if a mob spawn should be blocked by any active pike.
     * This is the main query method - optimized for efficiency.
     * 
     * @param level The server world
     * @param spawnPos The position where the mob is trying to spawn
     * @param entityType The type of mob trying to spawn
     * @return The blocking pike's data if spawn is blocked, null otherwise
     */
    @Nullable
    public static PikeData getBlockingPike(ServerLevel level, BlockPos spawnPos, EntityType<?> entityType) {
        ResourceKey<Level> dimension = level.dimension();
        
        Map<Long, List<PikeData>> dimensionPikes = pikesByDimension.get(dimension);
        if (dimensionPikes == null || dimensionPikes.isEmpty()) {
            return null;
        }
        
        int spawnChunkX = spawnPos.getX() >> 4;
        int spawnChunkZ = spawnPos.getZ() >> 4;
        int maxRadius = getMaxChunkRadius();
        
        // Only check chunks within max radius of the spawn position
        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dz = -maxRadius; dz <= maxRadius; dz++) {
                long chunkKey = chunkKey(spawnChunkX + dx, spawnChunkZ + dz);
                List<PikeData> chunkPikes = dimensionPikes.get(chunkKey);
                if (chunkPikes == null || chunkPikes.isEmpty()) {
                    continue;
                }
                
                synchronized (chunkPikes) {
                    for (PikeData pike : chunkPikes) {
                        int pikeRadius = pike.tier().chunkRadius();
                        if (pikeRadius < 0) {
                            continue;
                        }

                        if (pike.entityType() != entityType) {
                            continue;
                        }
                        
                        int chunkDistance = Math.max(
                            Math.abs(pike.chunkX() - spawnChunkX),
                            Math.abs(pike.chunkZ() - spawnChunkZ)
                        );
                        
                        if (chunkDistance <= pikeRadius) {
                            return pike;
                        }
                    }
                }
            }
        }
        
        return null;
    }

    // Debug method - commented out for release
    // /**
    //  * Gets the number of registered pikes for debugging purposes.
    //  */
    // public static int getRegisteredPikeCount() {
    //     int count = 0;
    //     for (Map<Long, List<PikeData>> dimensionPikes : pikesByDimension.values()) {
    //         for (List<PikeData> chunkPikes : dimensionPikes.values()) {
    //             count += chunkPikes.size();
    //         }
    //     }
    //     return count;
    // }
}
