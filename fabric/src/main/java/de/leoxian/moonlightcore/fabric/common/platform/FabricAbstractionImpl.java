package de.leoxian.moonlightcore.fabric.common.platform;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.Codec;
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
import de.leoxian.moonlightcore.common.util.ModProxy;
import de.leoxian.moonlightcore.fabric.common.capability.FabricBlockCapability;
import de.leoxian.moonlightcore.fabric.common.capability.FabricBlockCapabilityCache;
import de.leoxian.moonlightcore.fabric.common.capability.FabricEntityCapability;
import de.leoxian.moonlightcore.fabric.common.capability.FabricItemCapability;
import de.leoxian.moonlightcore.fabric.common.network.FabricServerConfigurationNetworkingContext;
import de.leoxian.moonlightcore.fabric.common.network.FabricServerPlayNetworkingContext;
import de.leoxian.moonlightcore.fabric.common.registry.FabricRegistryBuilderImpl;
import de.leoxian.moonlightcore.fabric.common.resource.FabricModResources;
import de.leoxian.moonlightcore.internal.common.internal.XplatPermissionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.SoundType;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricAbstractionImpl implements XplatAbstraction {
    private final AtomicReference<@Nullable MinecraftServer> currentServer = new AtomicReference<>();

    private final Supplier<PermissionsHelper> permissionsHelper = new ModProxy<>(PermissionsHelper.class, XplatPermissionHelper::new)
            .put("fabric-permission-api-v1", "de.leoxian.moonlightcore.fabric.common.server.permission.FabricPermissionsHelperV1")
            .put("fabric-permissions-api-v0", "de.leoxian.moonlightcore.fabric.common.server.permission.FabricPermissionsHelperV0");

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
        initializer.accept(new EntityAttributeRegistrar() {
            @Override
            public <E extends LivingEntity> void register(Supplier<EntityType<E>> entityType, AttributeSupplier attributes) {
                FabricDefaultAttributeRegistry.register(entityType.get(), attributes);
            }
        });
    }

    @Override
    public void commands(Consumer<CommandRegistrarContext> initializer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            initializer.accept(new CommandRegistrarContext() {
                @Override
                public CommandDispatcher<CommandSourceStack> dispatcher() {
                    return dispatcher;
                }

                @Override
                public Commands.CommandSelection selection() {
                    return selection;
                }

                @Override
                public CommandBuildContext buildContext() {
                    return buildContext;
                }
            });
        });
    }

    @Override
    public void argumentTypes(String namespace, Consumer<ArgumentTypeRegistrar> initializer) {
        initializer.accept(new ArgumentTypeRegistrar() {
            @Override
            public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(Identifier id, Class<A> argumentType, ArgumentTypeInfo<A, T> info) {
                ArgumentTypeRegistry.registerArgumentType(id, argumentType, info);
            }

            @Override
            public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(String id, Class<A> argumentType, ArgumentTypeInfo<A, T> info) {
                ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(namespace, id), argumentType, info);
            }
        });
    }

    @Override
    public void serverReloadListeners(Consumer<ResourceReloadListenerRegistrar> initializer) {
        initializer.accept(new ResourceReloadListenerRegistrar() {
            @Override
            public void register(Identifier id, Function<HolderLookup.Provider, PreparableReloadListener> listener) {
                DataResourceLoader.get().registerReloadListener(id, listener);
            }

            @Override
            public void addDependency(Identifier first, Identifier second) {
                DataResourceLoader.get().addListenerOrdering(first, second);
            }
        });
    }

    @Override
    public SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return new SoundType(volume, pitch, breakSound.get(), stepSound.get(), placeSound.get(), hitSound.get(), fallSound.get());
    }

    @Override
    public <T> RegistryBuilder<T> registryBuilder(ResourceKey<Registry<T>> registryKey) {
        return new FabricRegistryBuilderImpl<>(registryKey);
    }

    @Override
    public <R, T extends R> DeferredHolder<R, T> register(Registry<R> registry, Identifier id, Supplier<T> value) {
        ResourceKey<R> key = ResourceKey.create(registry.key(), id);
        Registry.register(registry, id, value.get());
        return DeferredHolder.create(key);
    }

    @Override
    public void datapackRegistries(String namespace, Consumer<DataPackRegistryRegistrar> initializer) {
        initializer.accept(new DataPackRegistryRegistrar() {
            @Override
            public <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
                if (networkCodec == null) {
                    DynamicRegistries.register(registryKey, codec);
                } else {
                    DynamicRegistries.registerSynced(registryKey, codec, networkCodec, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
                }
            }
        });
    }

    @Override
    public <A, C> ItemCapability<A, C> getItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return FabricItemCapability.get(id, apiClass, contextClass);
    }

    @Override
    public <A, C> BlockCapability<A, C> getBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return FabricBlockCapability.get(id, apiClass, contextClass);
    }

    @Override
    public <A, C> BlockCapabilityCache<A, C> getBlockCapabilityCache(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
        return new FabricBlockCapabilityCache<>(capability, level, blockPos, context);
    }

    @Override
    public <A, C> EntityCapability<A, C> getEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return FabricEntityCapability.get(id, apiClass, contextClass);
    }

    @Override
    public <T extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.Handler<T> handler) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            handler.handle(payload, new FabricServerPlayNetworkingContext(context));
        });
    }

    @Override
    public boolean canSendPlayPayloadToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player, type);
    }

    @Override
    public <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ServerConfigurationNetworking.Handler<T> handler) {
        PayloadTypeRegistry.serverboundConfiguration().register(type, codec);
        net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> {
            handler.handle(payload, new FabricServerConfigurationNetworkingContext(context));
        });
    }

    @Override
    public boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload.Type<?> type) {
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.canSend(packetListener, type);
    }

    @Override
    public void addConfigurationTask(String modId, ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask task) {
        packetListener.addTask(task);
    }

    @Override
    public void completeCurrentConfigurationTask(ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask.Type type) {
        packetListener.completeTask(type);
    }

    @Override
    public @Nullable ModResources getModResources(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(FabricModResources::new).orElse(null);
    }

    @Override
    public PermissionsHelper getPermissionHelper() {
        return this.permissionsHelper.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public MinecraftServer getCurrentServer() {
        return currentServer.get();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public EnvironmentSide getEnvironmentSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ?
                EnvironmentSide.CLIENT : EnvironmentSide.SERVER;
    }

    @Override
    public boolean isDevelopmentWorkspace() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isNeoforge() {
        return false;
    }

    @Override
    public boolean isFabric() {
        return true;
    }

    @Override
    public void initialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(currentServer::set);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServer.set(null));
    }
}
