package jackperry2187.gentlereminders.fabric;

//? if fabric {
import jackperry2187.gentlereminders.GentleRemindersClient;
import net.fabricmc.api.ClientModInitializer;

public class GentleRemindersClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GentleRemindersClient.init();
    }
}
//?} else {
/*public class GentleRemindersClientFabric {
    // NeoForge stub - not used
}*/
//?}
