//? if neoforge {
/*package jackperry2187.gentlereminders.neoforge;

import jackperry2187.gentlereminders.GentleReminders;
import jackperry2187.gentlereminders.commands.client.RegisterCommands;
import jackperry2187.gentlereminders.config.client.ConfigSettings;
import jackperry2187.gentlereminders.handler.client.GRHUDHandler;
import jackperry2187.gentlereminders.handler.client.GRTickManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@Mod(GentleReminders.MOD_ID)
public class GentleRemindersNeoForge {
    public GentleRemindersNeoForge(IEventBus modEventBus) {
        GentleReminders.init();
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ConfigSettings.initialize();
            NeoForge.EVENT_BUS.register(this);
        }
    }
    
    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        RegisterCommands.onRegisterClientCommands(event);
    }
    
    @SubscribeEvent
    public void onRenderHud(RenderGuiLayerEvent.Post event) {
        if (!ConfigSettings.enabled) return;
        GRHUDHandler.HUDHandler(event.getGuiGraphics());
    }
    
    @SubscribeEvent
    public void onClientTick(LevelTickEvent.Post event) {
        if (!ConfigSettings.enabled) return;
        if (event.getLevel() != Minecraft.getInstance().level) return;
        if (Minecraft.getInstance().level == null) return;
        GRTickManager.tickHandler();
    }
}
*///?} else {
package jackperry2187.gentlereminders.neoforge;

public class GentleRemindersNeoForge {
    // Fabric stub - not used
}
//?}
