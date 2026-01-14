//? if fabric {
package jackperry2187.gentlereminders.commands.client;

import com.mojang.brigadier.context.CommandContext;
import jackperry2187.gentlereminders.config.client.ConfigSettings;
import jackperry2187.gentlereminders.handler.client.GRTickManager;
import jackperry2187.gentlereminders.util.Message;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

import static jackperry2187.gentlereminders.config.client.EditConfigSettings.*;

@Environment(value = EnvType.CLIENT)
public class CommandInit {
    public static int help(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        source.sendFeedback(Text.literal("Thank you for downloading Gentle Reminders!").formatted(Formatting.AQUA));
        source.sendFeedback(Text.literal("Use /gentlereminders get|set|add|remove|enable|disable for commands.").formatted(Formatting.AQUA));
        return 1;
    }

    public static int getTimeUntilNextMessage(CommandContext<FabricClientCommandSource> context) {
        int remainingTime = (ConfigSettings.ticksBetweenMessages - (GRTickManager.totalTickCounter % ConfigSettings.ticksBetweenMessages));
        double remainingTimeInMinutes = (double) remainingTime / 20 / 60;
        int remainingMinutes = (int) remainingTimeInMinutes;
        int remainingSeconds = (int) ((remainingTimeInMinutes - remainingMinutes) * 60);
        context.getSource().sendFeedback(Text.literal("Remaining: " + (remainingMinutes > 0 ? remainingMinutes + "m " : "") + remainingSeconds + "s").formatted(Formatting.GOLD));
        return 1;
    }

    public static int getConfigPath(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Config: " + ConfigSettings.configPath).formatted(Formatting.GOLD));
        return 1;
    }

    public static int getConfigVersion(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Config version: " + ConfigSettings.configVersion).formatted(Formatting.GOLD));
        return 1;
    }

    public static int getDisplayStyle(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Display style: " + ConfigSettings.getDisplayStyle()).formatted(Formatting.GOLD));
        return 1;
    }

    public static int getTicksBetweenMessages(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Ticks: " + ConfigSettings.ticksBetweenMessages).formatted(Formatting.GOLD));
        return 1;
    }

    public static int getMessages(CommandContext<FabricClientCommandSource> context) {
        Integer pageNumber = context.getArgument("pageNumber", Integer.class);
        List<Message> messages = ConfigSettings.messages;
        int totalPages = (int) Math.ceil((double) messages.size() / 5);
        int startIndex = (pageNumber - 1) * 5;
        int endIndex = Math.min(pageNumber * 5, messages.size());
        for (int i = startIndex; i < endIndex; i++) {
            Message message = messages.get(i);
            context.getSource().sendFeedback(Text.literal("ID " + message.ID + ": ").formatted(Formatting.GOLD).append(message.Title));
        }
        context.getSource().sendFeedback(Text.literal("Page " + pageNumber + "/" + totalPages).formatted(Formatting.AQUA));
        return 1;
    }

    public static int setDisplayStyle(CommandContext<FabricClientCommandSource> context) {
        String style = context.getArgument("style", String.class);
        if (style.equals("custom")) {
            context.getSource().sendFeedback(Text.literal("Use /gentlereminders set CustomDisplayStyle instead!").formatted(Formatting.RED));
            return 1;
        }
        ConfigSettings.setDisplayStyle(style);
        setFileValue("displayStyle", "\"" + style + "\"");
        context.getSource().sendFeedback(Text.literal("Display style set to " + style).formatted(Formatting.GOLD));
        return 1;
    }

