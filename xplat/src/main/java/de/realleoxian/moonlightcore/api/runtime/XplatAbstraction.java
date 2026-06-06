package de.realleoxian.moonlightcore.api.runtime;

import de.realleoxian.moonlightcore.api.EnvironmentSide;
import de.realleoxian.moonlightcore.api.ModContainer;
import de.realleoxian.moonlightcore.api.ModLoadContext;
import de.realleoxian.moonlightcore.api.capability.BlockCapabilities;
import de.realleoxian.moonlightcore.api.capability.EntityCapabilities;
import de.realleoxian.moonlightcore.api.capability.ItemCapabilities;
import de.realleoxian.moonlightcore.api.command.CommandRegistrar;
import de.realleoxian.moonlightcore.api.entity.EntityAttributeRegistrar;
import de.realleoxian.moonlightcore.api.network.ServerNetworking;
import de.realleoxian.moonlightcore.api.permissions.PermissionAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@ApiStatus.Internal
public interface XplatAbstraction<T extends ModLoadContext> {
    void initializeMod(String modId, T loadContext, Consumer<ModContainer> initializer);

    void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer);

    void commands(String namespace, Consumer<CommandRegistrar> initializer);

    // -----[SERVER NETWORKING]------

    <MSG extends CustomPacketPayload> void registerServerConfigurationPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> codec, ServerNetworking.ConfigurationPayloadHandler<MSG> handler);

    <MSG extends CustomPacketPayload> void registerServerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> codec, ServerNetworking.PlayPayloadHandler<MSG> handler);

    boolean canSendServerConfigurationPayload(ServerConfigurationPacketListenerImpl networkHandler, CustomPacketPayload.Type<?> type);

    boolean canSendServerPlayPayload(ServerPlayer player, CustomPacketPayload.Type<?> type);

    // -----[CAPABILITIES]-----

    BlockCapabilities getBlockCapabilities();

    EntityCapabilities getEntityCapabilities();

    ItemCapabilities getItemCapabilities();

    PermissionAPI getPermissionAPI();

    // -----[PLATFORM]-----

    boolean isModLoaded(String modId);

    boolean isProduction();

    boolean isFabric();

    boolean isNeoforge();

    EnvironmentSide getEnvironmentSide();

    @Nullable
    MinecraftServer getCurrentSever();
}
