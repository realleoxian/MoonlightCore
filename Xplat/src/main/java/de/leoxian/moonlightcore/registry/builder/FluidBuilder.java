/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 * Modifications by Leoxian, 2025
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.fluid.BaseFlowingFluid;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullConsumer;
import de.leoxian.moonlightcore.util.nullness.NonnullFunction;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

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

    public FluidBuilder<T> bucket(NonnullSupplier<? extends Item> bucket) {
        return properties(p -> p.bucket(bucket));
    }

    public FluidBuilder<T> block(NonnullSupplier<? extends LiquidBlock> block) {
        return properties(p -> p.block(block));
    }

    public FluidBuilder<T> pickupSound(Supplier<SoundEvent> pickupSound) {
        return properties(p -> p.pickupSound(pickupSound));
    }

    public FluidBuilder<T> slopeFindDistance(int slopeFindDistance) {
        return properties(p -> p.slopeFindDistance(slopeFindDistance));
    }

    public FluidBuilder<T> levelDecreasePerBlock(int levelDecreasePerBlock) {
        return properties(p -> p.levelDecreasePerBlock(levelDecreasePerBlock));
    }

    public FluidBuilder<T> explosionResistance(int explosionResistance) {
        return properties(p -> p.explosionResistance(explosionResistance));
    }

    public FluidBuilder<T> tickRate(int tickRate) {
        return properties(p -> p.tickRate(tickRate));
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
