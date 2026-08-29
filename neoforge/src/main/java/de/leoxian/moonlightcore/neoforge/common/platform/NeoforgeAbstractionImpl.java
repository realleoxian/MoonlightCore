package de.leoxian.moonlightcore.neoforge.common.platform;

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
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.common.registry.DeferredHolder;
import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import de.leoxian.moonlightcore.common.resource.ModResources;
import de.leoxian.moonlightcore.common.server.permission.PermissionsHelper;
import de.leoxian.moonlightcore.neoforge.common.ModDeferredRegisters;
import de.leoxian.moonlightcore.neoforge.common.ModEventBuses;
import de.leoxian.moonlightcore.neoforge.common.capability.NeoforgeBlockCapabilityCache;
import de.leoxian.moonlightcore.neoforge.common.capability.NeoforgeCapabilityRegistry;
import de.leoxian.moonlightcore.neoforge.common.command.NeoforgeArgumentTypeRegistrar;
import de.leoxian.moonlightcore.neoforge.common.command.NeoforgeCommandRegistrarContext;
import de.leoxian.moonlightcore.neoforge.common.entity.NeoforgeEntityAttributeRegistrar;
import de.leoxian.moonlightcore.neoforge.common.network.NeoforgeServerNetworkHandler;
import de.leoxian.moonlightcore.neoforge.common.pack.NeoforgeDataPackRegistryRegistrar;
import de.leoxian.moonlightcore.neoforge.common.pack.NeoforgeResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.neoforge.common.registry.NeoforgeRegistryBuilder;
import de.leoxian.moonlightcore.neoforge.common.resource.NeoforgeModResources;
import de.leoxian.moonlightcore.neoforge.common.server.permission.NeoforgePermissionsHelper;
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
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModFileInfo;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoforgeAbstractionImpl implements XplatAbstraction {
    private final List<Consumer<CommandRegistrarContext>> pendingCommandRegistrations = new ArrayList<>();

    private final PermissionsHelper permissionsHelper = new NeoforgePermissionsHelper();

    public NeoforgeAbstractionImpl() {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            CommandRegistrarContext context = new NeoforgeCommandRegistrarContext(event);
            pendingCommandRegistrations.forEach(c -> c.accept(context));
            pendingCommandRegistrations.clear();
        });
    }

    @Override
    public void initializeMod(String modId, final ModEntrypoint entrypoint) {
        try {
            entrypoint.initialize();
        } catch (Throwable throwable) {
            throw new RuntimeException("Failed to initialize mod '" + modId + "'", throwable);
        }
    }

    @Override
    public void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
        initializer.accept(ModEventBuses.registerListener(namespace, NeoforgeEntityAttributeRegistrar.class));
    }

    @Override
    public void commands(Consumer<CommandRegistrarContext> initializer) {
        this.pendingCommandRegistrations.add(initializer);
    }

    @Override
    public void argumentTypes(String namespace, Consumer<ArgumentTypeRegistrar> initializer) {
        initializer.accept(new NeoforgeArgumentTypeRegistrar(namespace));
    }

    @Override
    public void serverReloadListeners(Consumer<ResourceReloadListenerRegistrar> initializer) {
        NeoForge.EVENT_BUS.addListener((AddServerReloadListenersEvent event) ->
                initializer.accept(new NeoforgeResourceReloadListenerRegistrar(event)));
    }

    @Override
    public SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return new DeferredSoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    @Override
    public <T> RegistryBuilder<T> registryBuilder(ResourceKey<Registry<T>> registryKey) {
        return new NeoforgeRegistryBuilder<>(registryKey);
    }

    @Override
    public <R, T extends R> DeferredHolder<R, T> register(Registry<R> registry, Identifier id, Supplier<T> value) {
        net.neoforged.neoforge.registries.DeferredHolder<R, T> holder = ModDeferredRegisters.get(registry, id.getNamespace()).register(id.getPath(), value);
        return DeferredHolder.create(holder.getKey());
    }

    @Override
    public void datapackRegistries(String namespace, Consumer<DataPackRegistryRegistrar> initializer) {
        initializer.accept(ModEventBuses.registerListener(namespace, NeoforgeDataPackRegistryRegistrar.class));
    }

    @Override
    public <A, C> ItemCapability<A, C> getItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return ModEventBuses.registerListener(id.getNamespace(), NeoforgeCapabilityRegistry.class)
                .getItemCapability(id, apiClass, contextClass);
    }

    @Override
    public <A, C> BlockCapability<A, C> getBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return ModEventBuses.registerListener(id.getNamespace(), NeoforgeCapabilityRegistry.class)
                .getBlockCapability(id, apiClass, contextClass);
    }

    @Override
    public <A, C> BlockCapabilityCache<A, C> getBlockCapabilityCache(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
        return new NeoforgeBlockCapabilityCache<>(level, blockPos, capability, context);
    }

    @Override
    public <A, C> EntityCapability<A, C> getEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return ModEventBuses.registerListener(id.getNamespace(), NeoforgeCapabilityRegistry.class)
                .getEntityCapability(id, apiClass, contextClass);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.Handler<T> handler) {
        ModEventBuses.registerListener(type.id().getNamespace(), NeoforgeServerNetworkHandler.class)
                .registerPlayPayload(type, codec, handler);
    }

    @Override
    public boolean canSendPlayPayloadToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return player.connection.hasChannel(type);
    }

    @Override
    public <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ServerConfigurationNetworking.Handler<T> handler) {
        ModEventBuses.registerListener(type.id().getNamespace(), NeoforgeServerNetworkHandler.class)
                .registerConfigurationPayload(type, codec, handler);
    }

    @Override
    public boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload.Type<?> type) {
        return packetListener.hasChannel(type);
    }

    @Override
    public void addConfigurationTask(String modId, ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask task) {
        ModEventBuses.registerListener(modId, NeoforgeServerNetworkHandler.class)
                .addTask(task);
    }

    @Override
    public void completeCurrentConfigurationTask(ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask.Type type) {
        packetListener.finishCurrentTask(type);
    }

    @Override
    public @Nullable ModResources getModResources(String modId) {
        IModFileInfo modFile = ModList.get().getModFileById(modId);
        if (modFile == null) {
            return null;
        }
        return new NeoforgeModResources(modFile.getFile().getContents());
    }

    @Override
    public PermissionsHelper getPermissionHelper() {
        return this.permissionsHelper;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public MinecraftServer getCurrentServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGameDirectory() {
        return FMLLoader.getCurrent().getGameDir();
    }

    @Override
    public EnvironmentSide getEnvironmentSide() {
        return FMLEnvironment.getDist().isClient() ? EnvironmentSide.CLIENT : EnvironmentSide.SERVER;
    }

    @Override
    public boolean isDevelopmentWorkspace() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public boolean isNeoforge() {
        return true;
    }

    @Override
    public boolean isFabric() {
        return false;
    }

    @Override
    public void initialize() {

    }
}
