package de.leoxian.moonlightcore.event.client;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface RenderingEvents {
    /**
     * @see BlockColorRegistration#onBlockColorRegistration(BiConsumer)
     */
    Event<BlockColorRegistration> BLOCK_COLOR_REGISTRATION = EventFactory.create(BlockColorRegistration.class);
    /**
     * @see ItemColorRegistration#onItemColorRegistration(BiConsumer)
     */
    Event<ItemColorRegistration> ITEM_COLOR_REGISTRATION = EventFactory.create(ItemColorRegistration.class);
    /**
     * @see BlockRenderTypeRegistration#onBlockRendererRegistration(BlockRenderTypeRegistration.Output)
     */
    Event<BlockRenderTypeRegistration> BLOCK_RENDER_TYPE_REGISTRATION = EventFactory.create(BlockRenderTypeRegistration.class);
    /**
     * @see RendererRegistration#onRendererRegistration(RendererRegistration.Output)
     */
    Event<RendererRegistration> RENDERER_REGISTRATION = EventFactory.create(RendererRegistration.class);
    /**
     * @see ModelLayerRegistration#onModelLayerRegistration(BiConsumer)
     */
    Event<ModelLayerRegistration> MODEL_LAYER_REGISTRATION = EventFactory.create(ModelLayerRegistration.class);
    /**
     * @see ParticleProviderRegistration#onParticleProvidersRegistration(ParticleProviderRegistration.Output)
     */
    Event<ParticleProviderRegistration> PARTICLE_PROVIDER_REGISTRATION = EventFactory.create(ParticleProviderRegistration.class);

    interface BlockColorRegistration {
        /**
         * Invoked to allow mods to register {@link BlockColor}s
         * @param output The output of the event, used to register the block colors
         */
        void onBlockColorRegistration(BiConsumer<BlockColor, Block> output);
    }

    interface ItemColorRegistration {
        /**
         * Invoked to allow mods to register {@link ItemColor}s
         * @param output The output of the event, used to register the item colors
         */
        void onItemColorRegistration(BiConsumer<ItemColor, ItemLike> output);
    }

    interface BlockRenderTypeRegistration {
        /**
         * Invoked for block/fluid {@link RenderType}s registration
         * @param output The output of the event, that is used to register the {@link RenderType}s
         */
        void onBlockRendererRegistration(Output output);

        interface Output {
            /**
             * Registers a {@link RenderType} to the given {@link Block} instances
             * @param renderType The {@link RenderType} registered
             * @param blocks The {@link Block} instances that have the registered {@link RenderType}
             */
            void register(RenderType renderType, Block... blocks);

            /**
             * Registers a {@link RenderType} to the given {@link Fluid} instances
             * @param renderType The {@link RenderType} registered
             * @param fluids The {@link Fluid} instances that have the registered {@link RenderType}
             */
            void register(RenderType renderType, Fluid... fluids);
        }
    }

    interface RendererRegistration  {
        /**
         * Invoked for {@link Entity}/{@link BlockEntity} renderers registration
         * @param output The output of the event, that is used for the renderers registration
         */
        void onRendererRegistration(RendererRegistration.Output output);

        interface Output {
            /**
             * Registers a {@link EntityRendererProvider} for the given {@link EntityType}
             * @param entityType The {@link EntityType} to register a renderer
             * @param renderer The {@link EntityRendererProvider}
             * @param <T> The {@link Entity} used by the {@link EntityRendererProvider}
             */
            <T extends Entity> void registerEntity(EntityType<T> entityType, EntityRendererProvider<T> renderer);

            /**
             * Registers a {@link BlockEntityRendererProvider} for the given {@link BlockEntityType}
             * @param blockEntityType The {@link BlockEntityType} to register a renderer for
             * @param renderer The {@link BlockEntityRendererProvider}
             * @param <T> The {@link BlockEntity} used by the {@link BlockEntityRendererProvider}
             */
            <T extends BlockEntity> void registerBlockEntity(BlockEntityType<T> blockEntityType, BlockEntityRendererProvider<T> renderer);
        }
    }

    interface ModelLayerRegistration {
        /**
         * Invoked for {@link LayerDefinition}s registration
         * @param output The output of the event, used to register the {@link LayerDefinition}s
         */
        void onModelLayerRegistration(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> output);
    }

    interface ParticleProviderRegistration {
        /**
         * Invoked for {@link ParticleProvider}s registration.
         * @param output The output of the event, that is used to register the providers
         */
        void onParticleProvidersRegistration(ParticleProviderRegistration.Output output);

        interface Output {
            /**
             * Registers a {@link ParticleProvider} for a json-based {@link ParticleType}. Particle jsons define a list of texture sprites which the particle can use to render itself.
             * @param type The {@link ParticleType} to register a {@link ParticleProvider} for
             * @param registration The {@link net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration} function responsible for providing that {@link ParticleType}'s particles
             * @param <T> The {@link ParticleOptions} used by the {@link ParticleType} and {@link net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration} function
             */
            <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration);

            /**
             * Registers a {@link ParticleProvider} for a json-based {@link ParticleType} with a single texture; the resulting {@link net.minecraft.client.particle.TextureSheetParticle}s will use that texture when created
             * @param type The {@link ParticleType} to register a {@link ParticleProvider} for
             * @param sprite The sprite function responsible for providing that {@link ParticleType}'s particles
             * @param <T> The {@link ParticleOptions} used by the {@link ParticleType} and {@link net.minecraft.client.particle.ParticleProvider.Sprite} function
             */
            <T extends ParticleOptions> void registerSprite(ParticleType<T> type, ParticleProvider.Sprite<T> sprite);

            /**
             * Registers a {@link ParticleProvider} for non json-based {@link ParticleType}. These particles do not receive a
             * list of texture sprites to use for rendering themselves
             * @param type The {@link ParticleType} to register a {@link ParticleProvider} for
             * @param provider The {@link ParticleProvider} function responsible for providing that {@link ParticleType}'s particles
             * @param <T> The {@link ParticleOptions} used by the {@link ParticleType} and {@link ParticleProvider}
             */
            <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider);
        }
    }
}
