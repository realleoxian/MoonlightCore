package de.leoxian.moonlightcore.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;

import java.util.function.Consumer;

public interface RenderPipelineRegistrar {
    /// Configure and register new render pipelines
    /// @param namespace The mod's id
    /// @param initializer The initializer
    static void configure(String namespace, Consumer<RenderPipelineRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.renderPipelines(namespace, initializer);
    }

    /// Register a new render pipeline
    /// @param pipeline The render pipeline
    void register(RenderPipeline pipeline);
}
