package jackperry2187.effigies;

import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.registry.ModItems;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.mojang.serialization.Codec;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//? if mc261 {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.world.item.ItemStack;
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

    public static boolean giveGrimoireIfNeeded(MinecraftServer server, ServerPlayer player) {
        if (!ConfigSettings.giveGrimoireOnJoin) {
            return false;
        }

        GrimoireTracker tracker = get(server);
        if (tracker.hasReceivedGrimoire(player.getUUID())) {
            return false;
        }

        ItemStack grimoire = new ItemStack(ModItems.grimoire());
        if (!player.getInventory().add(grimoire)) {
            player.drop(grimoire, false);
        }
        tracker.markGrimoireGiven(player.getUUID());
        Effigies.LOGGER.info("Gave grimoire to player {}", player.getName().getString());
        return true;
    }

    public static GrimoireTracker get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
