package jackperry2187.gentlereminders.config.client;

import jackperry2187.gentlereminders.GentleReminders;
import jackperry2187.gentlereminders.config.DefaultSettings;
import jackperry2187.gentlereminders.util.Message;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
*///?}

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class ConfigSettings {
    private static boolean isInitialized = false;

    //? if fabric {
    public static final Path configPath = FabricLoader.getInstance().getConfigDir();
    //?} else {
    /*public static final Path configPath = FMLPaths.CONFIGDIR.get();
    *///?}
    public static final Path modConfigFile = configPath.resolve(GentleReminders.MOD_ID + "-config.toml");

    public static int configVersion;

    public static boolean enabled;
    public static int ticksBetweenMessages;

    public static List<Message> messages = new ArrayList<>();
    public static int highestMessageID = 0;

    private static String displayStyle = "default";
    
    //? if fabric {
    //? if mcGte121 {
    private static final Identifier DEFAULT_CUSTOM_TOAST = Identifier.ofVanilla("textures/gui/sprites/toast/system.png");
    //?} else {
    /*private static final Identifier DEFAULT_CUSTOM_TOAST = Identifier.of("minecraft", "textures/gui/sprites/toast/system.png");
    *///?}
    
    //? if mcGte121 {
    private static final Identifier LIGHT_MODE = Identifier.of("gentlereminders", "custom_toasts/system_light.png");
    private static final Identifier DARK_MODE = Identifier.of("gentlereminders", "custom_toasts/system_dark.png");
    public static Identifier CUSTOM_BG_TEXTURE = Identifier.ofVanilla("textures/block/dark_oak_planks.png");
    public static Identifier CUSTOM_BORDER_TEXTURE = Identifier.ofVanilla("textures/block/amethyst_block.png");
    //?} else {
    /*private static final Identifier LIGHT_MODE = new Identifier("gentlereminders", "custom_toasts/system_light.png");
    private static final Identifier DARK_MODE = new Identifier("gentlereminders", "custom_toasts/system_dark.png");
    public static Identifier CUSTOM_BG_TEXTURE = Identifier.of("minecraft", "textures/block/dark_oak_planks.png");
    public static Identifier CUSTOM_BORDER_TEXTURE = Identifier.of("minecraft", "textures/block/amethyst_block.png");
    *///?}
    
    public static Boolean CUSTOM_INCLUDE_ICON = true;
    public static Identifier toastTexture = DEFAULT_CUSTOM_TOAST;
    //?} else {
    /*private static final ResourceLocation DEFAULT_CUSTOM_TOAST = ResourceLocation.withDefaultNamespace("textures/gui/sprites/toast/system.png");
    private static final ResourceLocation LIGHT_MODE = ResourceLocation.fromNamespaceAndPath("gentlereminders", "custom_toasts/system_light.png");
    private static final ResourceLocation DARK_MODE = ResourceLocation.fromNamespaceAndPath("gentlereminders", "custom_toasts/system_dark.png");
    public static ResourceLocation CUSTOM_BG_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/dark_oak_planks.png");
    public static ResourceLocation CUSTOM_BORDER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/amethyst_block.png");
    public static Boolean CUSTOM_INCLUDE_ICON = true;
    public static ResourceLocation toastTexture = DEFAULT_CUSTOM_TOAST;
    *///?}

    private static final boolean isDebug = false;
    private static final int debugTicksBetweenMessages = 20 * 7;

    public static void initialize() {
        if (isInitialized) {
            GentleReminders.LOGGER.error("ConfigSettings have already been initialized!");
            return;
        }

        if (!Files.exists(modConfigFile)) {
            GentleReminders.LOGGER.error("Config file does not exist!");
            throw new RuntimeException("Config file does not exist!");
        }

        readConfigFile();
        if (isDebug) ticksBetweenMessages = debugTicksBetweenMessages;

        messages.sort(Comparator.comparingInt(m -> m.ID));
        highestMessageID = messages.get(messages.size() - 1).ID;

        isInitialized = true;
        if (isDebug) logConfigSettings();
    }

    private static void logConfigSettings() {
        GentleReminders.LOGGER.info("Config Version: {}", configVersion);
        GentleReminders.LOGGER.info("Enabled: {}", enabled);
        GentleReminders.LOGGER.info("Display Style: {}", displayStyle);
        GentleReminders.LOGGER.info("Time Between Messages: {} ticks, {} minutes", ticksBetweenMessages, ticksBetweenMessages / 20 / 60);
    }

    private static void readConfigFile() {
        try {
            boolean isReadingMessages = false;
            for (String line : Files.readAllLines(modConfigFile)) {
                if (line.startsWith("#")) continue;
                if (line.startsWith("configVersion=")) {
                    configVersion = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("enabled=")) {
                    enabled = Boolean.parseBoolean(line.split("=")[1]);
                } else if (line.startsWith("displayStyle=")) {
                    String got = line.split("=")[1].strip().replace("\"", "");
                    switch (got) {
                        case "default":
                            toastTexture = DEFAULT_CUSTOM_TOAST;
                            displayStyle = "default";
                            break;
                        case "light":
                            toastTexture = LIGHT_MODE;
                            displayStyle = "light";
                            break;
                        case "dark":
                            toastTexture = DARK_MODE;
                            displayStyle = "dark";
                            break;
                        case "custom":
                            toastTexture = CUSTOM_BG_TEXTURE;
                            displayStyle = "custom";
                            break;
                        case "chat":
                            displayStyle = "chat";
                            break;
                        default:
                            GentleReminders.LOGGER.error("Invalid display style: {}! Should be default, light, dark, or chat; setting to default", got);
                            displayStyle = "default";
                            break;
                    }
                } else if (line.startsWith("timeBetweenMessages=")) {
                    ticksBetweenMessages = Integer.parseInt(line.split("=")[1]) * 20 * 60;
                } else if (line.startsWith("customBGTexture=")) {
                    String value = line.split("=", 2)[1].strip().replace("\"", "");
                    // Extract block name from "minecraft:block_name" format
                    String blockName = value.contains(":") ? value.split(":")[1] : value;
                    //? if fabric {
                    //? if mcGte121 {
                    CUSTOM_BG_TEXTURE = Identifier.ofVanilla("textures/block/" + blockName + ".png");
                    //?} else {
                    /*CUSTOM_BG_TEXTURE = new Identifier("minecraft", "textures/block/" + blockName + ".png");
                    *///?}
                    //?} else {
                    /*CUSTOM_BG_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/" + blockName + ".png");
                    *///?}
                } else if (line.startsWith("customBorderTexture=")) {
                    String value = line.split("=", 2)[1].strip().replace("\"", "");
                    // Extract block name from "minecraft:block_name" format
                    String blockName = value.contains(":") ? value.split(":")[1] : value;
                    //? if fabric {
                    //? if mcGte121 {
                    CUSTOM_BORDER_TEXTURE = Identifier.ofVanilla("textures/block/" + blockName + ".png");
                    //?} else {
                    /*CUSTOM_BORDER_TEXTURE = new Identifier("minecraft", "textures/block/" + blockName + ".png");
                    *///?}
                    //?} else {
                    /*CUSTOM_BORDER_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/" + blockName + ".png");
                    *///?}
                } else if (line.startsWith("customIncludeIcon=")) {
                    String value = line.split("=", 2)[1].strip().replace("\"", "");
                    CUSTOM_INCLUDE_ICON = Boolean.parseBoolean(value);
                } else if (line.startsWith("messages=[")) {
                    isReadingMessages = true;
                } else if (isReadingMessages) {
                    if (line.startsWith("]")) {
                        isReadingMessages = false;
                    } else {
                        messages.add(parseMessage(line));
                    }
                }
            }
        } catch (Exception e) {
            GentleReminders.LOGGER.error("Failed to read config file!");
            throw new RuntimeException(e);
        }
    }

    private static Message parseMessage(String line) {
        String[][] parts = Arrays.stream(line.split(",")).map(s -> s.trim().replace("\"", "").split("=")).toArray(String[][]::new);
        int id = Integer.parseInt(parts[0][1]);
        String title = parts[1][1];
        String message = parts[2][1];
        boolean messageEnabled = Boolean.parseBoolean(parts[3][1]);
        String titleColor = parts[4][1];
        String messageColor = parts[5][1];

        //? if fabric {
        Text titleText = MutableText.of(Text.of(title).getContent()).formatted(Formatting.byName(titleColor));
        Text messageText = MutableText.of(Text.of(message).getContent()).formatted(Formatting.byName(messageColor));
        //?} else {
        /*Component titleText = Component.literal(title).withStyle(ChatFormatting.getByName(titleColor));
        Component messageText = Component.literal(message).withStyle(ChatFormatting.getByName(messageColor));
        *///?}

        return new Message(id, titleText, messageText, messageEnabled);
    }

    public static String getDisplayStyle() {
        return displayStyle;
    }

    public static void setDisplayStyle(String style) {
        switch (style) {
            case "default":
                toastTexture = DEFAULT_CUSTOM_TOAST;
                displayStyle = "default";
                break;
            case "light":
                toastTexture = LIGHT_MODE;
                displayStyle = "light";
                break;
            case "dark":
                toastTexture = DARK_MODE;
                displayStyle = "dark";
                break;
            case "custom":
                toastTexture = CUSTOM_BG_TEXTURE;
                displayStyle = "custom";
                break;
            case "chat":
                displayStyle = "chat";
                break;
            default:
                GentleReminders.LOGGER.error("Invalid display style: {}! Should be default or chat, setting to default", style);
                displayStyle = "default";
                break;
        }
    }

    //? if fabric {
    public static void setCustomBackgroundTexture(Identifier texture) {
        CUSTOM_BG_TEXTURE = texture;
    }

    public static void setCustomBorderTexture(Identifier texture) {
        CUSTOM_BORDER_TEXTURE = texture;
    }
    //?} else {
    /*public static void setCustomBackgroundTexture(ResourceLocation texture) {
        CUSTOM_BG_TEXTURE = texture;
    }

    public static void setCustomBorderTexture(ResourceLocation texture) {
        CUSTOM_BORDER_TEXTURE = texture;
    }
    *///?}

    public static void setCustomIncludeIcon(Boolean include) {
        CUSTOM_INCLUDE_ICON = include;
    }
}
