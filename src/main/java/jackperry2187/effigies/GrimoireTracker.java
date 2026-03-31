package jackperry2187.effigies;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.mojang.serialization.Codec;

//? if fabric {
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
//?} else {
/*import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
*///?}

//? if fabric {
public class GrimoireTracker extends PersistentState {
//?} else {
/*public class GrimoireTracker extends SavedData {
*///?}

    private final Set<UUID> givenPlayers;

    public GrimoireTracker() {
        this.givenPlayers = new HashSet<>();
    }

    private GrimoireTracker(Set<UUID> players) {
        this.givenPlayers = new HashSet<>(players);
    }

    private static final Codec<GrimoireTracker> CODEC =
        Codec.STRING.listOf()
            .fieldOf("given_players")
            .xmap(
                list -> {
                    Set<UUID> players = new HashSet<>();
                    for (String s : list) {
                        try {
                            players.add(UUID.fromString(s));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    return new GrimoireTracker(players);
                },
                tracker -> tracker.givenPlayers.stream().map(UUID::toString).toList()
            )
            .codec();

    //? if fabric {
    public static final PersistentStateType<GrimoireTracker> TYPE = new PersistentStateType<>(
        "effigies_grimoire_tracker",
        GrimoireTracker::new,
        CODEC,
        null
    );
    //?} else {
    /*public static final SavedDataType<GrimoireTracker> TYPE = new SavedDataType<>(
        "effigies_grimoire_tracker",
        GrimoireTracker::new,
        CODEC,
        null
    );
    *///?}

    public boolean hasReceivedGrimoire(UUID playerUuid) {
        return givenPlayers.contains(playerUuid);
    }

    public void markGrimoireGiven(UUID playerUuid) {
        givenPlayers.add(playerUuid);
        //? if fabric {
        markDirty();
        //?} else {
        /*setDirty();
        *///?}
    }

    public static GrimoireTracker get(MinecraftServer server) {
        //? if fabric {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
        //?} else {
        /*return server.overworld().getDataStorage().computeIfAbsent(TYPE);
        *///?}
    }
}
