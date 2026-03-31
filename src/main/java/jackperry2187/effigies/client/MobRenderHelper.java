package jackperry2187.effigies.client;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

//? if fabric {
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
//?} else {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
*///?}

public final class MobRenderHelper {
    private MobRenderHelper() {}

    private static final Map<String, Entity> entityCache = new HashMap<>();
    private static Object lastCachedWorld = null;

    private static final long NANOS_START = System.nanoTime();
    private static final long ROTATION_PERIOD_MS = 9000L;
    private static final float DEGREES_PER_MS = 360.0f / ROTATION_PERIOD_MS;
    private static final float MAX_GUI_SCALE = 20.0f;

    @Nullable
    //? if fabric {
    private static Entity getOrCreateEntity(String entityTypeId, World world) {
    //?} else {
    /*private static Entity getOrCreateEntity(String entityTypeId, Level world) {
    *///?}
        if (lastCachedWorld != world) {
            entityCache.clear();
            lastCachedWorld = world;
        }

        Entity cached = entityCache.get(entityTypeId);
        if (cached != null) {
            return cached;
        }

        Identifier id = Identifier.tryParse(entityTypeId);
        //? if fabric {
        if (id == null || !Registries.ENTITY_TYPE.containsId(id)) return null;
        EntityType<?> type = Registries.ENTITY_TYPE.get(id);
        Entity entity = type.create(world, SpawnReason.COMMAND);
        //?} else {
        /*if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return null;
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
        Entity entity = type.create(world, EntitySpawnReason.COMMAND);
        *///?}
        if (entity != null) {
            entityCache.put(entityTypeId, entity);
        }
        return entity;
    }

    /**
     * Renders a spinning mob entity in GUI space, scaled to fit within areaSize.
     * Rotation is applied via the body quaternion (not entity fields) so that all
     * model parts — including multi-head entities like the Wither and complex
     * models like the Ender Dragon — spin uniformly.
     */
    //? if fabric {
    public static void renderEntity(DrawContext context, String entityTypeId, int centerX, int centerY, int areaSize) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Entity entity = getOrCreateEntity(entityTypeId, client.world);
        if (!(entity instanceof LivingEntity living)) return;

        long elapsedMs = (System.nanoTime() - NANOS_START) / 1_000_000L;
        float spinAngle = (elapsedMs % ROTATION_PERIOD_MS) * DEGREES_PER_MS;

        living.bodyYaw = 0;
        living.lastBodyYaw = 0;
        living.headYaw = 0;
        living.lastHeadYaw = 0;
        living.setYaw(0);
        living.setPitch(0);

        EntityRenderManager renderManager = client.getEntityRenderDispatcher();
        EntityRenderState renderState = renderManager.getAndUpdateRenderState(living, 1.0f);
        renderState.light = 15728880;
        renderState.shadowPieces.clear();
        renderState.shadowRadius = 0;
        renderState.outlineColor = 0;
        renderState.displayName = null;

        if (renderState instanceof LivingEntityRenderState livingState) {
            livingState.bodyYaw = 0;
            livingState.relativeHeadYaw = 0;
            livingState.limbSwingAnimationProgress = 0;
            livingState.limbSwingAmplitude = 0;
            livingState.shaking = false;
        }

        if (renderState instanceof WitherEntityRenderState witherState) {
            for (int i = 0; i < witherState.sideHeadYaws.length; i++) {
                witherState.sideHeadYaws[i] = 0;
                witherState.sideHeadPitches[i] = 0;
            }
        }

        float maxDim = Math.max(living.getHeight(), living.getWidth());
        float guiScale = Math.min(MAX_GUI_SCALE, (areaSize * 0.8f) / Math.max(maxDim, 0.5f));

        int tx = Math.round(context.getMatrices().m20());
        int ty = Math.round(context.getMatrices().m21());

        int halfSize = areaSize / 2;
        int x1 = centerX - halfSize + tx;
        int y1 = centerY - halfSize + ty;
        int x2 = centerX + halfSize + tx;
        int y2 = centerY + halfSize + ty;

        float spinRadians = (float) Math.toRadians(spinAngle);
        Quaternionf bodyRotation = new Quaternionf().rotateZ((float) Math.PI).rotateY(spinRadians);
        Quaternionf headRotation = new Quaternionf();
        Vector3f offset = new Vector3f(0.0f, renderState.height / 2.0f, 0.0f);

        context.addEntity(renderState, guiScale, offset, bodyRotation, headRotation, x1, y1, x2, y2);
    }
    //?} else {
    /*public static void renderEntity(GuiGraphics graphics, String entityTypeId, int centerX, int centerY, int areaSize) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        Entity entity = getOrCreateEntity(entityTypeId, client.level);
        if (!(entity instanceof LivingEntity living)) return;

        long elapsedMs = (System.nanoTime() - NANOS_START) / 1_000_000L;
        float spinAngle = (elapsedMs % ROTATION_PERIOD_MS) * DEGREES_PER_MS;

        living.yBodyRot = 0;
        living.yBodyRotO = 0;
        living.yHeadRot = 0;
        living.yHeadRotO = 0;
        living.setYRot(0);
        living.setXRot(0);

        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        EntityRenderState renderState = dispatcher.extractEntity(living, 1.0f);
        renderState.lightCoords = 15728880;
        renderState.shadowPieces.clear();
        renderState.shadowRadius = 0;
        renderState.outlineColor = 0;
        renderState.nameTag = null;

        float scaleAdjustedHeight = renderState.boundingBoxHeight;
        float scaleAdjustedWidth = scaleAdjustedHeight;

        if (renderState instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = 0;
            livingState.yRot = 0;
            livingState.xRot = 0;
            livingState.walkAnimationPos = 0;
            livingState.walkAnimationSpeed = 0;
            livingState.isFullyFrozen = false;

            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1;

            scaleAdjustedHeight = livingState.boundingBoxHeight;
            scaleAdjustedWidth = livingState.boundingBoxWidth;
        }

        if (renderState instanceof WitherRenderState witherState) {
            for (int i = 0; i < witherState.yHeadRots.length; i++) {
                witherState.yHeadRots[i] = 0;
                witherState.xHeadRots[i] = 0;
            }
        }

        float maxDim = Math.max(scaleAdjustedHeight, scaleAdjustedWidth);
        float guiScale = Math.min(MAX_GUI_SCALE, (areaSize * 0.8f) / Math.max(maxDim, 0.5f));

        int tx = Math.round(graphics.pose().m20());
        int ty = Math.round(graphics.pose().m21());

        int halfSize = areaSize / 2;
        int x1 = centerX - halfSize + tx;
        int y1 = centerY - halfSize + ty;
        int x2 = centerX + halfSize + tx;
        int y2 = centerY + halfSize + ty;

        float spinRadians = (float) Math.toRadians(spinAngle);
        Quaternionf bodyRot = new Quaternionf().rotateZ((float) Math.PI).rotateY(spinRadians);
        Quaternionf headRot = new Quaternionf();
        Vector3f offset = new Vector3f(0.0f, scaleAdjustedHeight / 2.0f, 0.0f);

        graphics.submitEntityRenderState(renderState, guiScale, offset, bodyRot, headRot, x1, y1, x2, y2);
    }
    *///?}
}
