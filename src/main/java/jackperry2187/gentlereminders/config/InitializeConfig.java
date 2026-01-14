package jackperry2187.gentlereminders.config;

import jackperry2187.gentlereminders.GentleReminders;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
*///?}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static jackperry2187.gentlereminders.config.ConfigLines.getBaseConfigLines;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class InitializeConfig {
    //? if fabric {
    private final static Path configPath = FabricLoader.getInstance().getConfigDir();
    //?} else {
    /*private final static Path configPath = FMLPaths.CONFIGDIR.get();
    *///?}
    private final static Path modConfigFile = configPath.resolve(GentleReminders.MOD_ID + "-config.toml");

    public static void generateConfigFile() {
        try {
            if (Files.exists(modConfigFile)) {
                GentleReminders.LOGGER.info("Config file already exists!");
                if (!checkConfigVersion()) {
                    GentleReminders.LOGGER.info("Config version is different, overwriting config file!");
                    Files.write(modConfigFile, getBaseConfigLines(), StandardOpenOption.TRUNCATE_EXISTING);
                }
                GentleReminders.LOGGER.info("Config version is correct!");
            } else {
                GentleReminders.LOGGER.info("Creating config file!");
                Files.createFile(modConfigFile);
                Files.write(modConfigFile, getBaseConfigLines(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            GentleReminders.LOGGER.error("Failed to create config file!");
            throw new RuntimeException(e);
        }
    }

    public static boolean checkConfigVersion() throws IOException {
        if (!Files.exists(modConfigFile)) Files.createFile(modConfigFile);
        boolean isCorrectVersion = false;

        for (String line : Files.readAllLines(modConfigFile)) {
            if (line.startsWith("#")) continue;
            if (line.startsWith("configVersion=")) {
                String version = line.split("=")[1];
                if (Integer.parseInt(version) == DefaultSettings.configVersion) {
                    isCorrectVersion = true;
                    break;
                }
            }
        }

        return isCorrectVersion;
    }
}
