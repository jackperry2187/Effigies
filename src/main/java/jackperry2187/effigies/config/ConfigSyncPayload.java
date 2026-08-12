package jackperry2187.effigies.config;

import jackperry2187.effigies.Effigies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for syncing server config values to clients.
 * Sent to each player on join so tooltips reflect server settings.
 */
public record ConfigSyncPayload(
    boolean giveGrimoireOnJoin,
    int woodenPikeRadius,
    int stonePikeRadius,
    int copperPikeRadius,
    int ironPikeRadius,
    int goldenPikeRadius,
    int diamondPikeRadius,
    int netheritePikeRadius,
    int antiPikeMinSpawnDelay,
    int antiPikeMaxSpawnDelay,
    int antiPikeMaxNearbyEntities,
    int antiPikeSpawnRange,
    int antiPikeActivationRange,
    Map<String, String> blockEntityMappings,
    List<String> dimensionWhitelist
) implements CustomPacketPayload {

    public static final Type<ConfigSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Effigies.MOD_ID, "config_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ConfigSyncPayload decode(RegistryFriendlyByteBuf buf) {
            boolean giveGrimoire = buf.readBoolean();
            int wooden = buf.readInt();
            int stone = buf.readInt();
            int copper = buf.readInt();
            int iron = buf.readInt();
            int golden = buf.readInt();
            int diamond = buf.readInt();
            int netherite = buf.readInt();
            int antiMinDelay = buf.readInt();
            int antiMaxDelay = buf.readInt();
            int antiMaxNearby = buf.readInt();
            int antiSpawnRange = buf.readInt();
            int antiActivationRange = buf.readInt();
            int mappingCount = buf.readInt();
            Map<String, String> mappings = new HashMap<>(mappingCount);
            for (int i = 0; i < mappingCount; i++) {
                mappings.put(buf.readUtf(), buf.readUtf());
            }
            int whitelistCount = buf.readInt();
            List<String> whitelist = new ArrayList<>(whitelistCount);
            for (int i = 0; i < whitelistCount; i++) {
                whitelist.add(buf.readUtf());
            }
            return new ConfigSyncPayload(giveGrimoire, wooden, stone, copper, iron, golden, diamond, netherite,
                antiMinDelay, antiMaxDelay, antiMaxNearby, antiSpawnRange, antiActivationRange, mappings, whitelist);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ConfigSyncPayload value) {
            buf.writeBoolean(value.giveGrimoireOnJoin());
            buf.writeInt(value.woodenPikeRadius());
            buf.writeInt(value.stonePikeRadius());
            buf.writeInt(value.copperPikeRadius());
            buf.writeInt(value.ironPikeRadius());
            buf.writeInt(value.goldenPikeRadius());
            buf.writeInt(value.diamondPikeRadius());
            buf.writeInt(value.netheritePikeRadius());
            buf.writeInt(value.antiPikeMinSpawnDelay());
            buf.writeInt(value.antiPikeMaxSpawnDelay());
            buf.writeInt(value.antiPikeMaxNearbyEntities());
            buf.writeInt(value.antiPikeSpawnRange());
            buf.writeInt(value.antiPikeActivationRange());
            Map<String, String> mappings = value.blockEntityMappings();
            buf.writeInt(mappings.size());
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeUtf(entry.getValue());
            }
            List<String> whitelist = value.dimensionWhitelist();
            buf.writeInt(whitelist.size());
            for (String dimension : whitelist) {
                buf.writeUtf(dimension);
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static ConfigSyncPayload fromCurrentConfig() {
        return new ConfigSyncPayload(
            ConfigSettings.giveGrimoireOnJoin,
            ConfigSettings.woodenPikeRadius,
            ConfigSettings.stonePikeRadius,
            ConfigSettings.copperPikeRadius,
            ConfigSettings.ironPikeRadius,
            ConfigSettings.goldenPikeRadius,
            ConfigSettings.diamondPikeRadius,
            ConfigSettings.netheritePikeRadius,
            ConfigSettings.antiPikeMinSpawnDelay,
            ConfigSettings.antiPikeMaxSpawnDelay,
            ConfigSettings.antiPikeMaxNearbyEntities,
            ConfigSettings.antiPikeSpawnRange,
            ConfigSettings.antiPikeActivationRange,
            ConfigSettings.getBlockToEntityMappings(),
            ConfigSettings.getDimensionWhitelist()
        );
    }
}
