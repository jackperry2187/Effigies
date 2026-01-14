package jackperry2187.gentlereminders.handler.client;

import jackperry2187.gentlereminders.config.client.ConfigSettings;
import jackperry2187.gentlereminders.util.Message;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
//? if mcGte1216
import net.minecraft.client.gl.RenderPipelines;
//?}

//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
*///?}

//? if neoforge && mcGte1216 {
/*import net.minecraft.client.renderer.RenderPipelines;
*///?}

import java.util.List;

//? if fabric {
@Environment(value = EnvType.CLIENT)
public class GRToastHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static int showMessageDuration = 20 * 6;
    private static final float fadeOutDuration = 6.0F;
    private static boolean hasPlayedDismissSound = false;

    public static void renderCustomToast(DrawContext drawContext, Message currentMessage) {
        if (GRTickManager.totalTickCounter < GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration) {
            showCustomToast(drawContext, currentMessage, 1.0F);
            hasPlayedDismissSound = false;
        }

        if (GRTickManager.totalTickCounter >= GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration && !hasPlayedDismissSound) {
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_OUT, 1.0F, 1.0F));
            hasPlayedDismissSound = true;
        }

        if (GRTickManager.totalTickCounter >= GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration) {
            float fadeOutAmount = MathHelper.clamp((float) Math.abs(GRTickManager.totalTickCounter - GRTickManager.ticksWhenStartedShowing - showMessageDuration) / fadeOutDuration, 0.0F, 1.0F);
            showCustomToast(drawContext, currentMessage, fadeOutAmount * fadeOutAmount);
        }
    }

    public static void resetDismissSound() {
        hasPlayedDismissSound = false;
    }

    private static void showCustomToast(DrawContext drawContext, Message message, float fadeOutAmount) {
        int w = drawContext.getScaledWindowWidth();
        TextRenderer textRenderer = client.textRenderer;

        List<OrderedText> titleLines = textRenderer.wrapLines(message.Title, 200);
        List<OrderedText> descriptionLines = textRenderer.wrapLines(message.Description, 200);

        int textureW;
        if (titleLines.size() == 1 && descriptionLines.size() == 1 && textRenderer.getWidth(message.Title) <= 180 && textRenderer.getWidth(message.Description) <= 180) {
            textureW = Math.max(textRenderer.getWidth(message.Title), textRenderer.getWidth(message.Description)) + 20;
        } else {
            textureW = Math.max(200, titleLines.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
        }
        int textureWWithFadeOut = MathHelper.ceil(textureW * fadeOutAmount);
        int textureH = 20 + ((titleLines.size() - 1) * 12) + descriptionLines.size() * 12;

        int textStartX = w - textureWWithFadeOut - 5;

        if (ConfigSettings.getDisplayStyle().equals("custom")) {
            fillWithCustomTexture(drawContext, ConfigSettings.CUSTOM_BG_TEXTURE, ConfigSettings.CUSTOM_BORDER_TEXTURE, ConfigSettings.CUSTOM_INCLUDE_ICON, w - textureWWithFadeOut - 30, 0, textureW + 30, textureH);
        } else {
            //? if mcGte1216 {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ConfigSettings.toastTexture, w - textureWWithFadeOut - 30, 0, 0, 0, textureW + 30, textureH, textureW + 30, textureH);
            //?} else {
            /*drawContext.drawTexture(ConfigSettings.toastTexture, w - textureWWithFadeOut - 30, 0, 0, 0, textureW + 30, textureH, textureW + 30, textureH);
            *///?}
        }

        for (int t = 0; t < titleLines.size(); ++t) {
            drawContext.drawText(client.textRenderer, titleLines.get(t), textStartX, 7 + t * 12, -1, false);
        }

        for (int j = 0; j < descriptionLines.size(); ++j) {
            drawContext.drawText(client.textRenderer, descriptionLines.get(j), textStartX, 18 + (j * 12) + ((titleLines.size() - 1) * 12), -1, false);
        }
    }

    private static void fillWithCustomTexture(DrawContext drawContext, Identifier bgTexture, Identifier borderTexture, Boolean includeIcon, int x, int y, int w, int h) {
        int textureSize = 16;
        int fullTexturesX = w / textureSize;
        int fullTexturesY = h / textureSize;
        int remainingX = w % textureSize;
        int remainingY = h % textureSize;

        for (int i = 0; i < fullTexturesX; i++) {
            for (int j = 0; j < fullTexturesY; j++) {
                //? if mcGte1216 {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, bgTexture, x + i * textureSize, y + j * textureSize, 0, 0, textureSize, textureSize, textureSize, textureSize);
                //?} else {
                /*drawContext.drawTexture(bgTexture, x + i * textureSize, y + j * textureSize, 0, 0, textureSize, textureSize, textureSize, textureSize);
                *///?}
            }
        }

        if (remainingX > 0) {
            for (int j = 0; j < fullTexturesY; j++) {
                //? if mcGte1216 {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, bgTexture, x + fullTexturesX * textureSize, y + j * textureSize, 0, 0, remainingX, textureSize, textureSize, textureSize);
                //?} else {
                /*drawContext.drawTexture(bgTexture, x + fullTexturesX * textureSize, y + j * textureSize, 0, 0, remainingX, textureSize, textureSize, textureSize);
                *///?}
            }
        }

        if (remainingY > 0) {
            for (int i = 0; i < fullTexturesX; i++) {
                //? if mcGte1216 {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, bgTexture, x + i * textureSize, y + fullTexturesY * textureSize, 0, 0, textureSize, remainingY, textureSize, textureSize);
                //?} else {
                /*drawContext.drawTexture(bgTexture, x + i * textureSize, y + fullTexturesY * textureSize, 0, 0, textureSize, remainingY, textureSize, textureSize);
                *///?}
            }
        }

        if (remainingX > 0 && remainingY > 0) {
            //? if mcGte1216 {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, bgTexture, x + fullTexturesX * textureSize, y + fullTexturesY * textureSize, 0, 0, remainingX, remainingY, textureSize, textureSize);
            //?} else {
            /*drawContext.drawTexture(bgTexture, x + fullTexturesX * textureSize, y + fullTexturesY * textureSize, 0, 0, remainingX, remainingY, textureSize, textureSize);
            *///?}
        }

        //? if mcGte1216 {
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, borderTexture, x, y, 0, 0, w, 3, 16, 16);
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, borderTexture, x, y + h - 3, 0, 0, w, 3, 16, 16);
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, borderTexture, x, y, 0, 0, 3, h, 16, 16);
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, borderTexture, x + w - 3, y, 0, 0, 3, h, 16, 16);
        //?} else {
        /*drawContext.drawTexture(borderTexture, x, y, 0, 0, w, 3, 16, 16);
        drawContext.drawTexture(borderTexture, x, y + h - 3, 0, 0, w, 3, 16, 16);
        drawContext.drawTexture(borderTexture, x, y, 0, 0, 3, h, 16, 16);
        drawContext.drawTexture(borderTexture, x + w - 3, y, 0, 0, 3, h, 16, 16);
        *///?}

        if (includeIcon) {
            //? if mcGte1216 {
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.ofVanilla("textures/gui/sprites/world_list/warning_highlighted.png"), x + 8, y + 6, 7, 6, 6, 22, 32, 32);
            //?} else if mcGte121 {
            /*drawContext.drawTexture(Identifier.ofVanilla("textures/gui/sprites/world_list/warning_highlighted.png"), x + 8, y + 6, 7, 6, 6, 22, 32, 32);
            *///?}
        }
    }
}
//?}

