package de.leoxian.moonlightcore.fabric.common.platform;

import de.leoxian.moonlightcore.common.EnvironmentSide;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentHolder;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.block.BlockCapabilityCache;
import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import de.leoxian.moonlightcore.common.capability.item.ItemCapability;
import de.leoxian.moonlightcore.common.command.ArgumentTypeRegistrar;
import de.leoxian.moonlightcore.common.command.CommandRegistrarContext;
import de.leoxian.moonlightcore.common.entity.EntityAttributeRegistrar;
import de.leoxian.moonlightcore.common.fluid.FluidRegistrar;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.common.pack.DataPackRegistryRegistrar;
import de.leoxian.moonlightcore.common.pack.ResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import de.leoxian.moonlightcore.common.resource.ModResources;
import de.leoxian.moonlightcore.common.server.permission.PermissionsHelper;
import de.leoxian.moonlightcore.common.util.ModProxy;
import de.leoxian.moonlightcore.fabric.common.attachment.FabricDataAttachmentHolder;
import de.leoxian.moonlightcore.fabric.common.attachment.FabricDataAttachmentTypeBuilder;
import de.leoxian.moonlightcore.fabric.common.capability.FabricBlockCapability;
import de.leoxian.moonlightcore.fabric.common.capability.FabricBlockCapabilityCache;
import de.leoxian.moonlightcore.fabric.common.capability.FabricEntityCapability;
import de.leoxian.moonlightcore.fabric.common.capability.FabricItemCapability;
import de.leoxian.moonlightcore.fabric.common.command.FabricArgumentTypeRegistrar;
import de.leoxian.moonlightcore.fabric.common.command.FabricCommandRegistrarContext;
import de.leoxian.moonlightcore.fabric.common.entity.FabricEntityAttributeRegistrar;
import de.leoxian.moonlightcore.fabric.common.event.CommonEventHooks;
import de.leoxian.moonlightcore.fabric.common.fluid.FabricFluidRegistrar;
import de.leoxian.moonlightcore.fabric.common.network.FabricServerConfigurationNetworkingContext;
import de.leoxian.moonlightcore.fabric.common.network.FabricServerPlayNetworkingContext;
import de.leoxian.moonlightcore.fabric.common.pack.FabricDataPackRegistryRegistrar;
import de.leoxian.moonlightcore.fabric.common.pack.FabricResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.fabric.common.registry.FabricRegistryBuilderImpl;
import de.leoxian.moonlightcore.fabric.common.resource.FabricModResources;
import de.leoxian.moonlightcore.internal.common.internal.XplatPermissionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricAbstractionImpl implements XplatAbstraction {
	private final AtomicReference<@Nullable MinecraftServer> currentServer = new AtomicReference<>();

	private final Supplier<PermissionsHelper> permissionsHelper = new ModProxy<>(PermissionsHelper.class, XplatPermissionHelper::new)
			.put("fabric-permission-api-v1", "de.leoxian.moonlightcore.fabric.common.server.permission.FabricPermissionsHelperV1")
			.put("fabric-permissions-api-v0", "de.leoxian.moonlightcore.fabric.common.server.permission.FabricPermissionsHelperV0");

	@Override
	public void fluids(String namespace, Consumer<FluidRegistrar> initializer) {
		initializer.accept(new FabricFluidRegistrar(namespace));
	}

	@Override
	public void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
		initializer.accept(FabricEntityAttributeRegistrar.INSTANCE);
	}

	@Override
	public void commands(Consumer<CommandRegistrarContext> initializer) {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
			initializer.accept(new FabricCommandRegistrarContext(dispatcher, selection, buildContext));
		});
	}

	@Override
	public void argumentTypes(Consumer<ArgumentTypeRegistrar> initializer) {
		initializer.accept(FabricArgumentTypeRegistrar.INSTANCE);
	}

	@Override
	public void serverReloadListeners(Consumer<ResourceReloadListenerRegistrar> initializer) {
		initializer.accept(FabricResourceReloadListenerRegistrar.INSTANCES);
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
	public void datapackRegistries(String namespace, Consumer<DataPackRegistryRegistrar> initializer) {
		initializer.accept(FabricDataPackRegistryRegistrar.INSTANCE);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromLevel(Level level) {
		return new FabricDataAttachmentHolder(level);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromChunk(ChunkAccess chunkAccess) {
		return new FabricDataAttachmentHolder(chunkAccess);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromBlockEntity(BlockEntity blockEntity) {
		return new FabricDataAttachmentHolder(blockEntity);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromEntity(Entity entity) {
		return new FabricDataAttachmentHolder(entity);
	}

	@Override
	public <T> DataAttachmentType.Builder<T> createAttachmentTypeBuilder(Supplier<T> initializer) {
		return new FabricDataAttachmentTypeBuilder<>(initializer);
	}

	@Override
	public <A, C> ItemCapability<A, C> createItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return FabricItemCapability.get(id, apiClass, contextClass);
	}

	@Override
	public <A, C> BlockCapability<A, C> createBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return FabricBlockCapability.get(id, apiClass, contextClass);
	}

	@Override
	public <A, C> BlockCapabilityCache<A, C> createBlockCapabilityCache(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
		return new FabricBlockCapabilityCache<>(capability, level, blockPos, context);
	}

	@Override
	public <A, C> EntityCapability<A, C> createEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return FabricEntityCapability.get(id, apiClass, contextClass);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerServerboundConfigurationPayloadHandler(CustomPacketPayload.Type<MSG> type, ServerConfigurationNetworking.Handler<MSG> handler) {
		net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> {
			handler.handle(payload, new FabricServerConfigurationNetworkingContext(context));
		});
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerServerboundConfigurationPacketPayloadType(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec) {
		PayloadTypeRegistry.serverboundConfiguration().register(type, streamCodec);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerServerboundPlayPayloadHandler(CustomPacketPayload.Type<MSG> type, ServerPlayNetworking.Handler<MSG> handler) {
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
			handler.handle(payload, new FabricServerPlayNetworkingContext(context));
		});
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerServerboundPlayPacketPayloadType(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec) {
		PayloadTypeRegistry.serverboundPlay().register(type, streamCodec);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerClientboundConfigurationPayloadPacketType(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec) {
		PayloadTypeRegistry.clientboundConfiguration().register(type, streamCodec);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerClientboundPlayPayloadPacketType(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec) {
		PayloadTypeRegistry.clientboundPlay().register(type, streamCodec);
	}

	@Override
	public boolean canSendPlayPayloadToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type) {
		return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player, type);
	}

	@Override
	public boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload.Type<?> type) {
		return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.canSend(packetListener, type);
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
		CommonEventHooks.bindFabricApiEvents();

		ServerLifecycleEvents.SERVER_STARTING.register(currentServer::set);
		ServerLifecycleEvents.SERVER_STOPPED.register(_ -> currentServer.set(null));
	}
}
