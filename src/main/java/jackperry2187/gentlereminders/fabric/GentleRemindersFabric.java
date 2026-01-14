package jackperry2187.gentlereminders.fabric;

//? if fabric {
import jackperry2187.gentlereminders.GentleReminders;
import net.fabricmc.api.ModInitializer;

public class GentleRemindersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GentleReminders.init();
    }
}
//?} else {
/*public class GentleRemindersFabric {
    // NeoForge stub - not used
}*/
//?}
