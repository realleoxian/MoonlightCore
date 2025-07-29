package de.leoxian.moonlightcore.fabric.mixin.client;

import de.leoxian.moonlightcore.api.network.MoonlightCustomPacket;
import de.leoxian.moonlightcore.api.network.PacketDispatcher;
import de.leoxian.moonlightcore.api.util.nullness.NotnullConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("all")
@Mixin(value = PacketDispatcher.class, remap = false)
public class PacketDispatcherImpl {

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void registerClientBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullConsumer<MSG> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, $1, byteBuf, $2) -> {
            MSG packet = codec.decode(byteBuf);

            client.execute(() -> handler.accept(packet));
        });
    }

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void sendToServer(MSG packet) {
        ResourceLocation id = packet.id();

        if(ClientPlayNetworking.canSend(id)) {
            ClientPlayNetworking.send(id, packet.toBuf());
        }
    }

}
