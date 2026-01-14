//? if fabric {
package jackperry2187.gentlereminders.commands.client.customArguments;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.registry.Registries;

public class TextureArgument implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        // Read a single word (block name like "oak_planks")
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        Registries.BLOCK.getIds().forEach(id -> {
            String blockName = id.getPath(); // Just the block name, not the full namespace
            if (remaining.isEmpty() || blockName.toLowerCase().startsWith(remaining)) {
                builder.suggest(blockName);
            }
        });

        return builder.buildFuture();
    }

    public static TextureArgument textureArgument() {
        return new TextureArgument();
    }
}
//?} else {
/*package jackperry2187.gentlereminders.commands.client.customArguments;

// NeoForge stub - commands not implemented
public class TextureArgument {
}
*///?}
