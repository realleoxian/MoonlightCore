package de.leoxian.moonlightcore.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;

import java.util.function.Consumer;

public interface RenderPipelineRegistrar {
    static void init(String namespace, Consumer<RenderPipelineRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.renderPipelines(namespace, initializer);
    }

    void register(RenderPipeline pipeline);
}