    public static int setCustomDisplayStyle(CommandContext<FabricClientCommandSource> context) {
        String bgTexture = context.getArgument("bgTexture", String.class);
        String borderTexture = context.getArgument("borderTexture", String.class);
        Boolean includeIcon = context.getArgument("includeIcon", Boolean.class);
        //? if mcGte121 {
        Identifier bgId = Identifier.ofVanilla("textures/block/" + bgTexture + ".png");
        Identifier borderId = Identifier.ofVanilla("textures/block/" + borderTexture + ".png");
        //?} else {
        /*Identifier bgId = new Identifier("minecraft", "textures/block/" + bgTexture + ".png");
        Identifier borderId = new Identifier("minecraft", "textures/block/" + borderTexture + ".png");
        *///?}
        ConfigSettings.setDisplayStyle("custom");
        ConfigSettings.setCustomBackgroundTexture(bgId);
        ConfigSettings.setCustomBorderTexture(borderId);
        ConfigSettings.setCustomIncludeIcon(includeIcon);
        setFileValue("displayStyle", "\"custom\"");
        setFileValue("customBGTexture", "\"minecraft:" + bgTexture + "\"");
        setFileValue("customBorderTexture", "\"minecraft:" + borderTexture + "\"");
        setFileValue("customIncludeIcon", "\"" + includeIcon + "\"");
        context.getSource().sendFeedback(Text.literal("Custom display style set with bg=" + bgTexture + ", border=" + borderTexture + ", icon=" + includeIcon).formatted(Formatting.GOLD));
        return 1;
    }

    public static int setTicksBetweenMessages(CommandContext<FabricClientCommandSource> context) {
        Integer ticks = context.getArgument("ticks", Integer.class);
        ConfigSettings.ticksBetweenMessages = ticks;
        setFileValue("timeBetweenMessages", Math.max(ticks / 20 / 60, 1) + "");
        context.getSource().sendFeedback(Text.literal("Ticks set to " + ticks).formatted(Formatting.GOLD));
        return 1;
    }

    public static int setMessage(CommandContext<FabricClientCommandSource> context) {
        Integer id = context.getArgument("id", Integer.class);
        String title = context.getArgument("title", String.class);
        String message = context.getArgument("message", String.class);
        boolean enabled = context.getArgument("enabled", Boolean.class);
        Formatting titleColor = context.getArgument("titleColor", Formatting.class);
        Formatting messageColor = context.getArgument("messageColor", Formatting.class);
        Text formattedTitle = Text.literal(title).formatted(titleColor);
        Text formattedMessage = Text.literal(message).formatted(messageColor);

        for (Message m : ConfigSettings.messages) {
            if (m.ID == id) {
                m.Title = formattedTitle;
                m.Description = formattedMessage;
                m.Enabled = enabled;
                setFileMessage(id, new Message(id, formattedTitle, formattedMessage, enabled));
                context.getSource().sendFeedback(Text.literal("Message " + id + " updated").formatted(Formatting.GOLD));
                return 1;
            }
        }
        context.getSource().sendFeedback(Text.literal("Message not found").formatted(Formatting.RED));
        return 1;
    }

    public static int addMessage(CommandContext<FabricClientCommandSource> context) {
        int id = ConfigSettings.highestMessageID + 1;
        String title = context.getArgument("title", String.class);
        String message = context.getArgument("message", String.class);
        boolean enabled = context.getArgument("enabled", Boolean.class);
        Formatting titleColor = context.getArgument("titleColor", Formatting.class);
        Formatting messageColor = context.getArgument("messageColor", Formatting.class);
        Text formattedTitle = Text.literal(title).formatted(titleColor);
        Text formattedMessage = Text.literal(message).formatted(messageColor);
        ConfigSettings.messages.add(new Message(id, formattedTitle, formattedMessage, enabled));
        ConfigSettings.highestMessageID = id;
        addFileMessage(new Message(id, formattedTitle, formattedMessage, enabled));
        context.getSource().sendFeedback(Text.literal("Message " + id + " added").formatted(Formatting.GOLD));
        return 1;
    }

    public static int removeMessage(CommandContext<FabricClientCommandSource> context) {
        Integer id = context.getArgument("id", Integer.class);
        for (Message message : ConfigSettings.messages) {
            if (message.ID == id) {
                ConfigSettings.messages.remove(message);
                removeFileLine("{id", id + "");
                context.getSource().sendFeedback(Text.literal("Message " + id + " removed").formatted(Formatting.GOLD));
                return 1;
            }
        }
        context.getSource().sendFeedback(Text.literal("Message not found").formatted(Formatting.RED));
        return 1;
    }

    public static int enableMod(CommandContext<FabricClientCommandSource> context) {
        ConfigSettings.enabled = true;
        setFileValue("enabled", "true");
        context.getSource().sendFeedback(Text.literal("Mod enabled").formatted(Formatting.GREEN));
        return 1;
    }

