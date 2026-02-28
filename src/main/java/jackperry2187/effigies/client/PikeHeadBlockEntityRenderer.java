package jackperry2187.effigies.client;

import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
*///?}

public class PikeHeadBlockEntityRenderer
    //? if fabric {
    implements BlockEntityRenderer<PikeHeadBlockEntity, PikeHeadBlockEntityRenderer.PikeHeadRenderState>
    //?} else {
    /*implements BlockEntityRenderer<PikeHeadBlockEntity, PikeHeadBlockEntityRenderer.PikeHeadRenderState>
    *///?}
{
    //? if fabric {
    private final LoadedEntityModels modelLoader;

    public PikeHeadBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.modelLoader = ctx.loadedEntityModels();
    }

    @Override
    public PikeHeadRenderState createRenderState() {
        return new PikeHeadRenderState();
    }

    @Override
    public void updateRenderState(PikeHeadBlockEntity entity, PikeHeadRenderState state, float tickProgress,
                                  Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderState.updateBlockEntityRenderState(entity, state, crumblingOverlay);
        state.storedState = entity.getStoredBlockState();
        state.rotation = entity.getRotation();
    }

    @Override
    public void render(PikeHeadRenderState state, MatrixStack matrices,
                       OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        if (state.storedState == null) return;

        float yaw = state.rotation * 22.5f;

        if (state.storedState.getBlock() instanceof AbstractSkullBlock skull) {
            SkullBlock.SkullType skullType = skull.getSkullType();
            SkullBlockEntityModel model = SkullBlockEntityRenderer.getModels(modelLoader, skullType);
            RenderLayer renderLayer = SkullBlockEntityRenderer.getCutoutRenderLayer(skullType, null);
            if (model != null && renderLayer != null) {
                SkullBlockEntityRenderer.render(null, yaw, 0f, matrices, queue,
                    state.lightmapCoordinates, model, renderLayer, 0, state.crumblingOverlay);
            }
        } else {
            var bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            matrices.push();
            matrices.translate(0.5, 0.0, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(-0.5, 0.0, -0.5);
            MinecraftClient.getInstance().getBlockRenderManager()
                .renderBlockAsEntity(state.storedState, matrices, bufferSource,
                    state.lightmapCoordinates, OverlayTexture.DEFAULT_UV);
            bufferSource.draw();
            matrices.pop();
        }
    }
    //?} else {
    /*private final EntityModelSet modelSet;

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
                SkullBlockRenderer.submitSkull(null, yaw, 0f, poseStack, submitNodeCollector,
                    state.lightCoords, model, renderType, 0, state.breakProgress);
            }
        } else {
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
        }
    }
    *///?}

    public static class PikeHeadRenderState extends BlockEntityRenderState {
        @Nullable
        //? if fabric {
        public BlockState storedState;
        //?} else {
        /*public BlockState storedState;
        *///?}
        public int rotation;
    }
}