//? if neoforge && !mcGte1216 {
/*@OnlyIn(Dist.CLIENT)
public class GRToastHandler {
    private static final Minecraft client = Minecraft.getInstance();
    public static int showMessageDuration = 20 * 6;
    private static final float fadeOutDuration = 6.0F;
    private static boolean hasPlayedDismissSound = false;

    // Sprite location for vanilla toast (used with blitSprite)
    private static final ResourceLocation TOAST_SYSTEM_SPRITE = ResourceLocation.withDefaultNamespace("toast/system");
    // Full texture paths for mod's custom textures (used with blit)
    private static final ResourceLocation TOAST_LIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath("gentlereminders", "custom_toasts/system_light.png");
    private static final ResourceLocation TOAST_DARK_TEXTURE = ResourceLocation.fromNamespaceAndPath("gentlereminders", "custom_toasts/system_dark.png");

    public static void renderCustomToast(GuiGraphics drawContext, Message currentMessage) {
        if (GRTickManager.totalTickCounter < GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration) {
            showCustomToast(drawContext, currentMessage, 1.0F);
            hasPlayedDismissSound = false;
        }

        if (GRTickManager.totalTickCounter >= GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration && !hasPlayedDismissSound) {
            client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_OUT, 1.0F, 1.0F));
            hasPlayedDismissSound = true;
        }

        if (GRTickManager.totalTickCounter >= GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration) {
            float fadeOutAmount = Mth.clamp((float) Math.abs(GRTickManager.totalTickCounter - GRTickManager.ticksWhenStartedShowing - showMessageDuration) / fadeOutDuration, 0.0F, 1.0F);
            showCustomToast(drawContext, currentMessage, fadeOutAmount * fadeOutAmount);
        }
    }

    public static void resetDismissSound() {
        hasPlayedDismissSound = false;
    }

    private static void showCustomToast(GuiGraphics drawContext, Message message, float fadeOutAmount) {
        int w = drawContext.guiWidth();
        Font font = client.font;

        List<FormattedCharSequence> titleLines = font.split(message.Title, 200);
        List<FormattedCharSequence> descriptionLines = font.split(message.Description, 200);

        int textureW;
        if (titleLines.size() == 1 && descriptionLines.size() == 1 && font.width(message.Title) <= 180 && font.width(message.Description) <= 180) {
            textureW = Math.max(font.width(message.Title), font.width(message.Description)) + 20;
        } else {
            textureW = Math.max(200, titleLines.stream().mapToInt(font::width).max().orElse(200));
        }
        int textureWWithFadeOut = Mth.ceil(textureW * fadeOutAmount);
        int textureH = 20 + ((titleLines.size() - 1) * 12) + descriptionLines.size() * 12;

        int textStartX = w - textureWWithFadeOut - 5;
        int toastX = w - textureWWithFadeOut - 30;
        int toastW = textureW + 30;

        String style = ConfigSettings.getDisplayStyle();
        switch (style) {
            case "custom":
                fillWithCustomTexture(drawContext, ConfigSettings.CUSTOM_BG_TEXTURE, ConfigSettings.CUSTOM_BORDER_TEXTURE, ConfigSettings.CUSTOM_INCLUDE_ICON, toastX, 0, toastW, textureH);
                break;
            case "light":
                // Light style: use blit with full texture path (mod's custom texture)
                drawContext.blit(TOAST_LIGHT_TEXTURE, toastX, 0, 0, 0, toastW, textureH, toastW, textureH);
                break;
            case "dark":
                // Dark style: use blit with full texture path (mod's custom texture)
                drawContext.blit(TOAST_DARK_TEXTURE, toastX, 0, 0, 0, toastW, textureH, toastW, textureH);
                break;
            default: // "default"
                // Default style: use vanilla toast sprite rendering (blitSprite)
                drawContext.blitSprite(TOAST_SYSTEM_SPRITE, toastX, 0, toastW, textureH);
                break;
        }

        for (int t = 0; t < titleLines.size(); ++t) {
            drawContext.drawString(client.font, titleLines.get(t), textStartX, 7 + t * 12, -1, false);
        }

        for (int j = 0; j < descriptionLines.size(); ++j) {
            drawContext.drawString(client.font, descriptionLines.get(j), textStartX, 18 + (j * 12) + ((titleLines.size() - 1) * 12), -1, false);
        }
    }

    private static void fillWithCustomTexture(GuiGraphics drawContext, ResourceLocation bgTexture, ResourceLocation borderTexture, Boolean includeIcon, int x, int y, int w, int h) {
        int textureSize = 16;
        int fullTexturesX = w / textureSize;
        int fullTexturesY = h / textureSize;
        int remainingX = w % textureSize;
        int remainingY = h % textureSize;

        for (int i = 0; i < fullTexturesX; i++) {
            for (int j = 0; j < fullTexturesY; j++) {
                drawContext.blit(bgTexture, x + i * textureSize, y + j * textureSize, 0, 0, textureSize, textureSize, textureSize, textureSize);
            }
        }

        if (remainingX > 0) {
            for (int j = 0; j < fullTexturesY; j++) {
                drawContext.blit(bgTexture, x + fullTexturesX * textureSize, y + j * textureSize, 0, 0, remainingX, textureSize, textureSize, textureSize);
            }
        }

        if (remainingY > 0) {
            for (int i = 0; i < fullTexturesX; i++) {
                drawContext.blit(bgTexture, x + i * textureSize, y + fullTexturesY * textureSize, 0, 0, textureSize, remainingY, textureSize, textureSize);
            }
        }

        if (remainingX > 0 && remainingY > 0) {
            drawContext.blit(bgTexture, x + fullTexturesX * textureSize, y + fullTexturesY * textureSize, 0, 0, remainingX, remainingY, textureSize, textureSize);
        }

        drawContext.blit(borderTexture, x, y, 0, 0, w, 3, 16, 16);
        drawContext.blit(borderTexture, x, y + h - 3, 0, 0, w, 3, 16, 16);
        drawContext.blit(borderTexture, x, y, 0, 0, 3, h, 16, 16);
        drawContext.blit(borderTexture, x + w - 3, y, 0, 0, 3, h, 16, 16);

        if (includeIcon) {
            drawContext.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/world_list/warning_highlighted.png"), x + 8, y + 6, 7, 6, 6, 22, 32, 32);
        }
    }
}
*///?}

