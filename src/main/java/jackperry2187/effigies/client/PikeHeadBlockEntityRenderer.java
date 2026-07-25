package jackperry2187.effigies.client;

import jackperry2187.effigies.block.AntiPikeBlock;
import jackperry2187.effigies.block.PikeBlock;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import jackperry2187.effigies.config.ConfigSettings;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
//? if mc12011 {
import net.minecraft.client.renderer.state.CameraRenderState;
//?} else {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?}
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class PikeHeadBlockEntityRenderer
    implements BlockEntityRenderer<PikeHeadBlockEntity, PikeHeadBlockEntityRenderer.PikeHeadRenderState>
{
    private final EntityModelSet modelSet;

    public PikeHeadBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.modelSet = ctx.entityModelSet();
    }

    @Override
    public PikeHeadRenderState createRenderState() {
        return new PikeHeadRenderState();
    }

    @Override
    public void extractRenderState(PikeHeadBlockEntity entity, PikeHeadRenderState state, float partialTick,
                                   Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        state.storedState = entity.getStoredBlockState();
        state.rotation = entity.getRotation();
        updateHologramState(entity, state, partialTick);
    }

    @Override
    public void submit(PikeHeadRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (state.storedState == null) return;

        float yaw = state.rotation * 22.5f;

        if (state.storedState.getBlock() instanceof AbstractSkullBlock skull) {
            SkullBlock.Type skullType = skull.getType();
            SkullModelBase model = SkullBlockRenderer.createModel(modelSet, skullType);
            RenderType renderType = SkullBlockRenderer.getSkullRenderType(skullType, null);
            if (model != null && renderType != null) {
                //? if mc12011 {
                SkullBlockRenderer.submitSkull(null, yaw, 0f, poseStack, submitNodeCollector,
                    state.lightCoords, model, renderType, 0, state.breakProgress);
                //?} else {
                /*poseStack.pushPose();
                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                SkullBlockRenderer.submitSkull(0f, poseStack, submitNodeCollector,
                    state.lightCoords, model, renderType, 0, state.breakProgress);
                poseStack.popPose();
                *///?}
            }
        } else {
            //? if mc12011 {
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            poseStack.pushPose();
            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(-0.5, 0.0, -0.5);
            Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(state.storedState, poseStack, bufferSource,
                    state.lightCoords, OverlayTexture.NO_OVERLAY);
            bufferSource.endBatch();
            poseStack.popPose();
            //?} else {
            /*// Minecraft.getBlockRenderer() was removed in MC 26.1; non-skull pike heads are not rendered
            *///?}
        }

        if (state.isTargeted && state.mappedEntityTypeId != null) {
            renderHologram(state, poseStack, submitNodeCollector, cameraRenderState);
        }
    }

    private static void updateHologramState(PikeHeadBlockEntity entity, PikeHeadRenderState state, float tickProgress) {
        state.isTargeted = false;
        state.mappedEntityTypeId = null;
        state.tickProgress = tickProgress;

        Level level = entity.getLevel();
        if (level == null) return;

        BlockPos pikePos = entity.getBlockPos().below();
        BlockState pikeState = level.getBlockState(pikePos);
        boolean isPikeActivated = (pikeState.getBlock() instanceof PikeBlock && pikeState.getValue(PikeBlock.ACTIVATED))
            || (pikeState.getBlock() instanceof AntiPikeBlock && pikeState.getValue(AntiPikeBlock.ACTIVATED));
        if (!isPikeActivated) return;

        String storedBlockId = entity.getStoredBlockId();
        if (storedBlockId == null || storedBlockId.isEmpty()) return;
        String entityTypeId = ConfigSettings.getEntityIdForBlock(storedBlockId);
        if (entityTypeId == null) return;

        Minecraft client = Minecraft.getInstance();
        if (client.hitResult instanceof BlockHitResult blockHit) {
            BlockPos targetPos = blockHit.getBlockPos();
            if (targetPos.equals(entity.getBlockPos()) || targetPos.equals(pikePos)) {
                state.isTargeted = true;
                state.mappedEntityTypeId = entityTypeId;
                state.worldTime = level.getGameTime() + tickProgress;
            }
        }
    }

    @Nullable
    private static Entity getOrCreateEntity(String entityTypeId, Level level) {
        if (lastCachedWorld != level) {
            entityCache.clear();
            lastCachedWorld = level;
        }

        Entity cached = entityCache.get(entityTypeId);
        if (cached != null) {
            return cached;
        }

        Identifier id = Identifier.tryParse(entityTypeId);
        if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return null;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
        Entity entity = type.create(level, EntitySpawnReason.COMMAND);
        if (entity != null) {
            entityCache.put(entityTypeId, entity);
        }
        return entity;
    }

    private void renderHologram(PikeHeadRenderState state, PoseStack poseStack,
                                SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        Entity hologramEntity = getOrCreateEntity(state.mappedEntityTypeId, client.level);
        if (hologramEntity == null) return;

        float spinAngle = (state.worldTime * 2.0f) % 360.0f;

        if (hologramEntity instanceof LivingEntity living) {
            living.yBodyRot = 0;
            living.yBodyRotO = 0;
            living.yHeadRot = 0;
            living.yHeadRotO = 0;
            living.setYRot(0);
        } else {
            hologramEntity.setYRot(0);
        }

        EntityRenderDispatcher renderDispatcher = client.getEntityRenderDispatcher();
        EntityRenderState entityState = renderDispatcher.extractEntity(hologramEntity, state.tickProgress);

        entityState.ageInTicks = state.worldTime;

        if (entityState instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = 0;
            livingState.yRot = 0;
            livingState.walkAnimationPos = 0;
            livingState.walkAnimationSpeed = 0;
            livingState.isFullyFrozen = false;
        }

        if (entityState instanceof WitherRenderState witherState) {
            for (int i = 0; i < witherState.yHeadRots.length; i++) {
                witherState.yHeadRots[i] = 0;
                witherState.xHeadRots[i] = 0;
            }
        }

        entityState.shadowRadius = 0;
        entityState.nameTag = null;
        entityState.lightCoords = state.lightCoords;

        float maxDisplayHeight = 1.0f;
        float scale = Math.min(0.4f, maxDisplayHeight / Math.max(entityState.boundingBoxHeight, 0.1f));

        float bobOffset = (float) Math.sin(state.worldTime * 0.1) * 0.05f;
        poseStack.pushPose();
        //? if fabric {
        poseStack.translate(0.5, 1.1 + bobOffset, 0.5);
        //?} else {
        /*poseStack.translate(0.5, 1.2 + bobOffset, 0.5);
        *///?}
        poseStack.mulPose(Axis.YP.rotationDegrees(spinAngle));
        poseStack.scale(scale, scale, scale);

        renderDispatcher.submit(entityState, cameraRenderState, 0, 0, 0, poseStack, submitNodeCollector);

        poseStack.popPose();
    }

    private static final Map<String, Entity> entityCache = new HashMap<>();
    private static Object lastCachedWorld = null;

    public static class PikeHeadRenderState extends BlockEntityRenderState {
        @Nullable
        public BlockState storedState;
        public int rotation;
        public boolean isTargeted;
        @Nullable
        public String mappedEntityTypeId;
        public float worldTime;
        public float tickProgress;
    }
}
