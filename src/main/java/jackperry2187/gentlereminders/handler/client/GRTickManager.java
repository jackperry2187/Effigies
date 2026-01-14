package jackperry2187.gentlereminders.handler.client;

import jackperry2187.gentlereminders.config.client.ConfigSettings;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

//? if fabric {
@Environment(value = EnvType.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class GRTickManager {
    public static int totalTickCounter = 0;
    public static int ticksWhenStartedShowing = 0;
    private static final int showDuration = 20 * 6; // 6 seconds

    public static void tickHandler() {
        if (totalTickCounter % ConfigSettings.ticksBetweenMessages == 0) {
            GRHUDHandler.startShowingMessage();
            ticksWhenStartedShowing = totalTickCounter;
        }

        if (totalTickCounter == ticksWhenStartedShowing + showDuration) {
            GRHUDHandler.stopShowingMessage();
            ticksWhenStartedShowing = -1;
        }

        // once in a world and the mod is enabled, increment the tick counter
        totalTickCounter++;
    }
}