    public static int enableMessage(CommandContext<FabricClientCommandSource> context) {
        Integer id = context.getArgument("id", Integer.class);
        for (Message m : ConfigSettings.messages) {
            if (m.ID == id) {
                m.Enabled = true;
                setMessageValue(m.ID, "enabled", "true");
                context.getSource().sendFeedback(Text.literal("Message " + id + " enabled").formatted(Formatting.GREEN));
                return 1;
            }
        }
        context.getSource().sendFeedback(Text.literal("Message not found").formatted(Formatting.RED));
        return 1;
    }

    public static int disableMod(CommandContext<FabricClientCommandSource> context) {
        ConfigSettings.enabled = false;
        setFileValue("enabled", "false");
        context.getSource().sendFeedback(Text.literal("Mod disabled").formatted(Formatting.RED));
        return 1;
    }

    public static int disableMessage(CommandContext<FabricClientCommandSource> context) {
        Integer id = context.getArgument("id", Integer.class);
        for (Message m : ConfigSettings.messages) {
            if (m.ID == id) {
                m.Enabled = false;
                setMessageValue(m.ID, "enabled", "false");
                context.getSource().sendFeedback(Text.literal("Message " + id + " disabled").formatted(Formatting.RED));
                return 1;
            }
        }
        context.getSource().sendFeedback(Text.literal("Message not found").formatted(Formatting.RED));
        return 1;
    }
}
//?} else {
/*package jackperry2187.gentlereminders.commands.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import jackperry2187.gentlereminders.config.client.ConfigSettings;
import jackperry2187.gentlereminders.handler.client.GRTickManager;
import jackperry2187.gentlereminders.util.Message;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static jackperry2187.gentlereminders.config.client.EditConfigSettings.*;

@OnlyIn(Dist.CLIENT)
public class CommandInit {
    private static final List<String> VALID_COLORS = Arrays.stream(ChatFormatting.values())
            .filter(ChatFormatting::isColor)
            .map(cf -> cf.getName().toUpperCase())
            .collect(Collectors.toList());

    public static int help(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Thank you for downloading Gentle Reminders!").withStyle(ChatFormatting.AQUA), false);
        context.getSource().sendSuccess(() -> Component.literal("Use /gentlereminders get|set|add|remove|enable|disable for commands.").withStyle(ChatFormatting.AQUA), false);
        context.getSource().sendSuccess(() -> Component.literal("Valid colors: " + String.join(", ", VALID_COLORS)).withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    public static int getTimeUntilNextMessage(CommandContext<CommandSourceStack> context) {
        int remainingTime = (ConfigSettings.ticksBetweenMessages - (GRTickManager.totalTickCounter % ConfigSettings.ticksBetweenMessages));
        double remainingTimeInMinutes = (double) remainingTime / 20 / 60;
        int remainingMinutes = (int) remainingTimeInMinutes;
        int remainingSeconds = (int) ((remainingTimeInMinutes - remainingMinutes) * 60);
        context.getSource().sendSuccess(() -> Component.literal("Remaining: " + (remainingMinutes > 0 ? remainingMinutes + "m " : "") + remainingSeconds + "s").withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int getConfigPath(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Config: " + ConfigSettings.configPath).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int getConfigVersion(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Config version: " + ConfigSettings.configVersion).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int getDisplayStyle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Display style: " + ConfigSettings.getDisplayStyle()).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int getTicksBetweenMessages(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Ticks: " + ConfigSettings.ticksBetweenMessages).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int getMessages(CommandContext<CommandSourceStack> context) {
        Integer pageNumber = IntegerArgumentType.getInteger(context, "pageNumber");
        List<Message> messages = ConfigSettings.messages;
        int totalPages = (int) Math.ceil((double) messages.size() / 5);
        int startIndex = (pageNumber - 1) * 5;
        int endIndex = Math.min(pageNumber * 5, messages.size());
        for (int i = startIndex; i < endIndex; i++) {
            Message message = messages.get(i);
            context.getSource().sendSuccess(() -> Component.literal("ID " + message.ID + ": ").withStyle(ChatFormatting.GOLD).append(message.Title), false);
        }
        context.getSource().sendSuccess(() -> Component.literal("Page " + pageNumber + "/" + totalPages).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    public static int setDisplayStyle(CommandContext<CommandSourceStack> context) {
        String style = StringArgumentType.getString(context, "style");
        if (style.equals("custom")) {
            context.getSource().sendSuccess(() -> Component.literal("Use /gentlereminders set CustomDisplayStyle <bgTexture> <borderTexture> <includeIcon> instead!").withStyle(ChatFormatting.RED), false);
            return 1;
        }
        if (!style.equals("default") && !style.equals("light") && !style.equals("dark") && !style.equals("chat")) {
            context.getSource().sendSuccess(() -> Component.literal("Invalid style! Use: default, light, dark, chat, or custom").withStyle(ChatFormatting.RED), false);
            return 1;
        }
        ConfigSettings.setDisplayStyle(style);
        setFileValue("displayStyle", "\"" + style + "\"");
        context.getSource().sendSuccess(() -> Component.literal("Display style set to " + style).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int setCustomDisplayStyle(CommandContext<CommandSourceStack> context) {
        String bgTexture = StringArgumentType.getString(context, "bgTexture");
        String borderTexture = StringArgumentType.getString(context, "borderTexture");
        Boolean includeIcon = BoolArgumentType.getBool(context, "includeIcon");

        // Parse textures - just use the block name directly (autocomplete provides just the name)
        ResourceLocation bgId = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/" + bgTexture + ".png");
        ResourceLocation borderId = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/" + borderTexture + ".png");

        ConfigSettings.setDisplayStyle("custom");
        ConfigSettings.setCustomBackgroundTexture(bgId);
        ConfigSettings.setCustomBorderTexture(borderId);
        ConfigSettings.setCustomIncludeIcon(includeIcon);
        setFileValue("displayStyle", "\"custom\"");
        setFileValue("customBGTexture", "\"minecraft:" + bgTexture + "\"");
        setFileValue("customBorderTexture", "\"minecraft:" + borderTexture + "\"");
        setFileValue("customIncludeIcon", "\"" + includeIcon + "\"");
        context.getSource().sendSuccess(() -> Component.literal("Custom display style set with bg=" + bgTexture + ", border=" + borderTexture + ", icon=" + includeIcon).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int setTicksBetweenMessages(CommandContext<CommandSourceStack> context) {
        Integer ticks = IntegerArgumentType.getInteger(context, "ticks");
        ConfigSettings.ticksBetweenMessages = ticks;
        setFileValue("timeBetweenMessages", Math.max(ticks / 20 / 60, 1) + "");
        context.getSource().sendSuccess(() -> Component.literal("Ticks set to " + ticks).withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int setMessage(CommandContext<CommandSourceStack> context) {
        Integer id = IntegerArgumentType.getInteger(context, "id");
        String title = StringArgumentType.getString(context, "title");
        String message = StringArgumentType.getString(context, "message");
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        String titleColorStr = StringArgumentType.getString(context, "titleColor");
        String messageColorStr = StringArgumentType.getString(context, "messageColor");

        ChatFormatting titleColor = ChatFormatting.getByName(titleColorStr);
        ChatFormatting messageColor = ChatFormatting.getByName(messageColorStr);

        if (titleColor == null || !titleColor.isColor()) {
            context.getSource().sendSuccess(() -> Component.literal("Invalid title color! Valid colors: " + String.join(", ", VALID_COLORS)).withStyle(ChatFormatting.RED), false);
            return 1;
        }
        if (messageColor == null || !messageColor.isColor()) {
            context.getSource().sendSuccess(() -> Component.literal("Invalid message color! Valid colors: " + String.join(", ", VALID_COLORS)).withStyle(ChatFormatting.RED), false);
            return 1;
        }

        MutableComponent formattedTitle = Component.literal(title).withStyle(titleColor);
        MutableComponent formattedMessage = Component.literal(message).withStyle(messageColor);

        for (Message m : ConfigSettings.messages) {
            if (m.ID == id) {
                m.Title = formattedTitle;
                m.Description = formattedMessage;
                m.Enabled = enabled;
                setFileMessage(id, new Message(id, formattedTitle, formattedMessage, enabled));
                context.getSource().sendSuccess(() -> Component.literal("Message " + id + " updated").withStyle(ChatFormatting.GOLD), false);
                return 1;
            }
        }
        context.getSource().sendSuccess(() -> Component.literal("Message not found").withStyle(ChatFormatting.RED), false);
        return 1;
    }

    public static int addMessage(CommandContext<CommandSourceStack> context) {
        int id = ConfigSettings.highestMessageID + 1;
        String title = StringArgumentType.getString(context, "title");
        String message = StringArgumentType.getString(context, "message");
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        String titleColorStr = StringArgumentType.getString(context, "titleColor");
        String messageColorStr = StringArgumentType.getString(context, "messageColor");

        ChatFormatting titleColor = ChatFormatting.getByName(titleColorStr);
        ChatFormatting messageColor = ChatFormatting.getByName(messageColorStr);

        if (titleColor == null || !titleColor.isColor()) {
            context.getSource().sendSuccess(() -> Component.literal("Invalid title color! Valid colors: " + String.join(", ", VALID_COLORS)).withStyle(ChatFormatting.RED), false);
            return 1;
        }
        if (messageColor == null || !messageColor.isColor()) {
            context.getSource().sendSuccess(() -> Component.literal("Invalid message color! Valid colors: " + String.join(", ", VALID_COLORS)).withStyle(ChatFormatting.RED), false);
            return 1;
        }

        MutableComponent formattedTitle = Component.literal(title).withStyle(titleColor);
        MutableComponent formattedMessage = Component.literal(message).withStyle(messageColor);

        Message newMessage = new Message(id, formattedTitle, formattedMessage, enabled);
        ConfigSettings.messages.add(newMessage);
        ConfigSettings.highestMessageID = id;
        addFileMessage(newMessage);
        context.getSource().sendSuccess(() -> Component.literal("Message " + id + " added").withStyle(ChatFormatting.GOLD), false);
        return 1;
    }

    public static int removeMessage(CommandContext<CommandSourceStack> context) {
        Integer id = IntegerArgumentType.getInteger(context, "id");
        for (Message message : ConfigSettings.messages) {
            if (message.ID == id) {
                ConfigSettings.messages.remove(message);
                removeFileLine("{id", id + "");
                context.getSource().sendSuccess(() -> Component.literal("Message " + id + " removed").withStyle(ChatFormatting.GOLD), false);
                return 1;
            }
        }
        context.getSource().sendSuccess(() -> Component.literal("Message not found").withStyle(ChatFormatting.RED), false);
        return 1;
    }

    public static int enableMod(CommandContext<CommandSourceStack> context) {
        ConfigSettings.enabled = true;
        setFileValue("enabled", "true");
        context.getSource().sendSuccess(() -> Component.literal("Mod enabled").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    public static int enableMessage(CommandContext<CommandSourceStack> context) {
        Integer id = IntegerArgumentType.getInteger(context, "id");
        for (Message m : ConfigSettings.messages) {
            if (m.ID == id) {
                m.Enabled = true;
                setMessageValue(m.ID, "enabled", "true");
                context.getSource().sendSuccess(() -> Component.literal("Message " + id + " enabled").withStyle(ChatFormatting.GREEN), false);
                return 1;
            }
        }
        context.getSource().sendSuccess(() -> Component.literal("Message not found").withStyle(ChatFormatting.RED), false);
        return 1;
    }

    public static int disableMod(CommandContext<CommandSourceStack> context) {
        ConfigSettings.enabled = false;
        setFileValue("enabled", "false");
        context.getSource().sendSuccess(() -> Component.literal("Mod disabled").withStyle(ChatFormatting.RED), false);
        return 1;
    }

    public static int disableMessage(CommandContext<CommandSourceStack> context) {
        Integer id = IntegerArgumentType.getInteger(context, "id");
        for (Message m : ConfigSettings.messages) {
            if (m.ID == id) {
                m.Enabled = false;
                setMessageValue(m.ID, "enabled", "false");
                context.getSource().sendSuccess(() -> Component.literal("Message " + id + " disabled").withStyle(ChatFormatting.RED), false);
                return 1;
            }
        }
        context.getSource().sendSuccess(() -> Component.literal("Message not found").withStyle(ChatFormatting.RED), false);
        return 1;
    }
}
*///?}
