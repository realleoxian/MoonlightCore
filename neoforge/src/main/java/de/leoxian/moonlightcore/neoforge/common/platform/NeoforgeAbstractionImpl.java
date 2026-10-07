package de.leoxian.moonlightcore.neoforge.common.platform;

import com.google.common.eventbus.EventBus;
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
import de.leoxian.moonlightcore.neoforge.common.attachment.NeoAttachmentHolderWrapper;
import de.leoxian.moonlightcore.neoforge.common.attachment.NeoDataAttachmentTypeBuilderImpl;
import de.leoxian.moonlightcore.neoforge.common.capability.NeoforgeBlockCapabilityCache;
import de.leoxian.moonlightcore.neoforge.common.capability.NeoforgeCapabilityRegistry;
import de.leoxian.moonlightcore.neoforge.common.command.NeoforgeArgumentTypeRegistrar;
import de.leoxian.moonlightcore.neoforge.common.command.NeoforgeCommandRegistrarContext;
import de.leoxian.moonlightcore.neoforge.common.entity.NeoforgeEntityAttributeRegistrar;
import de.leoxian.moonlightcore.neoforge.common.fluid.NeoforgeFluidRegistrar;
import de.leoxian.moonlightcore.neoforge.common.hooks.EventBusesHooks;
import de.leoxian.moonlightcore.neoforge.common.network.NeoforgeNetworkHandler;
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
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
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
	public void fluids(String namespace, Consumer<FluidRegistrar> initializer) {
		initializer.accept(new NeoforgeFluidRegistrar(namespace));
	}

	@Override
	public void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
		EventBusesHooks.atListener(namespace, NeoforgeEntityAttributeRegistrar.class, initializer);
	}

	@Override
	public void commands(Consumer<CommandRegistrarContext> initializer) {
		this.pendingCommandRegistrations.add(initializer);
	}

	@Override
	public void argumentTypes(Consumer<ArgumentTypeRegistrar> initializer) {
		initializer.accept(NeoforgeArgumentTypeRegistrar.INSTANCE);
	}

	@Override
	public void serverReloadListeners(Consumer<ResourceReloadListenerRegistrar> initializer) {
		NeoForge.EVENT_BUS.addListener((AddServerReloadListenersEvent event) -> {
			initializer.accept(new NeoforgeResourceReloadListenerRegistrar(event));
		});
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
	public void datapackRegistries(String namespace, Consumer<DataPackRegistryRegistrar> initializer) {
		EventBusesHooks.atListener(namespace, NeoforgeDataPackRegistryRegistrar.class, initializer);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromLevel(Level level) {
		return new NeoAttachmentHolderWrapper(level);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromChunk(ChunkAccess chunkAccess) {
		return new NeoAttachmentHolderWrapper(chunkAccess);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromBlockEntity(BlockEntity blockEntity) {
		return new NeoAttachmentHolderWrapper(blockEntity);
	}

	@Override
	public DataAttachmentHolder getDataAttachmentsFromEntity(Entity entity) {
		return new NeoAttachmentHolderWrapper(entity);
	}

	@Override
	public <T> DataAttachmentType.Builder<T> createAttachmentTypeBuilder(Supplier<T> initializer) {
		return new NeoDataAttachmentTypeBuilderImpl<>(initializer);
	}

	@Override
	public <A, C> ItemCapability<A, C> createItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return EventBusesHooks.getListener(id.getNamespace(), NeoforgeCapabilityRegistry.class).getItemCapability(id, apiClass, contextClass);
	}

	@Override
	public <A, C> BlockCapability<A, C> createBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return EventBusesHooks.getListener(id.getNamespace(), NeoforgeCapabilityRegistry.class).getBlockCapability(id, apiClass, contextClass);
	}

	@Override
	public <A, C> BlockCapabilityCache<A, C> createBlockCapabilityCache(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
		return new NeoforgeBlockCapabilityCache<>(level, blockPos, capability, context);
	}

	@Override
	public <A, C> EntityCapability<A, C> createEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return EventBusesHooks.getListener(id.getNamespace(), NeoforgeCapabilityRegistry.class).getEntityCapability(id, apiClass, contextClass);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerServerboundConfigurationPacketPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec, ServerConfigurationNetworking.Handler<MSG> handler) {
		EventBusesHooks.getListener(type.id().getNamespace(), NeoforgeNetworkHandler.class)
						.serverboundConfiguration(type, streamCodec, handler);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerClientboundConfigurationPayloadPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec) {
		EventBusesHooks.getListener(type.id().getNamespace(), NeoforgeNetworkHandler.class)
				.clientboundConfiguration(type, streamCodec);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerServerboundPlayPacketPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, ServerPlayNetworking.Handler<MSG> handler) {
		EventBusesHooks.getListener(type.id().getNamespace(), NeoforgeNetworkHandler.class)
				.serverboundPlay(type, streamCodec, handler);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerClientboundPlayPayloadPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec) {
		EventBusesHooks.getListener(type.id().getNamespace(), NeoforgeNetworkHandler.class)
				.clientboundPlay(type, streamCodec);
	}

	@Override
	public boolean canSendPlayPayloadToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type) {
		return player.connection.hasChannel(type);
	}

	@Override
	public boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload.Type<?> type) {
		return packetListener.hasChannel(type);
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
