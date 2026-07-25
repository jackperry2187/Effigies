package jackperry2187.effigies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.Identifier;

public final class Effigies {
    public static final String MOD_ID = "effigies";
    public static final Logger LOGGER = LoggerFactory.getLogger("Effigies");

    private Effigies() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
