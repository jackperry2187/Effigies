package jackperry2187.gentlereminders.util;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
*///?}

import static jackperry2187.gentlereminders.config.DefaultSettings.*;

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class Message {
    private static int messageCount = 0;
    public int ID;
    //? if fabric {
    public Text Title;
    public Text Description;
    //?} else {
    /*public Component Title;
    public Component Description;
    *///?}
    public boolean Enabled;

    //? if fabric {
    public Message(int id, Text title, Text description, boolean enabled) {
        ID = id;
        Title = title;
        Description = description;
        Enabled = enabled;
    }

    public Message(Text title, Text description, boolean enabled) {
        ID = messageCount++;
        Title = title;
        Description = description;
        Enabled = enabled;
    }
    //?} else {
    /*public Message(int id, Component title, Component description, boolean enabled) {
        ID = id;
        Title = title;
        Description = description;
        Enabled = enabled;
    }

    public Message(Component title, Component description, boolean enabled) {
        ID = messageCount++;
        Title = title;
        Description = description;
        Enabled = enabled;
    }
    *///?}

    public String toString() {
        //? if fabric {
        String titleColor = Title.getStyle().getColor().getName();
        String messageColor = Description.getStyle().getColor().getName();
        //?} else {
        /*String titleColor = Title.getStyle().getColor() != null ? Title.getStyle().getColor().serialize() : "white";
        String messageColor = Description.getStyle().getColor() != null ? Description.getStyle().getColor().serialize() : "white";
        *///?}

        return " {id=" + ID + ", title=\"" + Title.getString() + "\", message=\"" + Description.getString() + "\", enabled=" + Enabled + ", titleColor=\"" + titleColor + "\", messageColor=\"" + messageColor + "\"},";
    }

    //? if fabric {
    public static Message generateSimpleMessage(String message) {
        return new Message(
                MutableText.of(Text.of(defaultTitle).getContent()).formatted(defaultTitleColor),
                MutableText.of(Text.of(message).getContent()).formatted(defaultMessageColor),
                true
        );
    }

    public static Message generateUniqueMessage(String title, String message, boolean enabled, Formatting titleColor, Formatting messageColor) {
        return new Message(
                MutableText.of(Text.of(title).getContent()).formatted(titleColor),
                MutableText.of(Text.of(message).getContent()).formatted(messageColor),
                enabled
        );
    }
    //?} else {
    /*public static Message generateSimpleMessage(String message) {
        return new Message(
                Component.literal(defaultTitle).withStyle(defaultTitleColor),
                Component.literal(message).withStyle(defaultMessageColor),
                true
        );
    }

    public static Message generateUniqueMessage(String title, String message, boolean enabled, ChatFormatting titleColor, ChatFormatting messageColor) {
        return new Message(
                Component.literal(title).withStyle(titleColor),
                Component.literal(message).withStyle(messageColor),
                enabled
        );
    }
    *///?}
}
