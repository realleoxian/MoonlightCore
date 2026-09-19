package de.leoxian.moonlightcore.client.fluid;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import jdk.jfr.Experimental;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Consumer;

@Experimental
public interface FluidRendererRegistrar {
    /// Configure and register fluids models and render handlers
    /// @param namespace The mod's id
    /// @param initializer The initializer
    static void configure(String namespace, Consumer<FluidRendererRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.fluidRenderer(namespace, initializer);
    }

    /// Register the given unbaked model to the given fluid
    /// @param holder The fluid holder
    /// @param model The unbaked model
    void registerModel(Holder<Fluid> holder, FluidModel.Unbaked model);

    /// Register the given unbaked model to both the source and flowing fluids
    /// @param source The source fluid holder
    /// @param flowing The flowing fluid holder
    /// @param model The unbaked model
    default void registerModel(Holder<Fluid> source, Holder<Fluid> flowing, FluidModel.Unbaked model) {
        registerModel(source, model);
        registerModel(flowing, model);
    }

    /// Register a render handler to the fluid
    /// @param holder The fluid holder
    /// @param renderHandler The render handler
    void registerRenderHandler(Holder<Fluid> holder, FluidRenderHandler renderHandler);

    /// Register a render handler to both the source and flowing fluids
    /// @param source The source fluid holder
    /// @param flowing The flowing fluid holder
    /// @param renderHandler The render handler
    default void registerRenderHandler(Holder<Fluid> source, Holder<Fluid> flowing, FluidRenderHandler renderHandler) {
        registerRenderHandler(source, renderHandler);
        registerRenderHandler(flowing, renderHandler);
    }
}
