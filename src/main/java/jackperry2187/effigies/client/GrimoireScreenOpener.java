package jackperry2187.effigies.client;

import net.minecraft.client.Minecraft;

public final class GrimoireScreenOpener {
    private GrimoireScreenOpener() {}

    public static void open() {
        Minecraft.getInstance().setScreen(new GrimoireScreen());
    }
}
