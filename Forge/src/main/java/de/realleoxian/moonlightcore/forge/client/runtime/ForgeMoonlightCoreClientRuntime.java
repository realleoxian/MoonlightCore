package de.realleoxian.moonlightcore.forge.client.runtime;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.client.command.ClientCommandSourceStack;
import de.realleoxian.moonlightcore.api.client.command.ClientCommandsRegistrar;
import de.realleoxian.moonlightcore.api.client.keymapping.KeyMappingRegistrar;
import de.realleoxian.moonlightcore.api.client.model.ModelLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.particle.ParticleProviderRegistrar;
import de.realleoxian.moonlightcore.api.client.render.BlockEntityRendererRegistrar;
import de.realleoxian.moonlightcore.api.client.render.ChunkRenderLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.render.EntityRendererRegistrar;
import de.realleoxian.moonlightcore.api.client.render.color.BlockColorRegistrar;
import de.realleoxian.moonlightcore.api.client.render.color.ItemColorRegistrar;
import de.realleoxian.moonlightcore.forge.client.keymapping.ForgeKeyMappingRegistrar;
import de.realleoxian.moonlightcore.forge.client.model.ForgeModelLayerRegistrar;
import de.realleoxian.moonlightcore.forge.client.particle.ForgeParticleProviderRegistrar;
import de.realleoxian.moonlightcore.forge.client.render.ForgeBlockEntityRendererRegistrar;
import de.realleoxian.moonlightcore.forge.client.render.ForgeChunkRenderLayerRegistrar;
import de.realleoxian.moonlightcore.forge.client.render.ForgeEntityRendererRegistrar;
import de.realleoxian.moonlightcore.forge.client.render.color.ForgeBlockColorRegistrar;
import de.realleoxian.moonlightcore.forge.client.render.color.ForgeItemColorRegistrar;
import de.realleoxian.moonlightcore.forge.platform.ModEventBusHandler;
import de.realleoxian.moonlightcore.forge.runtime.ForgeModLoadingContext;
import de.realleoxian.moonlightcore.impl.client.runtime.XplatMoonlightCoreClientRuntime;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ForgeMoonlightCoreClientRuntime extends XplatMoonlightCoreClientRuntime<ForgeModLoadingContext> {
    private final List<Pair<ResourceLocation, PreparableReloadListener>> clientReloadListeners = new ArrayList<>();
    private final List<ClientCommandsRegistrar> clientCommandsRegistrars = new ArrayList<>();

    ForgeMoonlightCoreClientRuntime() {
        MinecraftForge.EVENT_BUS.addListener((RegisterClientReloadListenersEvent event) -> this.clientReloadListeners.stream().map(Pair::getSecond).forEach(event::registerReloadListener));

        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) -> {
            @SuppressWarnings({"unchecked", "rawtypes"})
            CommandDispatcher<ClientCommandSourceStack> dispatcher = (CommandDispatcher<ClientCommandSourceStack>) (CommandDispatcher) event.getDispatcher();
            CommandBuildContext buildContext = event.getBuildContext();

            this.clientCommandsRegistrars.forEach(registrar -> registrar.registerClientCommands(dispatcher,buildContext));
        });
    }

    @Override
    public void initializeClientMod(String modId, ForgeModLoadingContext context, Runnable initializer) {
        ModEventBusHandler.register(modId, context.eventBus());
        initializer.run();
    }

    @Override
    public void commands(String namespace, ClientCommandsRegistrar initializer) {
        this.clientCommandsRegistrars.add(initializer);
    }

    @Override
    public void modelLayers(String namespace, Consumer<ModelLayerRegistrar> registrar) {
        ForgeModelLayerRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeModelLayerRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void particles(String namespace, Consumer<ParticleProviderRegistrar> registrar) {
        ForgeParticleProviderRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeParticleProviderRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> registrar) {
        ForgeEntityRendererRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeEntityRendererRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> registrar) {
        ForgeBlockEntityRendererRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeBlockEntityRendererRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void blockColors(String namespace, Consumer<BlockColorRegistrar> registrar) {
        ForgeBlockColorRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeBlockColorRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void itemColors(String namespace, Consumer<ItemColorRegistrar> registrar) {
        ForgeItemColorRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeItemColorRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void chunkRenderLayers(String namespace, Consumer<ChunkRenderLayerRegistrar> registrar) {
        ForgeChunkRenderLayerRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeChunkRenderLayerRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void keyMappings(String namespace, Consumer<KeyMappingRegistrar> registrar) {
        ForgeKeyMappingRegistrar forgeRegistrar = ModEventBusHandler.getRegistration(namespace, ForgeKeyMappingRegistrar.class);
        registrar.accept(forgeRegistrar);
    }

    @Override
    public void registerPreparableReloadListener(ResourceLocation name, PreparableReloadListener listener) {
        this.clientReloadListeners.add(Pair.of(name, listener));
    }
}
