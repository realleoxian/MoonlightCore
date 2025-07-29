package de.leoxian.moonlightcore.api.event.client;

import de.leoxian.moonlightcore.api.event.Event;
import de.leoxian.moonlightcore.api.util.SidedEnvironment;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

@SidedEnvironment(SidedEnvironment.Environment.CLIENT)
public interface GameRenderingEvents {
    /**
     * An event fired when registering custom block/item colors
     */
    Event<BlockColorRegistration> BLOCK_COLOR_REGISTRATION = Event.create();
    /**
     * An event fired when registering custom block/fluid renderers
     */
    Event<BlockRendererRegistration> BLOCK_RENDERER_REGISTRATION = Event.create();
    /**
     * An event fired when registering custom entity renderers
     */
    Event<EntityRendererRegistration> ENTITY_RENDERER_REGISTRATION = Event.create();
    /**
     * An event fired when registering custom block entity renderers
     */
    Event<BlockEntityRendererRegistration> BLOCK_ENTITY_RENDERER_REGISTRATION = Event.create();
    /**
     * An event fired when registering custom model layers
     */
    Event<ModelLayerRegistration> MODEL_LAYER_REGISTRATION = Event.create();
    /**
     * An event fired when registering custom particle factories
     */
    Event<ParticleFactoryRegistration> PARTICLE_FACTORY_REGISTRATION = Event.create();

    @FunctionalInterface
    interface BlockColorRegistration {
        void bootstrap(Output output);

        interface Output {
            void register(ItemColor color, ItemLike... items);

            void register(BlockColor color, Block... blocks);
        }
    }

    @FunctionalInterface
    interface BlockRendererRegistration {
        void bootstrap(Output output);

        interface Output {
            void register(RenderType renderType, Block... blocks);

            void register(RenderType renderType, Fluid... fluids);
        }
    }

    @FunctionalInterface
    interface EntityRendererRegistration {
        void bootstrap(Output output);

        interface Output {
            <E extends Entity> void register(EntityType<? extends E> entityType, EntityRendererProvider<E> renderer);
        }
    }

    @FunctionalInterface
    interface BlockEntityRendererRegistration {
        void bootstrap(Output output);

        interface Output {
            <BE extends BlockEntity> void register(BlockEntityType<? extends BE> blockEntityType, BlockEntityRendererProvider<BE> renderer);
        }
    }

    @FunctionalInterface
    interface ModelLayerRegistration {
        void bootstrap(Output output);

        interface Output {
            void register(ModelLayerLocation layer, Supplier<LayerDefinition> definition);
        }
    }

    @FunctionalInterface
    interface ParticleFactoryRegistration {
        void bootstrap(Output output);

        interface Output {
            <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, ParticleProvider<T> provider);
        }
    }
}
