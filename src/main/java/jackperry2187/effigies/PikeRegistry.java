package jackperry2187.effigies;

import jackperry2187.effigies.block.PikeBlock;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//? if fabric {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
*///?}

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
        //? if fabric {
        EntityType<?> entityType,
        //?} else {
        /*EntityType<?> entityType,
        *///?}
        int chunkX,
        int chunkZ
    ) {}

    // Map: Dimension Registry Key -> (Map: Chunk Long Position -> List<PikeData>)
    //? if fabric {
    private static final Map<RegistryKey<World>, Map<Long, List<PikeData>>> pikesByDimension = new ConcurrentHashMap<>();
    //?} else {
    /*private static final Map<ResourceKey<Level>, Map<Long, List<PikeData>>> pikesByDimension = new ConcurrentHashMap<>();
    *///?}

    // Cache the max chunk radius for efficient range checking
    private static int maxChunkRadius = -1;

    private static int getMaxChunkRadius() {
        if (maxChunkRadius < 0) {
            maxChunkRadius = 0;
            for (PikeTier tier : PikeTier.values()) {
                maxChunkRadius = Math.max(maxChunkRadius, tier.chunkRadius());
            }
        }
        return maxChunkRadius;
    }

    /**
     * Helper method to compute chunk key from chunk coordinates.
     * Handles different method names between Fabric and NeoForge.
     */
    private static long chunkKey(int chunkX, int chunkZ) {
        //? if fabric {
        return ChunkPos.toLong(chunkX, chunkZ);
        //?} else {
        /*return ChunkPos.asLong(chunkX, chunkZ);
        *///?}
    }

    /**
     * Registers an active pike in the registry.
     * Called when a pike becomes active (head is placed).
     */
    //? if fabric {
    public static void registerPike(ServerWorld level, BlockPos pos, PikeTier tier, EntityType<?> entityType) {
        RegistryKey<World> dimension = level.getRegistryKey();
        //?} else {
    /*public static void registerPike(ServerLevel level, BlockPos pos, PikeTier tier, EntityType<?> entityType) {
        ResourceKey<Level> dimension = level.dimension();
        *///?}
        
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
    //? if fabric {
    public static void unregisterPike(ServerWorld level, BlockPos pos) {
        RegistryKey<World> dimension = level.getRegistryKey();
        //?} else {
    /*public static void unregisterPike(ServerLevel level, BlockPos pos) {
        ResourceKey<Level> dimension = level.dimension();
        *///?}
        
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
    //? if fabric {
    public static void updatePikeEntityType(ServerWorld level, BlockPos pos, EntityType<?> newEntityType) {
        RegistryKey<World> dimension = level.getRegistryKey();
        //?} else {
    /*public static void updatePikeEntityType(ServerLevel level, BlockPos pos, EntityType<?> newEntityType) {
        ResourceKey<Level> dimension = level.dimension();
        *///?}
        
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
    //? if fabric {
    public static void onChunkUnload(ServerWorld level, int chunkX, int chunkZ) {
        RegistryKey<World> dimension = level.getRegistryKey();
        //?} else {
    /*public static void onChunkUnload(ServerLevel level, int chunkX, int chunkZ) {
        ResourceKey<Level> dimension = level.dimension();
        *///?}
        
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
    //? if fabric {
    public static void onChunkLoad(ServerWorld level, WorldChunk chunk) {
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (!(blockEntity instanceof PikeBlockEntity)) {
                continue;
            }
            BlockPos pos = blockEntity.getPos();
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof PikeBlock pikeBlock)) {
                continue;
            }
            // Only register if the pike is activated
            if (!state.get(PikeBlock.ACTIVATED)) {
                continue;
            }
            // Get the entity type from the head above
            BlockState headState = level.getBlockState(pos.up());
            EntityType<?> entityType = PikeBlockEntity.getHeadTypeFromBlockState(headState);
            if (entityType == null) {
                continue;
            }
            registerPike(level, pos, pikeBlock.tier(), entityType);
        }
    }
    //?} else {
    /*public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (!(blockEntity instanceof PikeBlockEntity)) {
                continue;
            }
            BlockPos pos = blockEntity.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof PikeBlock pikeBlock)) {
                continue;
            }
            // Only register if the pike is activated
            if (!state.getValue(PikeBlock.ACTIVATED)) {
                continue;
            }
            // Get the entity type from the head above
            BlockState headState = level.getBlockState(pos.above());
            EntityType<?> entityType = PikeBlockEntity.getHeadTypeFromBlockState(headState);
            if (entityType == null) {
                continue;
            }
            registerPike(level, pos, pikeBlock.tier(), entityType);
        }
    }
    *///?}

    /**
     * Clears the entire registry for a dimension.
     * Called when a world is unloaded.
     */
    //? if fabric {
    public static void onWorldUnload(RegistryKey<World> dimension) {
        //?} else {
    /*public static void onWorldUnload(ResourceKey<Level> dimension) {
        *///?}
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
    //? if fabric {
    public static PikeData getBlockingPike(ServerWorld level, BlockPos spawnPos, EntityType<?> entityType) {
        RegistryKey<World> dimension = level.getRegistryKey();
        //?} else {
    /*public static PikeData getBlockingPike(ServerLevel level, BlockPos spawnPos, EntityType<?> entityType) {
        ResourceKey<Level> dimension = level.dimension();
        *///?}
        
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
                        // Check if entity type matches
                        if (pike.entityType() != entityType) {
                            continue;
                        }
                        
                        // Check if spawn is within this pike's range
                        int chunkDistance = Math.max(
                            Math.abs(pike.chunkX() - spawnChunkX),
                            Math.abs(pike.chunkZ() - spawnChunkZ)
                        );
                        
                        if (chunkDistance <= pike.tier().chunkRadius()) {
                            return pike;
                        }
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * Gets the number of registered pikes for debugging purposes.
     */
    public static int getRegisteredPikeCount() {
        int count = 0;
        for (Map<Long, List<PikeData>> dimensionPikes : pikesByDimension.values()) {
            for (List<PikeData> chunkPikes : dimensionPikes.values()) {
                count += chunkPikes.size();
            }
        }
        return count;
    }
}
