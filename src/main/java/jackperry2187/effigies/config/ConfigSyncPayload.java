package jackperry2187.effigies.config;

import jackperry2187.effigies.Effigies;

import java.util.HashMap;
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
    Map<String, String> blockEntityMappings
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
            int mappingCount = buf.readInt();
            Map<String, String> mappings = new HashMap<>(mappingCount);
            for (int i = 0; i < mappingCount; i++) {
                mappings.put(buf.readUtf(), buf.readUtf());
            }
            return new ConfigSyncPayload(giveGrimoire, wooden, stone, copper, iron, golden, diamond, netherite, mappings);
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
            Map<String, String> mappings = value.blockEntityMappings();
            buf.writeInt(mappings.size());
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeUtf(entry.getValue());
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
            ConfigSettings.getBlockToEntityMappings()
        );
    }
}
