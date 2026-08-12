package jackperry2187.effigies;

import jackperry2187.effigies.config.ConfigSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.chunk.LevelChunk;
// Debug imports - uncomment when re-enabling debug messages
// import net.minecraft.ChatFormatting;
// import net.minecraft.core.registries.BuiltInRegistries;
// import net.minecraft.network.chat.Component;
// import net.minecraft.server.level.ServerPlayer;

//? if fabric {
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
//?}

//? if mc12011 && fabric {
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
//?} else if mc261 && fabric {
/*import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
*///?}

//? if neoforge {
/*import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
*///?}

public final class SpawnPreventionHandler {
    private SpawnPreventionHandler() {
    }

    private static boolean antiPikeBypass = false;

    /**
     * Enables the bypass flag so the next entity spawn is not blocked by pikes.
     * Must be called on the server thread before spawning an entity from an Anti-Pike.
     */
    public static void enableAntiPikeBypass() {
        antiPikeBypass = true;
    }

    /**
     * Disables the bypass flag after spawning completes.
     */
    public static void disableAntiPikeBypass() {
        antiPikeBypass = false;
    }

    //? if fabric {
    public static void registerFabric() {
        // Register mob spawn prevention
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Mob mob)) {
                return;
            }
            if (!(level instanceof ServerLevel serverLevel)) {
                return;
            }
            if (mob.tickCount > 0) {
                return;
            }
            if (isSpawnBlocked(serverLevel, mob.blockPosition(), mob.getType())) {
                mob.discard();
            }
        });

        // Register chunk load event to scan for active pikes
        //? if mc12011 {
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (chunk instanceof LevelChunk levelChunk) {
                PikeRegistry.onChunkLoad(world, levelChunk);
            }
        });
        //?} else {
        /*ServerChunkEvents.CHUNK_LOAD.register((world, chunk, generated) -> {
            if (chunk instanceof LevelChunk levelChunk) {
                PikeRegistry.onChunkLoad(world, levelChunk);
            }
        });
        *///?}

        // Register chunk unload event to clear pikes from registry
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            //? if mc12011 {
            PikeRegistry.onChunkUnload(world, chunk.getPos().x, chunk.getPos().z);
            //?} else {
            /*PikeRegistry.onChunkUnload(world, chunk.getPos().x(), chunk.getPos().z());
            *///?}
        });

        // Register world unload event to clear dimension data
        //? if mc12011 {
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            PikeRegistry.onWorldUnload(world.dimension());
        });
        //?} else {
        /*ServerLevelEvents.UNLOAD.register((server, world) -> {
            PikeRegistry.onWorldUnload(world.dimension());
        });
        *///?}
    }
    //?} else {
    /*@SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob mob)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (event.loadedFromDisk()) {
            return;
        }
        if (isSpawnBlocked(serverLevel, mob.blockPosition(), mob.getType())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(event.getChunk() instanceof LevelChunk levelChunk)) {
            return;
        }
        PikeRegistry.onChunkLoad(serverLevel, levelChunk);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        //? if mc12011 {
        PikeRegistry.onChunkUnload(serverLevel, event.getChunk().getPos().x, event.getChunk().getPos().z);
        //?} else {
        PikeRegistry.onChunkUnload(serverLevel, event.getChunk().getPos().x(), event.getChunk().getPos().z());
        //?}
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        PikeRegistry.onWorldUnload(serverLevel.dimension());
    }
    *///?}

    private static boolean isSpawnBlocked(ServerLevel level, BlockPos spawnPos, EntityType<?> entityType) {
        if (antiPikeBypass) {
            return false;
        }

        // Only apply spawn prevention in whitelisted dimensions (empty whitelist = all)
        if (!ConfigSettings.isDimensionAllowed(level.dimension().identifier().toString())) {
            return false;
        }

        // Use the efficient pike registry for spawn checking
        PikeRegistry.PikeData blockingPike = PikeRegistry.getBlockingPike(level, spawnPos, entityType);
        
        if (blockingPike != null) {
            // Spawn particles at the pike to indicate it blocked a spawn
            BlockPos pikePos = blockingPike.pos();
            level.sendParticles(
                ParticleTypes.FLAME,
                pikePos.getX() + 0.5,
                pikePos.getY() + 1.5,  // At head height
                pikePos.getZ() + 0.5,
                1,      // particle count
                0.3,    // deltaX
                0.3,    // deltaY
                0.3,    // deltaZ
                0.0     // speed
            );

            // Debug: Send message to nearby players (commented out for release)
            // String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
            // for (ServerPlayer player : level.players()) {
            //     if (player.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) <= 64 * 64) {
            //         player.displayClientMessage(
            //             Component.literal("[Effigies] ").withStyle(ChatFormatting.GOLD)
            //                 .append(Component.literal("Prevented ").withStyle(ChatFormatting.RED))
            //                 .append(Component.literal(entityName).withStyle(ChatFormatting.YELLOW))
            //                 .append(Component.literal(" spawn at ").withStyle(ChatFormatting.RED))
            //                 .append(Component.literal("(" + spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ() + ")").withStyle(ChatFormatting.AQUA))
            //                 .append(Component.literal(" due to effigy at ").withStyle(ChatFormatting.RED))
            //                 .append(Component.literal("(" + pikePos.getX() + ", " + pikePos.getY() + ", " + pikePos.getZ() + ")").withStyle(ChatFormatting.AQUA)),
            //             false
            //         );
            //     }
            // }
            return true;
        }

        return false;
    }
}
