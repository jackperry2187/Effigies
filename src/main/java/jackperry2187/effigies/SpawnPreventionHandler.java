package jackperry2187.effigies;

import jackperry2187.effigies.block.PikeBlock;
import jackperry2187.effigies.block.entity.PikeBlockEntity;

//? if fabric {
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
*///?}

public final class SpawnPreventionHandler {
    private SpawnPreventionHandler() {
    }

    //? if fabric {
    public static void registerFabric() {
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
    *///?}

    //? if fabric {
    private static boolean isSpawnBlocked(ServerWorld level, BlockPos spawnPos, EntityType<?> entityType) {
        int spawnChunkX = spawnPos.getX() >> 4;
        int spawnChunkZ = spawnPos.getZ() >> 4;

        int maxRadius = 0;
        for (PikeTier tier : PikeTier.values()) {
            maxRadius = Math.max(maxRadius, tier.chunkRadius());
        }

        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dz = -maxRadius; dz <= maxRadius; dz++) {
                int chunkX = spawnChunkX + dx;
                int chunkZ = spawnChunkZ + dz;
                for (BlockEntity blockEntity : level.getChunk(chunkX, chunkZ).getBlockEntities().values()) {
                    if (!(blockEntity instanceof PikeBlockEntity pike) || !pike.hasHead()) {
                        continue;
                    }
                    if (pike.getHeadType() != entityType) {
                        continue;
                    }
                    BlockPos pikePos = pike.getPos();
                    int pikeChunkX = pikePos.getX() >> 4;
                    int pikeChunkZ = pikePos.getZ() >> 4;
                    PikeTier tier = getTierFromBlock(pike.getCachedState().getBlock());
                    if (tier == null) {
                        continue;
                    }
                    int chunkDistance = Math.max(Math.abs(pikeChunkX - spawnChunkX), Math.abs(pikeChunkZ - spawnChunkZ));
                    if (chunkDistance <= tier.chunkRadius()) {
                        // Send message to nearby players
                        String entityName = Registries.ENTITY_TYPE.getId(entityType).getPath();
                        for (ServerPlayerEntity player : level.getPlayers()) {
                            if (player.squaredDistanceTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) <= 64 * 64) {
                                player.sendMessage(
                                    Text.literal("[Effigies] ").formatted(Formatting.GOLD)
                                        .append(Text.literal("Prevented ").formatted(Formatting.RED))
                                        .append(Text.literal(entityName).formatted(Formatting.YELLOW))
                                        .append(Text.literal(" spawn at ").formatted(Formatting.RED))
                                        .append(Text.literal("(" + spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ() + ")").formatted(Formatting.AQUA))
                                        .append(Text.literal(" due to effigy at ").formatted(Formatting.RED))
                                        .append(Text.literal("(" + pikePos.getX() + ", " + pikePos.getY() + ", " + pikePos.getZ() + ")").formatted(Formatting.AQUA)),
                                    false
                                );
                            }
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }
    //?} else {
    /*private static boolean isSpawnBlocked(ServerLevel level, BlockPos spawnPos, EntityType<?> entityType) {
        int spawnChunkX = spawnPos.getX() >> 4;
        int spawnChunkZ = spawnPos.getZ() >> 4;

        int maxRadius = 0;
        for (PikeTier tier : PikeTier.values()) {
            maxRadius = Math.max(maxRadius, tier.chunkRadius());
        }

        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dz = -maxRadius; dz <= maxRadius; dz++) {
                int chunkX = spawnChunkX + dx;
                int chunkZ = spawnChunkZ + dz;
                for (BlockEntity blockEntity : level.getChunk(chunkX, chunkZ).getBlockEntities().values()) {
                    if (!(blockEntity instanceof PikeBlockEntity pike) || !pike.hasHead()) {
                        continue;
                    }
                    if (pike.getHeadType() != entityType) {
                        continue;
                    }
                    BlockPos pikePos = pike.getBlockPos();
                    int pikeChunkX = pikePos.getX() >> 4;
                    int pikeChunkZ = pikePos.getZ() >> 4;
                    PikeTier tier = getTierFromBlock(pike.getBlockState().getBlock());
                    if (tier == null) {
                        continue;
                    }
                    int chunkDistance = Math.max(Math.abs(pikeChunkX - spawnChunkX), Math.abs(pikeChunkZ - spawnChunkZ));
                    if (chunkDistance <= tier.chunkRadius()) {
                        // Send message to nearby players
                        String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
                        for (ServerPlayer player : level.players()) {
                            if (player.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) <= 64 * 64) {
                                player.displayClientMessage(
                                    Component.literal("[Effigies] ").withStyle(ChatFormatting.GOLD)
                                        .append(Component.literal("Prevented ").withStyle(ChatFormatting.RED))
                                        .append(Component.literal(entityName).withStyle(ChatFormatting.YELLOW))
                                        .append(Component.literal(" spawn at ").withStyle(ChatFormatting.RED))
                                        .append(Component.literal("(" + spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ() + ")").withStyle(ChatFormatting.AQUA))
                                        .append(Component.literal(" due to effigy at ").withStyle(ChatFormatting.RED))
                                        .append(Component.literal("(" + pikePos.getX() + ", " + pikePos.getY() + ", " + pikePos.getZ() + ")").withStyle(ChatFormatting.AQUA)),
                                    false
                                );
                            }
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }
    *///?}

    //? if fabric {
    private static PikeTier getTierFromBlock(net.minecraft.block.Block block) {
        if (block instanceof PikeBlock pikeBlock) {
            return pikeBlock.tier();
        }
        return null;
    }
    //?} else {
    /*private static PikeTier getTierFromBlock(net.minecraft.world.level.block.Block block) {
        if (block instanceof PikeBlock pikeBlock) {
            return pikeBlock.tier();
        }
        return null;
    }
    *///?}
}
