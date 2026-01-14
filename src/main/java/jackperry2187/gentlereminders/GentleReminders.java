package jackperry2187.gentlereminders;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.minecraft.resources.ResourceLocation;
*///?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jackperry2187.gentlereminders.config.InitializeConfig.generateConfigFile;

public class GentleReminders {
    public static final Logger LOGGER = LoggerFactory.getLogger("Gentle Reminders");
    public static final String MOD_ID = "gentlereminders";

    public static void init() {
        LOGGER.info("Initializing Gentle Reminders...");

        //? if fabric {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            generateConfigFile();
        } else {
            LOGGER.warn("Gentle Reminders is a client-side mod and will not be loaded on the server!");
        }
        //?} else {
        /*if (FMLEnvironment.dist == Dist.CLIENT) {
            generateConfigFile();
        } else {
            LOGGER.warn("Gentle Reminders is a client-side mod and will not be loaded on the server!");
        }
        *///?}

        LOGGER.info("Initialized Successfully!");
    }

    //? if fabric && mcGte121 {
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    //?} elif fabric && !mcGte121 {
    /*public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
    *///?} elif neoforge {
    /*public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    *///?}
}
