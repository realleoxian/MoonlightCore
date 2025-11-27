package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.event.client.RenderingEvents;
import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullFunction;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.NonnullUnaryOperator;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockBuilder<T extends Block> extends AbstractBuilder<Block, T, BlockBuilder<T>> {

    public static <T extends Block> BlockBuilder<T> builder(DeferredRegistrar<Block> registrar, String name, Function<BlockBehaviour.Properties, T> factory) {
        return new BlockBuilder<>(registrar, name, factory);
    }

    private final Function<BlockBehaviour.Properties, T> factory;

    private NonnullSupplier<BlockBehaviour.Properties> initialProperties = BlockBehaviour.Properties::of;
    private NonnullFunction<BlockBehaviour.Properties, BlockBehaviour.Properties> propertiesCallbacks = NonnullUnaryOperator.identity();

    private final List<NonnullSupplier<Supplier<RenderType>>> renderLayers = new ArrayList<>(1);
    private @Nullable NonnullSupplier<Supplier<BlockColor>> colorHandler = null;

    protected BlockBuilder(DeferredRegistrar<Block> registrar, String name, Function<BlockBehaviour.Properties, T> factory) {
        super(registrar, name);
        this.factory = factory;
    }

    public BlockBuilder<T> initialProperties(NonnullSupplier<BlockBehaviour.Properties> initialProperties) {
        this.initialProperties = Objects.requireNonNull(initialProperties, "Initial properties may not be null");
        return this;
    }

    public BlockBuilder<T> properties(NonnullUnaryOperator<BlockBehaviour.Properties> callback) {
        Objects.requireNonNull(callback, "Properties callback may not be null");
        this.propertiesCallbacks = propertiesCallbacks.andThen(callback);
        return this;
    }

    public BlockBuilder<T> color(NonnullSupplier<Supplier<BlockColor>> colorHandler) {
        Objects.requireNonNull(colorHandler, "Color handler may not be null");
        if(this.colorHandler == null) onRegister(this::setupBlockColor);
        this.colorHandler = colorHandler;
        return this;
    }

    public BlockBuilder<T> addRenderLayer(NonnullSupplier<Supplier<RenderType>> renderLayer) {
        EnvironmentSide.CLIENT.runIfCurrent(() -> () -> {
            if(!RenderType.chunkBufferLayers().contains(renderLayer.get().get())) {
                throw new IllegalArgumentException("Invalid block render layer: " + renderLayer.get().get());
            }
        });
        if(this.renderLayers.isEmpty()) onRegister(this::setupBlockRenderLayers);
        this.renderLayers.add(renderLayer);
        return this;
    }

    @Override
    protected T buildEntry() {
        BlockBehaviour.Properties properties = initialProperties.get();
        properties = propertiesCallbacks.apply(properties);

        return factory.apply(properties);
    }

    private void setupBlockRenderLayers(T entry) {
        EnvironmentSide.CLIENT.runIfCurrent(() -> () -> RenderingEvents.BLOCK_RENDER_TYPE_REGISTRATION.subscribe(output -> {
            if(renderLayers.size() == 1) {
                RenderType renderType = renderLayers.get(0).get().get();
                output.register(renderType, entry);
            } else {
                final Set<RenderType> layers = renderLayers.stream().map(s -> s.get().get()).collect(Collectors.toSet());
                layers.forEach(layer -> output.register(layer, entry));
            }
        }));
    }

    private void setupBlockColor(T entry) {
        EnvironmentSide.CLIENT.runIfCurrent(() -> () -> {
            var colorHandler = this.colorHandler;

            if(colorHandler != null) {
                RenderingEvents.BLOCK_COLOR_REGISTRATION.subscribe(output -> output.register(colorHandler.get().get(), entry));
            }
        });
    }

}
