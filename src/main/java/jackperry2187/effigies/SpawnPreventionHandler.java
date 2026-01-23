package jackperry2187.effigies;

//? if fabric {
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
// Debug imports - uncomment when re-enabling debug messages
// import net.minecraft.registry.Registries;
// import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
// import net.minecraft.text.Text;
// import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.particle.ParticleTypes;
//?} else {
/*// Debug imports - uncomment when re-enabling debug messages
// import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
// import net.minecraft.core.registries.BuiltInRegistries;
// import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
// import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
*///?}

public final class SpawnPreventionHandler {
    private SpawnPreventionHandler() {
    }

    //? if fabric {
    public static void registerFabric() {
        // Register mob spawn prevention
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof MobEntity mob)) {
                return;
            }
            if (!(level instanceof ServerWorld serverWorld)) {
                return;
            }
            if (mob.age > 0) {
                return;
            }
            if (isSpawnBlocked(serverWorld, mob.getBlockPos(), mob.getType())) {
                mob.discard();
            }
        });

        // Register chunk load event to scan for active pikes
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (chunk instanceof WorldChunk worldChunk) {
                PikeRegistry.onChunkLoad(world, worldChunk);
            }
        });

        // Register chunk unload event to clear pikes from registry
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            PikeRegistry.onChunkUnload(world, chunk.getPos().x, chunk.getPos().z);
        });

        // Register world unload event to clear dimension data
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            PikeRegistry.onWorldUnload(world.getRegistryKey());
        });
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
        PikeRegistry.onChunkUnload(serverLevel, event.getChunk().getPos().x, event.getChunk().getPos().z);
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        PikeRegistry.onWorldUnload(serverLevel.dimension());
    }
    *///?}

    //? if fabric {
    private static boolean isSpawnBlocked(ServerWorld level, BlockPos spawnPos, EntityType<?> entityType) {
        // Use the efficient pike registry for spawn checking
        PikeRegistry.PikeData blockingPike = PikeRegistry.getBlockingPike(level, spawnPos, entityType);
        
        if (blockingPike != null) {
            // Spawn particles at the pike to indicate it blocked a spawn
            BlockPos pikePos = blockingPike.pos();
            level.spawnParticles(
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
            // String entityName = Registries.ENTITY_TYPE.getId(entityType).getPath();
            // for (ServerPlayerEntity player : level.getPlayers()) {
            //     if (player.squaredDistanceTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) <= 64 * 64) {
            //         player.sendMessage(
            //             Text.literal("[Effigies] ").formatted(Formatting.GOLD)
            //                 .append(Text.literal("Prevented ").formatted(Formatting.RED))
            //                 .append(Text.literal(entityName).formatted(Formatting.YELLOW))
            //                 .append(Text.literal(" spawn at ").formatted(Formatting.RED))
            //                 .append(Text.literal("(" + spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ() + ")").formatted(Formatting.AQUA))
            //                 .append(Text.literal(" due to effigy at ").formatted(Formatting.RED))
            //                 .append(Text.literal("(" + pikePos.getX() + ", " + pikePos.getY() + ", " + pikePos.getZ() + ")").formatted(Formatting.AQUA)),
            //             false
            //         );
            //     }
            // }
            return true;
        }

        return false;
    }
    //?} else {
    /*private static boolean isSpawnBlocked(ServerLevel level, BlockPos spawnPos, EntityType<?> entityType) {
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
    *///?}
}
