package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.SpawnPreventionHandler;
import jackperry2187.effigies.block.AntiPikeBlock;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class AntiPikeBlockEntity extends BlockEntity {
    private static final int MAX_ATTEMPTS_PER_MOB = 4;
    private static final int AMBIENT_PARTICLE_MIN_INTERVAL = 15;
    private static final int AMBIENT_PARTICLE_MAX_INTERVAL = 30;

    private int spawnDelay = -1;
    private int particleTimer = 0;

    public AntiPikeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.antiPike(), pos, state);
    }

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        //? if mc12011 {
        var random = level.random;
        //?} else {
        /*var random = level.getRandom();
        *///?}

        if (!state.getValue(AntiPikeBlock.ACTIVATED)) {
            return;
        }

        boolean playerNearby = level.hasNearbyAlivePlayer(
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ConfigSettings.antiPikeActivationRange
        );
        if (!playerNearby) {
            return;
        }

        EntityType<?> entityType = PikeBlockEntity.getHeadTypeFromWorld(level, pos.above());
        boolean canSpawn = entityType != null;

        if (canSpawn) {
            int spawnRange = ConfigSettings.antiPikeSpawnRange;
            AABB nearbyBox = new AABB(
                pos.getX() - spawnRange, pos.getY() - spawnRange, pos.getZ() - spawnRange,
                pos.getX() + spawnRange + 1, pos.getY() + spawnRange + 1, pos.getZ() + spawnRange + 1
            );
            long nearbyCount = level.getEntities(entityType, nearbyBox, e -> true).size();
            if (nearbyCount >= ConfigSettings.antiPikeMaxNearbyEntities) {
                canSpawn = false;
            }
        }

        if (canSpawn) {
            if (particleTimer <= 0) {
                level.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                    3, 0.15, 0.2, 0.15, 0.005
                );
                particleTimer = AMBIENT_PARTICLE_MIN_INTERVAL
                    + random.nextInt(AMBIENT_PARTICLE_MAX_INTERVAL - AMBIENT_PARTICLE_MIN_INTERVAL);
            } else {
                particleTimer--;
            }
        }

        if (spawnDelay == -1) {
            resetSpawnDelay(level);
            return;
        }

        if (spawnDelay > 0) {
            spawnDelay--;
            return;
        }

        if (!canSpawn) {
            resetSpawnDelay(level);
            return;
        }

        int spawnCount = getWeightedSpawnCount(random);
        int totalSpawned = 0;

        int spawnRange = ConfigSettings.antiPikeSpawnRange;
        for (int mob = 0; mob < spawnCount; mob++) {
            for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_MOB; attempt++) {
                double x = pos.getX() + (random.nextDouble() - 0.5) * spawnRange * 2 + 0.5;
                double z = pos.getZ() + (random.nextDouble() - 0.5) * spawnRange * 2 + 0.5;
                double y = pos.getY() + random.nextInt(3) - 1;

                BlockPos spawnPos = BlockPos.containing(x, y, z);

                if (!level.getBlockState(spawnPos).isAir()) {
                    continue;
                }
                if (!level.getBlockState(spawnPos.below()).isFaceSturdy(level, spawnPos.below(), net.minecraft.core.Direction.UP)) {
                    continue;
                }

                SpawnPreventionHandler.enableAntiPikeBypass();
                Entity entity;
                try {
                    entity = entityType.spawn(level, spawnPos, EntitySpawnReason.SPAWNER);
                } finally {
                    SpawnPreventionHandler.disableAntiPikeBypass();
                }
                if (entity != null) {
                    totalSpawned++;
                    level.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        entity.getX(), entity.getY() + 0.5, entity.getZ(),
                        10, 0.3, 0.3, 0.3, 0.02
                    );
                    break;
                }
            }
        }

        if (totalSpawned > 0) {
            level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                5 + totalSpawned * 3, 0.2, 0.3, 0.2, 0.01
            );
        }

        resetSpawnDelay(level);
    }

    private void resetSpawnDelay(ServerLevel level) {
        int minDelay = ConfigSettings.antiPikeMinSpawnDelay;
        int maxDelay = ConfigSettings.antiPikeMaxSpawnDelay;
        // Guard against a misconfigured range where max <= min
        int spread = Math.max(1, maxDelay - minDelay);
        //? if mc12011 {
        spawnDelay = minDelay + level.random.nextInt(spread);
        //?} else {
        /*spawnDelay = minDelay + level.getRandom().nextInt(spread);
        *///?}
        setChanged();
    }

    private static int getWeightedSpawnCount(net.minecraft.util.RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.05f) return 1;
        if (roll < 0.25f) return 2;
        if (roll < 0.70f) return 3;
        return 4;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("SpawnDelay", spawnDelay);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        spawnDelay = input.getIntOr("SpawnDelay", -1);
    }
}
