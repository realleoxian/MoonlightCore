package de.realleoxian.moonlightcore.api.internal;

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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public interface XplatAbstraction<T extends ModLoadContext> {
    void initializeMod(String modId, T loadContext, Consumer<ModContainer> initializer);

    void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer);

    void commands(String namespace, CommandRegistrar initializer);

    void argumentType(String namespace, Consumer<EntityAttributeRegistrar> initializer);

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

    LevelResource createLevelResource(String id);

    SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound);

    boolean isModLoaded(String modId);

    boolean isProduction();

    boolean isFabric();

    boolean isNeoforge();

    Path getConfigDirectory();

    Path getGameDirectory();

    EnvironmentSide getEnvironmentSide();

    @Nullable
    MinecraftServer getCurrentSever();
}
