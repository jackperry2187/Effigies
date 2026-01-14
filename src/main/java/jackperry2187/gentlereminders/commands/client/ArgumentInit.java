//? if fabric {
package jackperry2187.gentlereminders.commands.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import jackperry2187.gentlereminders.commands.client.customArguments.DisplayStyleArgument;
import jackperry2187.gentlereminders.commands.client.customArguments.FormattingArgument;
import jackperry2187.gentlereminders.commands.client.customArguments.TextureArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Environment(value = EnvType.CLIENT)
public class ArgumentInit {
    public static LiteralArgumentBuilder<FabricClientCommandSource> getCommandsWithArguments() {
        return ClientCommandManager.literal("gentlereminders").executes(CommandInit::help)
                .then(ClientCommandManager.literal("help").executes(CommandInit::help))
                .then(ClientCommandManager.literal("get")
                        .then(ClientCommandManager.literal("TimeRemaining").executes(CommandInit::getTimeUntilNextMessage))
                        .then(ClientCommandManager.literal("ConfigPath").executes(CommandInit::getConfigPath))
                        .then(ClientCommandManager.literal("ConfigVersion").executes(CommandInit::getConfigVersion))
                        .then(ClientCommandManager.literal("DisplayStyle").executes(CommandInit::getDisplayStyle))
                        .then(ClientCommandManager.literal("TicksBetweenMessages").executes(CommandInit::getTicksBetweenMessages))
                        .then(ClientCommandManager.literal("Messages")
                                .then(ClientCommandManager.argument("pageNumber", IntegerArgumentType.integer(1))
                                .executes(CommandInit::getMessages)))
                )
                .then(ClientCommandManager.literal("set")
                        .then(ClientCommandManager.literal("DisplayStyle")
                                .then(ClientCommandManager.argument("style", DisplayStyleArgument.displayStyle())
                                .executes(CommandInit::setDisplayStyle)))
                        .then(ClientCommandManager.literal("CustomDisplayStyle")
                                .then(ClientCommandManager.argument("bgTexture", TextureArgument.textureArgument())
                                .then(ClientCommandManager.argument("borderTexture", TextureArgument.textureArgument())
                                .then(ClientCommandManager.argument("includeIcon", BoolArgumentType.bool())
                                .executes(CommandInit::setCustomDisplayStyle)))))
                        .then(ClientCommandManager.literal("TicksBetweenMessages")
                                .then(ClientCommandManager.argument("ticks", IntegerArgumentType.integer())
                                .executes(CommandInit::setTicksBetweenMessages)))
                        .then(ClientCommandManager.literal("Message")
                                .then(ClientCommandManager.argument("id", IntegerArgumentType.integer())
                                .then(ClientCommandManager.argument("title", StringArgumentType.string())
                                .then(ClientCommandManager.argument("message", StringArgumentType.string())
                                .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .then(ClientCommandManager.argument("titleColor", FormattingArgument.formatting())
                                .then(ClientCommandManager.argument("messageColor", FormattingArgument.formatting())
                                .executes(CommandInit::setMessage))))))))
                )
                .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.literal("Message")
                                .then(ClientCommandManager.argument("title", StringArgumentType.string())
                                .then(ClientCommandManager.argument("message", StringArgumentType.string())
                                .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .then(ClientCommandManager.argument("titleColor", FormattingArgument.formatting())
                                .then(ClientCommandManager.argument("messageColor", FormattingArgument.formatting())
                                .executes(CommandInit::addMessage)))))))
                )
                .then(ClientCommandManager.literal("remove")
                        .then(ClientCommandManager.literal("Message")
                                .then(ClientCommandManager.argument("id", IntegerArgumentType.integer())
                                .executes(CommandInit::removeMessage)))
                )
                .then(ClientCommandManager.literal("enable")
                        .then(ClientCommandManager.literal("GentleReminders")
                                .executes(CommandInit::enableMod))
                        .then(ClientCommandManager.literal("Message")
                                .then(ClientCommandManager.argument("id", IntegerArgumentType.integer())
                                .executes(CommandInit::enableMessage)))
                )
                .then(ClientCommandManager.literal("disable")
                        .then(ClientCommandManager.literal("GentleReminders")
                                .executes(CommandInit::disableMod))
                        .then(ClientCommandManager.literal("Message")
                                .then(ClientCommandManager.argument("id", IntegerArgumentType.integer())
                                .executes(CommandInit::disableMessage)))
                );
    }
}
//?} else {
/*package jackperry2187.gentlereminders.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ArgumentInit {
    // Suggestion provider for display styles
    private static final SuggestionProvider<CommandSourceStack> DISPLAY_STYLE_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggest(Arrays.asList("default", "light", "dark", "chat", "custom"), builder);
    };

    // Suggestion provider for block textures
    private static final SuggestionProvider<CommandSourceStack> BLOCK_TEXTURE_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggest(
            BuiltInRegistries.BLOCK.keySet().stream()
                .map(ResourceLocation::getPath)
                .collect(Collectors.toList()),
            builder
        );
    };

    // Suggestion provider for colors
    private static final SuggestionProvider<CommandSourceStack> COLOR_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggest(
            Arrays.stream(ChatFormatting.values())
                .filter(ChatFormatting::isColor)
                .map(cf -> cf.getName().toLowerCase())
                .collect(Collectors.toList()),
            builder
        );
    };

    public static void registerNeoForgeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gentlereminders")
                .executes(CommandInit::help)
                .then(Commands.literal("help").executes(CommandInit::help))
                .then(Commands.literal("get")
                        .then(Commands.literal("TimeRemaining").executes(CommandInit::getTimeUntilNextMessage))
                        .then(Commands.literal("ConfigPath").executes(CommandInit::getConfigPath))
                        .then(Commands.literal("ConfigVersion").executes(CommandInit::getConfigVersion))
                        .then(Commands.literal("DisplayStyle").executes(CommandInit::getDisplayStyle))
                        .then(Commands.literal("TicksBetweenMessages").executes(CommandInit::getTicksBetweenMessages))
                        .then(Commands.literal("Messages")
                                .then(Commands.argument("pageNumber", IntegerArgumentType.integer(1))
                                .executes(CommandInit::getMessages)))
                )
                .then(Commands.literal("set")
                        .then(Commands.literal("DisplayStyle")
                                .then(Commands.argument("style", StringArgumentType.word())
                                .suggests(DISPLAY_STYLE_SUGGESTIONS)
                                .executes(CommandInit::setDisplayStyle)))
                        .then(Commands.literal("CustomDisplayStyle")
                                .then(Commands.argument("bgTexture", StringArgumentType.word())
                                .suggests(BLOCK_TEXTURE_SUGGESTIONS)
                                .then(Commands.argument("borderTexture", StringArgumentType.word())
                                .suggests(BLOCK_TEXTURE_SUGGESTIONS)
                                .then(Commands.argument("includeIcon", BoolArgumentType.bool())
                                .executes(CommandInit::setCustomDisplayStyle)))))
                        .then(Commands.literal("TicksBetweenMessages")
                                .then(Commands.argument("ticks", IntegerArgumentType.integer())
                                .executes(CommandInit::setTicksBetweenMessages)))
                        .then(Commands.literal("Message")
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                .then(Commands.argument("title", StringArgumentType.string())
                                .then(Commands.argument("message", StringArgumentType.string())
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .then(Commands.argument("titleColor", StringArgumentType.word())
                                .suggests(COLOR_SUGGESTIONS)
                                .then(Commands.argument("messageColor", StringArgumentType.word())
                                .suggests(COLOR_SUGGESTIONS)
                                .executes(CommandInit::setMessage))))))))
                )
                .then(Commands.literal("add")
                        .then(Commands.literal("Message")
                                .then(Commands.argument("title", StringArgumentType.string())
                                .then(Commands.argument("message", StringArgumentType.string())
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .then(Commands.argument("titleColor", StringArgumentType.word())
                                .suggests(COLOR_SUGGESTIONS)
                                .then(Commands.argument("messageColor", StringArgumentType.word())
                                .suggests(COLOR_SUGGESTIONS)
                                .executes(CommandInit::addMessage)))))))
                )
                .then(Commands.literal("remove")
                        .then(Commands.literal("Message")
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                .executes(CommandInit::removeMessage)))
                )
                .then(Commands.literal("enable")
                        .then(Commands.literal("GentleReminders")
                                .executes(CommandInit::enableMod))
                        .then(Commands.literal("Message")
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                .executes(CommandInit::enableMessage)))
                )
                .then(Commands.literal("disable")
                        .then(Commands.literal("GentleReminders")
                                .executes(CommandInit::disableMod))
                        .then(Commands.literal("Message")
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                .executes(CommandInit::disableMessage)))
                )
        );
    }
}
*///?}