//? if neoforge && mcGte1216 {
/*@OnlyIn(Dist.CLIENT)
public class GRToastHandler {
    private static final Minecraft client = Minecraft.getInstance();
    public static int showMessageDuration = 20 * 6;
    private static final float fadeOutDuration = 6.0F;
    private static boolean hasPlayedDismissSound = false;

    // Sprite location for vanilla toast (used with blitSprite)
    private static final ResourceLocation TOAST_SYSTEM_SPRITE = ResourceLocation.withDefaultNamespace("toast/system");
    // Full texture paths for mod's custom textures (used with blit)
    private static final ResourceLocation TOAST_LIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath("gentlereminders", "custom_toasts/system_light.png");
    private static final ResourceLocation TOAST_DARK_TEXTURE = ResourceLocation.fromNamespaceAndPath("gentlereminders", "custom_toasts/system_dark.png");

    public static void renderCustomToast(GuiGraphics drawContext, Message currentMessage) {
        if (GRTickManager.totalTickCounter < GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration) {
            showCustomToast(drawContext, currentMessage, 1.0F);
            hasPlayedDismissSound = false;
        }

        if (GRTickManager.totalTickCounter >= GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration && !hasPlayedDismissSound) {
            client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_OUT, 1.0F, 1.0F));
            hasPlayedDismissSound = true;
        }

        if (GRTickManager.totalTickCounter >= GRTickManager.ticksWhenStartedShowing + showMessageDuration - fadeOutDuration) {
            float fadeOutAmount = Mth.clamp((float) Math.abs(GRTickManager.totalTickCounter - GRTickManager.ticksWhenStartedShowing - showMessageDuration) / fadeOutDuration, 0.0F, 1.0F);
            showCustomToast(drawContext, currentMessage, fadeOutAmount * fadeOutAmount);
        }
    }

    public static void resetDismissSound() {
        hasPlayedDismissSound = false;
    }

    private static void showCustomToast(GuiGraphics drawContext, Message message, float fadeOutAmount) {
        int w = drawContext.guiWidth();
        Font font = client.font;

        List<FormattedCharSequence> titleLines = font.split(message.Title, 200);
        List<FormattedCharSequence> descriptionLines = font.split(message.Description, 200);

        int textureW;
        if (titleLines.size() == 1 && descriptionLines.size() == 1 && font.width(message.Title) <= 180 && font.width(message.Description) <= 180) {
            textureW = Math.max(font.width(message.Title), font.width(message.Description)) + 20;
        } else {
            textureW = Math.max(200, titleLines.stream().mapToInt(font::width).max().orElse(200));
        }
        int textureWWithFadeOut = Mth.ceil(textureW * fadeOutAmount);
        int textureH = 20 + ((titleLines.size() - 1) * 12) + descriptionLines.size() * 12;

        int textStartX = w - textureWWithFadeOut - 5;
        int toastX = w - textureWWithFadeOut - 30;
        int toastW = textureW + 30;

        String style = ConfigSettings.getDisplayStyle();
        switch (style) {
            case "custom":
                fillWithCustomTexture(drawContext, ConfigSettings.CUSTOM_BG_TEXTURE, ConfigSettings.CUSTOM_BORDER_TEXTURE, ConfigSettings.CUSTOM_INCLUDE_ICON, toastX, 0, toastW, textureH);
                break;
            case "light":
                // Light style: use blit with full texture path (mod's custom texture)
                drawContext.blit(RenderPipelines.GUI_TEXTURED, TOAST_LIGHT_TEXTURE, toastX, 0, 0, 0, toastW, textureH, toastW, textureH);
                break;
            case "dark":
                // Dark style: use blit with full texture path (mod's custom texture)
                drawContext.blit(RenderPipelines.GUI_TEXTURED, TOAST_DARK_TEXTURE, toastX, 0, 0, 0, toastW, textureH, toastW, textureH);
                break;
            default: // "default"
                // Default style: use vanilla toast sprite rendering (blitSprite)
                drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, TOAST_SYSTEM_SPRITE, toastX, 0, toastW, textureH);
                break;
        }

        for (int t = 0; t < titleLines.size(); ++t) {
            drawContext.drawString(client.font, titleLines.get(t), textStartX, 7 + t * 12, -1, false);
        }

        for (int j = 0; j < descriptionLines.size(); ++j) {
            drawContext.drawString(client.font, descriptionLines.get(j), textStartX, 18 + (j * 12) + ((titleLines.size() - 1) * 12), -1, false);
        }
    }

    private static void fillWithCustomTexture(GuiGraphics drawContext, ResourceLocation bgTexture, ResourceLocation borderTexture, Boolean includeIcon, int x, int y, int w, int h) {
        int textureSize = 16;
        int fullTexturesX = w / textureSize;
        int fullTexturesY = h / textureSize;
        int remainingX = w % textureSize;
        int remainingY = h % textureSize;

        for (int i = 0; i < fullTexturesX; i++) {
            for (int j = 0; j < fullTexturesY; j++) {
                drawContext.blit(RenderPipelines.GUI_TEXTURED, bgTexture, x + i * textureSize, y + j * textureSize, 0, 0, textureSize, textureSize, textureSize, textureSize);
            }
        }

        if (remainingX > 0) {
            for (int j = 0; j < fullTexturesY; j++) {
                drawContext.blit(RenderPipelines.GUI_TEXTURED, bgTexture, x + fullTexturesX * textureSize, y + j * textureSize, 0, 0, remainingX, textureSize, textureSize, textureSize);
            }
        }

        if (remainingY > 0) {
            for (int i = 0; i < fullTexturesX; i++) {
                drawContext.blit(RenderPipelines.GUI_TEXTURED, bgTexture, x + i * textureSize, y + fullTexturesY * textureSize, 0, 0, textureSize, remainingY, textureSize, textureSize);
            }
        }

        if (remainingX > 0 && remainingY > 0) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, bgTexture, x + fullTexturesX * textureSize, y + fullTexturesY * textureSize, 0, 0, remainingX, remainingY, textureSize, textureSize);
        }

        drawContext.blit(RenderPipelines.GUI_TEXTURED, borderTexture, x, y, 0, 0, w, 3, 16, 16);
        drawContext.blit(RenderPipelines.GUI_TEXTURED, borderTexture, x, y + h - 3, 0, 0, w, 3, 16, 16);
        drawContext.blit(RenderPipelines.GUI_TEXTURED, borderTexture, x, y, 0, 0, 3, h, 16, 16);
        drawContext.blit(RenderPipelines.GUI_TEXTURED, borderTexture, x + w - 3, y, 0, 0, 3, h, 16, 16);

        if (includeIcon) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, ResourceLocation.withDefaultNamespace("textures/gui/sprites/world_list/warning_highlighted.png"), x + 8, y + 6, 7, 6, 6, 22, 32, 32);
        }
    }
}
*///?}
