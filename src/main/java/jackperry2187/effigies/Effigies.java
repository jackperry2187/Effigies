package jackperry2187.effigies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if fabric {
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}

public final class Effigies {
    public static final String MOD_ID = "effigies";
    public static final Logger LOGGER = LoggerFactory.getLogger("Effigies");

    private Effigies() {
    }

    //? if fabric {
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    //?} else {
    /*public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
    *///?}
}
