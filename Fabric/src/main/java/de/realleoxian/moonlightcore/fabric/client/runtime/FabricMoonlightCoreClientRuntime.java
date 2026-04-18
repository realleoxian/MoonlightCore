package de.realleoxian.moonlightcore.fabric.client.runtime;

import com.mojang.brigadier.CommandDispatcher;
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
import de.realleoxian.moonlightcore.fabric.runtime.EmptyModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.impl.client.runtime.XplatMoonlightCoreClientRuntime;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricMoonlightCoreClientRuntime extends XplatMoonlightCoreClientRuntime<EmptyModLoadingRuntimeContext> {
    private final List<ClientCommandsRegistrar> clientCommandsRegistrars = new ArrayList<>();

    FabricMoonlightCoreClientRuntime() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            @SuppressWarnings({"unchecked", "rawtypes"})
            var dispatches = (CommandDispatcher<ClientCommandSourceStack>) (CommandDispatcher) dispatcher;
            this.clientCommandsRegistrars.forEach(registrar -> registrar.registerClientCommands(dispatches, buildContext));
        });
    }

    @Override
    public void initializeClientMod(String modId, EmptyModLoadingRuntimeContext context, Runnable initializer) {
        initializer.run();
    }

    @Override
    public void commands(ClientCommandsRegistrar initializer) {
        this.clientCommandsRegistrars.add(initializer);
    }

    @Override
    public void modelLayers(String namespace, Consumer<ModelLayerRegistrar> registrar) {
        registrar.accept((location, layerDefinition) -> {
            EntityModelLayerRegistry.registerModelLayer(location, layerDefinition::get);
            return location;
        });
    }

    @Override
    public void particles(String namespace, Consumer<ParticleProviderRegistrar> registrar) {
        registrar.accept(new ParticleProviderRegistrar() {
            @Override
            public <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory) {
                ParticleFactoryRegistry.getInstance().register(particleType.get(), factory::apply);
            }

            @Override
            public <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> particleType, ParticleProvider<T> provider) {
                ParticleFactoryRegistry.getInstance().register(particleType.get(), provider);
            }
        });
    }

    @Override
    public void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> registrar) {
        registrar.accept(new EntityRendererRegistrar() {
            @Override
            public <E extends Entity> void register(Supplier<EntityType<E>> entityType, EntityRendererProvider<E> provider) {
                EntityRendererRegistry.register(entityType.get(), provider);
            }
        });
    }

    @Override
    public void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> registrar) {
        registrar.accept(new BlockEntityRendererRegistrar() {
            @Override
            public <BE extends BlockEntity> void register(Supplier<BlockEntityType<BE>> blockEntityType, BlockEntityRendererProvider<BE> provider) {
                BlockEntityRenderers.register(blockEntityType.get(), provider);
            }
        });
    }

    @Override
    public void blockColors(String namespace, Consumer<BlockColorRegistrar> registrar) {
        registrar.accept((color, block) -> ColorProviderRegistry.BLOCK.register(color, block.get()));
    }

    @Override
    public void itemColors(String namespace, Consumer<ItemColorRegistrar> registrar) {
        registrar.accept((color, item) -> ColorProviderRegistry.ITEM.register(color, item.get()));
    }

    @Override
    public void chunkRenderLayers(String namespace, Consumer<ChunkRenderLayerRegistrar> registrar) {
        registrar.accept(new ChunkRenderLayerRegistrar() {
            @Override
            public void registerBlock(RenderType renderType, Supplier<Block> block) {
                BlockRenderLayerMap.INSTANCE.putBlock(block.get(), renderType);
            }

            @Override
            public void registerFluid(RenderType renderType, Supplier<Fluid> fluid) {
                BlockRenderLayerMap.INSTANCE.putFluid(fluid.get(), renderType);
            }
        });
    }

    @Override
    public void keyMappings(String namespace, Consumer<KeyMappingRegistrar> registrar) {
        registrar.accept(KeyBindingHelper::registerKeyBinding);
    }

    @Override
    public void registerPreparableReloadListener(ResourceLocation name, PreparableReloadListener listener) {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return name;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });
    }
}
