package de.leoxian.moonlightcore.common.platform;

import de.leoxian.moonlightcore.common.EnvironmentSide;
import de.leoxian.moonlightcore.common.ModEntrypoint;
import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.block.BlockCapabilityCache;
import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import de.leoxian.moonlightcore.common.capability.item.ItemCapability;
import de.leoxian.moonlightcore.common.command.ArgumentTypeRegistrar;
import de.leoxian.moonlightcore.common.command.CommandRegistrarContext;
import de.leoxian.moonlightcore.common.entity.EntityAttributeRegistrar;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.common.pack.DataPackRegistryRegistrar;
import de.leoxian.moonlightcore.common.pack.ResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.common.registry.DeferredHolder;
import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import de.leoxian.moonlightcore.common.resource.ModResources;
import de.leoxian.moonlightcore.common.server.permission.PermissionsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface XplatAbstraction {
    XplatAbstraction INSTANCE = ServiceLoader.load(XplatAbstractionFactory.class).findFirst().orElseThrow().create();

    void initializeMod(final String modId, final ModEntrypoint entrypoint);

    // |-----| Registrars |-----|
    void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer);

    void commands(Consumer<CommandRegistrarContext> initializer);

    void argumentTypes(String namespace, Consumer<ArgumentTypeRegistrar> initializer);

    void serverReloadListeners(Consumer<ResourceReloadListenerRegistrar> initializer);

    SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound);

    <T> RegistryBuilder<T> registryBuilder(ResourceKey<Registry<T>> registryKey);

    <R, T extends R> DeferredHolder<R, T> register(Registry<R> registry, Identifier id, Supplier<T> value);

    void datapackRegistries(String namespace, Consumer<DataPackRegistryRegistrar> initializer);

    // |-----| Capabilities |-----|
    <A, C extends @Nullable Object> ItemCapability<A, C> getItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass);

    <A, C extends @Nullable Object> BlockCapability<A, C> getBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass);

    <A, C extends @Nullable Object> BlockCapabilityCache<A, C> getBlockCapabilityCache(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context);

    <A, C extends @Nullable Object> EntityCapability<A, C> getEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass);

    // |-----| S2C Play Networking |-----|
    <T extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.Handler<T> handler);

    boolean canSendPlayPayloadToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type);

    // |-----| S2C Configuration Networking |-----|
    <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ServerConfigurationNetworking.Handler<T> handler);

    boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload.Type<?> type);

    void addConfigurationTask(String modId, ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask task);

    void completeCurrentConfigurationTask(ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask.Type type);

    // |-----| Platform |-----|
    @Nullable
    ModResources getModResources(String modId);

    PermissionsHelper getPermissionHelper();

    boolean isModLoaded(String modId);

    MinecraftServer getCurrentServer();

    Path getConfigDirectory();

    Path getGameDirectory();

    EnvironmentSide getEnvironmentSide();

    boolean isDevelopmentWorkspace();

    boolean isNeoforge();

    boolean isFabric();

    void initialize();
}
