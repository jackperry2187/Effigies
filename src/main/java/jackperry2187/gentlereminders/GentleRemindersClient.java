//? if fabric {
package jackperry2187.gentlereminders;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import jackperry2187.gentlereminders.config.client.ConfigSettings;

import static jackperry2187.gentlereminders.commands.client.RegisterCommands.*;
import static jackperry2187.gentlereminders.handler.client.GRHUDHandler.HUDHandler;
import static jackperry2187.gentlereminders.handler.client.GRTickManager.tickHandler;

public class GentleRemindersClient {
    public static void init() {
        // initialize the config settings
        ConfigSettings.initialize();

        // initialize client arguments and commands
        registerArguments();
        registerClientCommands();

        // Register HUD render event
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!ConfigSettings.enabled) return;
            HUDHandler(drawContext);
        });

        // Register client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ConfigSettings.enabled) return;
            if (client.world == null) return;
            tickHandler();
        });
    }
}
//?} else {
/*package jackperry2187.gentlereminders;

public class GentleRemindersClient {
    public static void init() {
        // NeoForge: Events and initialization handled in GentleRemindersNeoForge
    }
}
*///?}
