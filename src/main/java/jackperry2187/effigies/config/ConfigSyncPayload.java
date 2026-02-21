package jackperry2187.effigies.config;

import jackperry2187.effigies.Effigies;

//? if fabric {
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
//?} else {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
*///?}

/**
 * Network payload for syncing server config values to clients.
 * Sent to each player on join so tooltips reflect server settings.
 */
public record ConfigSyncPayload(
    int woodenPikeRadius,
    int stonePikeRadius,
    int copperPikeRadius,
    int ironPikeRadius,
    int goldenPikeRadius,
    int diamondPikeRadius,
    int netheritePikeRadius
//? if fabric {
) implements CustomPayload {
//?} else {
/*) implements CustomPacketPayload {
*///?}

    //? if fabric {
    public static final Id<ConfigSyncPayload> ID = new Id<>(Identifier.of(Effigies.MOD_ID, "config_sync"));

    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = new PacketCodec<>() {
        @Override
        public ConfigSyncPayload decode(RegistryByteBuf buf) {
            return new ConfigSyncPayload(
                buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readInt(), buf.readInt(), buf.readInt()
            );
        }

        @Override
        public void encode(RegistryByteBuf buf, ConfigSyncPayload value) {
            buf.writeInt(value.woodenPikeRadius());
            buf.writeInt(value.stonePikeRadius());
            buf.writeInt(value.copperPikeRadius());
            buf.writeInt(value.ironPikeRadius());
            buf.writeInt(value.goldenPikeRadius());
            buf.writeInt(value.diamondPikeRadius());
            buf.writeInt(value.netheritePikeRadius());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    //?} else {
    /*public static final Type<ConfigSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Effigies.MOD_ID, "config_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ConfigSyncPayload decode(RegistryFriendlyByteBuf buf) {
            return new ConfigSyncPayload(
                buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readInt(), buf.readInt(), buf.readInt()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ConfigSyncPayload value) {
            buf.writeInt(value.woodenPikeRadius());
            buf.writeInt(value.stonePikeRadius());
            buf.writeInt(value.copperPikeRadius());
            buf.writeInt(value.ironPikeRadius());
            buf.writeInt(value.goldenPikeRadius());
            buf.writeInt(value.diamondPikeRadius());
            buf.writeInt(value.netheritePikeRadius());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    *///?}

    public static ConfigSyncPayload fromCurrentConfig() {
        return new ConfigSyncPayload(
            ConfigSettings.woodenPikeRadius,
            ConfigSettings.stonePikeRadius,
            ConfigSettings.copperPikeRadius,
            ConfigSettings.ironPikeRadius,
            ConfigSettings.goldenPikeRadius,
            ConfigSettings.diamondPikeRadius,
            ConfigSettings.netheritePikeRadius
        );
    }
}
