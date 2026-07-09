package de.leoxian.moonlightcore.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public interface RenderPipelineRegistrar {
    void register(RenderPipeline pipeline);
}
