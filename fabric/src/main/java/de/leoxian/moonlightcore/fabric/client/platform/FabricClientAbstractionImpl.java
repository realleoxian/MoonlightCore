package de.leoxian.moonlightcore.fabric.client.platform;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.client.color.BlockColorRegistrar;
import de.leoxian.moonlightcore.client.command.ClientCommandsContext;
import de.leoxian.moonlightcore.client.gui.GuiLayer;
import de.leoxian.moonlightcore.client.gui.GuiLayerRegistrar;
import de.leoxian.moonlightcore.client.keymapping.KeyMappingRegistrar;
import de.leoxian.moonlightcore.client.menu.MenuScreenFactory;
import de.leoxian.moonlightcore.client.menu.MenuScreenRegistrar;
import de.leoxian.moonlightcore.client.model.ModelLayerRegistrar;
import de.leoxian.moonlightcore.client.model.RangeSelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.client.model.SelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.client.pack.ClientResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.client.particle.ParticleProviderRegistrar;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.client.render.BlockEntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import de.leoxian.moonlightcore.common.entrypoint.ClientModInitializer;
import de.leoxian.moonlightcore.fabric.client.gui.FabricGuiLayer;
import de.leoxian.moonlightcore.fabric.client.network.FabricClientConfigurationNetworkingContext;
import de.leoxian.moonlightcore.fabric.client.network.FabricClientPlayNetworkingContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricClientAbstractionImpl implements XplatClientAbstraction {
    @Override
    public void initializeClientMod(String modId, Class<?> initializer) {
        try {
            Constructor<?> constructor = initializer.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            if (instance instanceof ClientModInitializer modInitializer) {
                modInitializer.onInitializedClient();
            }
        }  catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to initialize client mod '" + modId + "': Class " + initializer.getName() + " must have a no-arg constructor!", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize client mod '" + modId + "' with initializer " + initializer.getName(), e);
        }
    }

    @Override
    public void guiLayers(String namespace, Consumer<GuiLayerRegistrar> initializer) {
        initializer.accept(new GuiLayerRegistrar() {
            @Override
            public void registerBelowAll(Identifier id, GuiLayer layer) {
                HudElementRegistry.addLast(id, new FabricGuiLayer(layer));
            }

            @Override
            public void registerBelow(Identifier upper, Identifier id, GuiLayer layer) {
                HudElementRegistry.attachElementBefore(upper, id, new FabricGuiLayer(layer));
            }

            @Override
            public void registerAbove(Identifier below, Identifier id, GuiLayer layer) {
                HudElementRegistry.attachElementAfter(below, id, new FabricGuiLayer(layer));
            }

            @Override
            public void registerAboveAll(Identifier id, GuiLayer layer) {
                HudElementRegistry.addFirst(id, new FabricGuiLayer(layer));
            }

            @Override
            public void replaceLayer(Identifier id, GuiLayer replacement) {
                HudElementRegistry.replaceElement(id, r -> new FabricGuiLayer(replacement));
            }
        });
    }

    @Override
    public void keyMappings(String namespace, Consumer<KeyMappingRegistrar> initializer) {
        initializer.accept(new KeyMappingRegistrar() {
            @Override
            public void register(KeyMapping keyMapping) {
                KeyMappingHelper.registerKeyMapping(keyMapping);
            }

            @Override
            public void registerCategory(KeyMapping.Category category) {
                // no-op
            }
        });
    }

    @Override
    public void modelLayers(String namespace, Consumer<ModelLayerRegistrar> initializer) {
        initializer.accept(new ModelLayerRegistrar() {
            @Override
            public void register(ModelLayerLocation location, Supplier<LayerDefinition> sup) {
                ModelLayerRegistry.registerModelLayer(location, sup::get);
            }
        });
    }

    @Override
    public void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> initializer) {
        initializer.accept(new BlockEntityRendererRegistrar() {
            @Override
            public <T extends BlockEntity, S extends BlockEntityRenderState> void register(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> provider) {
                BlockEntityRenderers.register(blockEntityType.get(), provider);
            }
        });
    }

    @Override
    public void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> initializer) {
        initializer.accept(new EntityRendererRegistrar() {
            @Override
            public <T extends Entity> void register(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> provider) {
                EntityRenderers.register(entityType.get(), provider);
            }
        });
    }

    @Override
    public void particles(String namespace, Consumer<ParticleProviderRegistrar> initializer) {
        initializer.accept(new ParticleProviderRegistrar() {
            @Override
            public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
                ParticleProviderRegistry.getInstance().register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, SpriteParticleProvider<T> registration) {
                ParticleProviderRegistry.getInstance().register(type, registration::create);
            }
        });
    }

    @Override
    public void renderPipelines(String namespace, Consumer<RenderPipelineRegistrar> initializer) {
        initializer.accept(new RenderPipelineRegistrar() {
            @Override
            public void register(RenderPipeline pipeline) {
                RenderPipelines.register(pipeline);
            }
        });
    }

    @Override
    public void blockColor(String namespace, Consumer<BlockColorRegistrar> initializer) {
        initializer.accept(new BlockColorRegistrar() {
            @Override
            public void register(List<BlockTintSource> tintSources, Supplier<Block> blocks) {
                BlockColorRegistry.register(tintSources, blocks.get());
            }
        });
    }

    @Override
    public void menuScreens(String namespace, Consumer<MenuScreenRegistrar> initializer) {
        initializer.accept(new MenuScreenRegistrar() {
            @Override
            public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void register(Supplier<MenuType<T>> menuType, MenuScreenFactory<T, S> factory) {
                MenuScreens.register(menuType.get(), factory::create);
            }
        });
    }

    @Override
    public void resourceReloadListeners(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer) {
        initializer.accept(new ClientResourceReloadListenerRegistrar() {
            @Override
            public void register(Identifier id, PreparableReloadListener listener) {
                ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(id, listener);
            }

            @Override
            public void addDependency(Identifier first, Identifier second) {
                ResourceLoader.get(PackType.CLIENT_RESOURCES).addListenerOrdering(first, second);
            }
        });
    }

    @Override
    public void selectItemModelProperties(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer) {
        initializer.accept(new SelectItemModelPropertyRegistrar() {
            @Override
            public void register(Identifier identifier, SelectItemModelProperty.Type<?, ?> type) {
                SelectItemModelProperties.ID_MAPPER.put(identifier, type);
            }
        });
    }

    @Override
    public void rangeSelectItemModelProperties(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer) {
        initializer.accept(new RangeSelectItemModelPropertyRegistrar() {
            @Override
            public void register(Identifier id, MapCodec<? extends RangeSelectItemModelProperty> source) {
                RangeSelectItemModelProperties.ID_MAPPER.put(id, source);
            }
        });
    }

    @Override
    public void commands(Consumer<ClientCommandsContext> initializer) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            initializer.accept(new ClientCommandsContext() {
                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public CommandDispatcher<SharedSuggestionProvider> dispatcher() {
                    return (CommandDispatcher<SharedSuggestionProvider>) (CommandDispatcher) dispatcher;
                }

                @Override
                public CommandBuildContext buildContext() {
                    return buildContext;
                }
            });
        });
    }

    @Override
    public <MSG extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, ClientPlayNetworking.Handler<MSG> handler) {
        PayloadTypeRegistry.clientboundPlay().register(type, streamCodec);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            handler.handle(payload, new FabricClientPlayNetworkingContext(context));
        });
    }

    @Override
    public boolean canSendPlayPayload(CustomPacketPayload.Type<?> type) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(type);
    }

    @Override
    public <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ClientConfigurationNetworking.Handler<T> handler) {
        PayloadTypeRegistry.clientboundConfiguration().register(type, streamCodec);
        net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> {
           handler.handle(payload, new FabricClientConfigurationNetworkingContext(context));
        });
    }

    @Override
    public boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.canSend(type);
    }
}
