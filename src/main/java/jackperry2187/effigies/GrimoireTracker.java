package jackperry2187.effigies;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.mojang.serialization.Codec;

import net.minecraft.server.MinecraftServer;
//? if mc261 {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class GrimoireTracker extends SavedData {

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

    public static final SavedDataType<GrimoireTracker> TYPE = new SavedDataType<>(
        //? if mc12011 {
        "effigies_grimoire_tracker",
        //?} else {
        /*Identifier.fromNamespaceAndPath("effigies", "grimoire_tracker"),
        *///?}
        GrimoireTracker::new,
        CODEC,
        null
    );

    public boolean hasReceivedGrimoire(UUID playerUuid) {
        return givenPlayers.contains(playerUuid);
    }

    public void markGrimoireGiven(UUID playerUuid) {
        givenPlayers.add(playerUuid);
        setDirty();
    }

    public static GrimoireTracker get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
