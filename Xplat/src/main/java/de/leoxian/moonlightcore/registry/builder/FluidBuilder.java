package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.fluid.BaseFlowingFluid;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullConsumer;
import de.leoxian.moonlightcore.util.nullness.NonnullFunction;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.level.material.Fluid;

public class FluidBuilder<T extends BaseFlowingFluid> extends AbstractBuilder<Fluid, T, FluidBuilder<T>> {

    public static <T extends BaseFlowingFluid> FluidBuilder<T> builder(DeferredRegistrar<Fluid> registrar, String name, NonnullFunction<BaseFlowingFluid.Properties, T> factory) {
        return new FluidBuilder<>(registrar, name, factory);
    }

    private final NonnullFunction<BaseFlowingFluid.Properties, T> factory;

    private NonnullConsumer<BaseFlowingFluid.Properties> propertiesCallback = p -> {};
    private @Nullable NonnullFunction<BaseFlowingFluid.Properties, ? extends BaseFlowingFluid> sourceFactory;
    private @Nullable BaseFlowingFluid builtPropertiesStill;

    protected FluidBuilder(DeferredRegistrar<Fluid> registrar, String name, NonnullFunction<BaseFlowingFluid.Properties, T> factory) {
        super(registrar, "flowing_" + name);
        this.factory = factory;
    }

    public FluidBuilder<T> properties(NonnullConsumer<BaseFlowingFluid.Properties> consumer) {
        this.propertiesCallback = this.propertiesCallback.andThen(consumer);
        return this;
    }

    public FluidBuilder<T> source(NonnullFunction<BaseFlowingFluid.Properties, ? extends BaseFlowingFluid> factory) {
        this.sourceFactory = factory;
        return this;
    }

    @Override
    protected T buildEntry() {
        BaseFlowingFluid.Properties builtProperties = new BaseFlowingFluid.Properties(() -> builtPropertiesStill, this::getValue);
        propertiesCallback.accept(builtProperties);
        builtPropertiesStill = (sourceFactory != null) ? sourceFactory.apply(builtProperties) : new BaseFlowingFluid.Source(builtProperties);

        return factory.apply(builtProperties);
    }

}
