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
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
*///?}

import java.util.List;

//? if fabric {
@Environment(value = EnvType.CLIENT)
public class GRHUDHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static boolean isShowingCustomMessage = false;
    private static Message currentMessage = null;
    private static final boolean isDebug = false;

    public static void HUDHandler(DrawContext drawContext) {
        if (isShowingCustomMessage) {
            GRToastHandler.renderCustomToast(drawContext, currentMessage);
        }

        if (isDebug && currentMessage != null) {
            drawDebug(drawContext, currentMessage);
        }
    }

    private static void addDefaultToast(Message message) {
        client.getToastManager().add(SystemToast.create(
                client,
                SystemToast.Type.PERIODIC_NOTIFICATION,
                message.Title,
                message.Description
        ));
    }

    private static void addChatMessage(Message message) {
        Text combinedText = ScreenTexts.joinLines(message.Title, message.Description);
        client.player.sendMessage(combinedText, false);
    }

    public static void startShowingMessage() {
        switch (ConfigSettings.getDisplayStyle()) {
            case "default":
                currentMessage = currentMessage != null ? currentMessage : GRMessageHandler.getRandomMessage();
                addDefaultToast(currentMessage);
                break;
            case "light":
            case "dark":
            case "custom":
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_IN, 1.0F, 1.0F));
                currentMessage = currentMessage != null ? currentMessage : GRMessageHandler.getRandomMessage();
                isShowingCustomMessage = true;
                break;
            case "chat":
                //? if mcGte121 {
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_DECORATED_POT_INSERT, 1.0F, 1.0F));
                //?} else {
                /*client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_COPPER_STEP, 1.0F, 1.0F));
                *///?}
                currentMessage = currentMessage != null ? currentMessage : GRMessageHandler.getRandomMessage();
                addChatMessage(currentMessage);
                break;
            default:
                break;
        }
    }

    public static void stopShowingMessage() {
        isShowingCustomMessage = false;
        currentMessage = null;
        GRToastHandler.resetDismissSound();
    }

    private static void drawDebug(DrawContext drawContext, Message message) {
        int w = drawContext.getScaledWindowWidth();
        int h = drawContext.getScaledWindowHeight();

        TextRenderer textRenderer = client.textRenderer;
        List<OrderedText> descriptionLines = textRenderer.wrapLines(message.Description, 200);
        int textureW = Math.max(200, descriptionLines.stream().mapToInt(textRenderer::getWidth).max().orElse(200));

        drawContext.drawHorizontalLine(0, w, h / 2, Colors.RED);
        drawContext.drawVerticalLine(w / 2, 0, h, Colors.RED);

        //? if mcGte121 {
        drawContext.drawVerticalLine(w - 160, 0, h, Colors.GREEN);
        drawContext.drawHorizontalLine(0, w, 7, Colors.RED);
        drawContext.drawHorizontalLine(0, w, 20, Colors.BLUE);
        drawContext.drawVerticalLine(w - textureW - 30, 0, h, Colors.BLUE);
        drawContext.drawVerticalLine(w - textureW - 18, 0, h, Colors.YELLOW);
        //?} else {
        /*drawContext.drawVerticalLine(w - 160, 0, h, Colors.BLACK);
        drawContext.drawHorizontalLine(0, w, 7, Colors.RED);
        drawContext.drawHorizontalLine(0, w, 20, Colors.GRAY);
        drawContext.drawVerticalLine(w - textureW - 30, 0, h, Colors.BLACK);
        drawContext.drawVerticalLine(w - textureW - 18, 0, h, Colors.GRAY);
        *///?}
        drawContext.drawVerticalLine(w - textureW, 0, h, Colors.WHITE);
    }
}
//?} else {
/*@OnlyIn(Dist.CLIENT)
public class GRHUDHandler {
    private static final Minecraft client = Minecraft.getInstance();

    private static boolean isShowingCustomMessage = false;
    private static Message currentMessage = null;
    private static final boolean isDebug = false;

    public static void HUDHandler(GuiGraphics drawContext) {
        if (isShowingCustomMessage) {
            GRToastHandler.renderCustomToast(drawContext, currentMessage);
        }

        if (isDebug && currentMessage != null) {
            drawDebug(drawContext, currentMessage);
        }
    }

    private static void addDefaultToast(Message message) {
        // NeoForge: Use custom toast instead of system toast due to API differences between versions
        GRToastHandler.showMessageDuration = 20 * 6;
        isShowingCustomMessage = true;
    }

    private static void addChatMessage(Message message) {
        Component combinedText = CommonComponents.joinForNarration(message.Title, message.Description);
        if (client.player != null) {
            client.player.displayClientMessage(combinedText, false);
        }
    }

    public static void startShowingMessage() {
        switch (ConfigSettings.getDisplayStyle()) {
            case "default":
                currentMessage = currentMessage != null ? currentMessage : GRMessageHandler.getRandomMessage();
                addDefaultToast(currentMessage);
                break;
            case "light":
            case "dark":
            case "custom":
                client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_IN, 1.0F, 1.0F));
                currentMessage = currentMessage != null ? currentMessage : GRMessageHandler.getRandomMessage();
                isShowingCustomMessage = true;
                break;
            case "chat":
                client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.DECORATED_POT_INSERT, 1.0F, 1.0F));
                currentMessage = currentMessage != null ? currentMessage : GRMessageHandler.getRandomMessage();
                addChatMessage(currentMessage);
                break;
            default:
                break;
        }
    }

    public static void stopShowingMessage() {
        isShowingCustomMessage = false;
        currentMessage = null;
        GRToastHandler.resetDismissSound();
    }

    private static void drawDebug(GuiGraphics drawContext, Message message) {
        int w = drawContext.guiWidth();
        int h = drawContext.guiHeight();

        Font font = client.font;
        List<FormattedCharSequence> descriptionLines = font.split(message.Description, 200);
        int textureW = Math.max(200, descriptionLines.stream().mapToInt(font::width).max().orElse(200));

        drawContext.hLine(0, w, h / 2, 0xFFFF0000);
        drawContext.vLine(w / 2, 0, h, 0xFFFF0000);

        drawContext.vLine(w - 160, 0, h, 0xFF00FF00);
        drawContext.hLine(0, w, 7, 0xFFFF0000);
        drawContext.hLine(0, w, 20, 0xFF0000FF);
        drawContext.vLine(w - textureW - 30, 0, h, 0xFF0000FF);
        drawContext.vLine(w - textureW - 18, 0, h, 0xFFFFFF00);
        drawContext.vLine(w - textureW, 0, h, 0xFFFFFFFF);
    }
}
*///?}
