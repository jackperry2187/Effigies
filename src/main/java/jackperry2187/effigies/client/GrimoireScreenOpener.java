package jackperry2187.effigies.client;

//? if fabric {
import net.minecraft.client.MinecraftClient;
//?} else {
/*import net.minecraft.client.Minecraft;
*///?}

public final class GrimoireScreenOpener {
    private GrimoireScreenOpener() {}

    public static void open() {
        //? if fabric {
        MinecraftClient.getInstance().setScreen(new GrimoireScreen());
        //?} else {
        /*Minecraft.getInstance().setScreen(new GrimoireScreen());
        *///?}
    }
}
