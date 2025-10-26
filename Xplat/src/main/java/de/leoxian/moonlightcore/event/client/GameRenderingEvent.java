package de.leoxian.moonlightcore.event.client;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface GameRenderingEvent {
//    Event<BlockColorRegistration> BLOCK_COLOR_REGISTRATION = EventFactory.createLoop(BlockColorRegistration.class);
//    Event<BlockRendererRegistration> BLOCK_RENDERER_REGISTRATION = EventFactory.createLoop(BlockRendererRegistration.class);;
//    Event<EntityRendererRegistration> ENTITY_RENDERER_REGISTRATION = EventFactory.createLoop(EntityRendererRegistration.class);;
//    Event<BlockEntityRendererRegistration> BLOCK_ENTITY_RENDERER_REGISTRATION = EventFactory.createLoop(BlockEntityRendererRegistration.class);;
//    Event<ModelLayerRegistration> MODEL_LAYER_REGISTRATION = EventFactory.createLoop(ModelLayerRegistration.class);;
//    Event<ParticleFactoryRegistration> PARTICLE_FACTORY_REGISTRATION = EventFactory.createLoop(ParticleFactoryRegistration.class);;
//
//    @FunctionalInterface
//    interface BlockColorRegistration {
//        void bootstrap(Output output);
//
//        interface Output {
//            void register(ItemColor color, ItemLike... items);
//
//            void register(BlockColor color, Block... blocks);
//        }
//    }
//
//    @FunctionalInterface
//    interface BlockRendererRegistration {
//        void bootstrap(Output output);
//
//        interface Output {
//            void register(RenderType renderType, Block... blocks);
//
//            void register(RenderType renderType, Fluid... fluids);
//        }
//    }
//
//    @FunctionalInterface
//    interface EntityRendererRegistration {
//        void bootstrap(Output output);
//
//        interface Output {
//            <E extends Entity> void register(EntityType<? extends E> entityType, EntityRendererProvider<E> renderer);
//        }
//    }
//
//    @FunctionalInterface
//    interface BlockEntityRendererRegistration {
//        void bootstrap(Output output);
//
//        interface Output {
//            <BE extends BlockEntity> void register(BlockEntityType<? extends BE> blockEntityType, BlockEntityRendererProvider<BE> renderer);
//        }
//    }
//
//    @FunctionalInterface
//    interface ModelLayerRegistration {
//        void bootstrap(Output output);
//
//        interface Output {
//            void register(ModelLayerLocation layer, Supplier<LayerDefinition> definition);
//        }
//    }
//
//    @FunctionalInterface
//    interface ParticleFactoryRegistration {
//        void bootstrap(Output output);
//
//        interface Output {
//            <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, ParticleProvider<T> provider);
//        }
//    }

    interface ParticleFactoryRegistration {

        interface Output {

        }
    }

}
