package jackperry2187.effigies.block.entity;

import jackperry2187.effigies.SpawnPreventionHandler;
import jackperry2187.effigies.block.AntiPikeBlock;
import jackperry2187.effigies.registry.ModBlockEntities;

//? if fabric {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
*///?}

public class AntiPikeBlockEntity extends BlockEntity {
    private static final int MIN_SPAWN_DELAY = 100;
    private static final int MAX_SPAWN_DELAY = 400;
    private static final int MAX_ATTEMPTS_PER_MOB = 4;
    private static final int SPAWN_RANGE = 4;
    private static final int MAX_NEARBY_ENTITIES = 6;
    private static final int ACTIVATION_RANGE = 16;
    private static final int AMBIENT_PARTICLE_MIN_INTERVAL = 15;
    private static final int AMBIENT_PARTICLE_MAX_INTERVAL = 30;

    private int spawnDelay = -1;
    private int particleTimer = 0;

    public AntiPikeBlockEntity(BlockPos pos, BlockState state) {
        //? if fabric {
        super(ModBlockEntities.antiPike(), pos, state);
        //?} else {
        /*super(ModBlockEntities.antiPike(), pos, state);
        *///?}
    }

    //? if fabric {
    public void serverTick(ServerWorld world, BlockPos pos, BlockState state) {
        if (!state.get(AntiPikeBlock.ACTIVATED)) {
            return;
        }

        boolean playerNearby = world.isPlayerInRange(
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ACTIVATION_RANGE
        );
        if (!playerNearby) {
            return;
        }

        EntityType<?> entityType = PikeBlockEntity.getHeadTypeFromWorld(world, pos.up());
        boolean canSpawn = entityType != null;

        if (canSpawn) {
            Box nearbyBox = new Box(
                pos.getX() - SPAWN_RANGE, pos.getY() - SPAWN_RANGE, pos.getZ() - SPAWN_RANGE,
                pos.getX() + SPAWN_RANGE + 1, pos.getY() + SPAWN_RANGE + 1, pos.getZ() + SPAWN_RANGE + 1
            );
            long nearbyCount = world.getEntitiesByType(entityType, nearbyBox, e -> true).size();
            if (nearbyCount >= MAX_NEARBY_ENTITIES) {
                canSpawn = false;
            }
        }

        if (canSpawn) {
            if (particleTimer <= 0) {
                world.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                    3, 0.15, 0.2, 0.15, 0.005
                );
                particleTimer = AMBIENT_PARTICLE_MIN_INTERVAL
                    + world.random.nextInt(AMBIENT_PARTICLE_MAX_INTERVAL - AMBIENT_PARTICLE_MIN_INTERVAL);
            } else {
                particleTimer--;
            }
        }

        if (spawnDelay == -1) {
            resetSpawnDelay(world);
            return;
        }

        if (spawnDelay > 0) {
            spawnDelay--;
            return;
        }

        if (!canSpawn) {
            resetSpawnDelay(world);
            return;
        }

        int spawnCount = getWeightedSpawnCount(world.random);
        int totalSpawned = 0;

        for (int mob = 0; mob < spawnCount; mob++) {
            for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_MOB; attempt++) {
                double x = pos.getX() + (world.random.nextDouble() - 0.5) * SPAWN_RANGE * 2 + 0.5;
                double z = pos.getZ() + (world.random.nextDouble() - 0.5) * SPAWN_RANGE * 2 + 0.5;
                double y = pos.getY() + world.random.nextInt(3) - 1;

                BlockPos spawnPos = BlockPos.ofFloored(x, y, z);

                if (!world.getBlockState(spawnPos).isAir()) {
                    continue;
                }
                if (!world.getBlockState(spawnPos.down()).isSolidBlock(world, spawnPos.down())) {
                    continue;
                }

                SpawnPreventionHandler.enableAntiPikeBypass();
                Entity entity;
                try {
                    entity = entityType.spawn(world, spawnPos, SpawnReason.SPAWNER);
                } finally {
                    SpawnPreventionHandler.disableAntiPikeBypass();
                }
                if (entity != null) {
                    totalSpawned++;
                    world.spawnParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        entity.getX(), entity.getY() + 0.5, entity.getZ(),
                        10, 0.3, 0.3, 0.3, 0.02
                    );
                    break;
                }
            }
        }

        if (totalSpawned > 0) {
            world.spawnParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                5 + totalSpawned * 3, 0.2, 0.3, 0.2, 0.01
            );
        }

        resetSpawnDelay(world);
    }

    private void resetSpawnDelay(ServerWorld world) {
        spawnDelay = MIN_SPAWN_DELAY + world.random.nextInt(MAX_SPAWN_DELAY - MIN_SPAWN_DELAY);
        markDirty();
    }

    private static int getWeightedSpawnCount(net.minecraft.util.math.random.Random random) {
        float roll = random.nextFloat();
        if (roll < 0.05f) return 1;
        if (roll < 0.25f) return 2;
        if (roll < 0.70f) return 3;
        return 4;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("SpawnDelay", spawnDelay);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        spawnDelay = view.getInt("SpawnDelay", -1);
    }
    //?} else {
    /*public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.getValue(AntiPikeBlock.ACTIVATED)) {
            return;
        }

        boolean playerNearby = level.hasNearbyAlivePlayer(
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ACTIVATION_RANGE
        );
        if (!playerNearby) {
            return;
        }

        EntityType<?> entityType = PikeBlockEntity.getHeadTypeFromWorld(level, pos.above());
        boolean canSpawn = entityType != null;

        if (canSpawn) {
            AABB nearbyBox = new AABB(
                pos.getX() - SPAWN_RANGE, pos.getY() - SPAWN_RANGE, pos.getZ() - SPAWN_RANGE,
                pos.getX() + SPAWN_RANGE + 1, pos.getY() + SPAWN_RANGE + 1, pos.getZ() + SPAWN_RANGE + 1
            );
            long nearbyCount = level.getEntities(entityType, nearbyBox, e -> true).size();
            if (nearbyCount >= MAX_NEARBY_ENTITIES) {
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
                    + level.random.nextInt(AMBIENT_PARTICLE_MAX_INTERVAL - AMBIENT_PARTICLE_MIN_INTERVAL);
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

        int spawnCount = getWeightedSpawnCount(level.random);
        int totalSpawned = 0;

        for (int mob = 0; mob < spawnCount; mob++) {
            for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_MOB; attempt++) {
                double x = pos.getX() + (level.random.nextDouble() - 0.5) * SPAWN_RANGE * 2 + 0.5;
                double z = pos.getZ() + (level.random.nextDouble() - 0.5) * SPAWN_RANGE * 2 + 0.5;
                double y = pos.getY() + level.random.nextInt(3) - 1;

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
        spawnDelay = MIN_SPAWN_DELAY + level.random.nextInt(MAX_SPAWN_DELAY - MIN_SPAWN_DELAY);
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
    *///?}
}
