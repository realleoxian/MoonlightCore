package de.leoxian.moonlightcore.fabric.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import net.minecraft.client.renderer.RenderPipelines;

public enum FabricRenderPipelineRegistrar implements RenderPipelineRegistrar {
    INSTANCE
    ;

    @Override
    public void register(RenderPipeline pipeline) {
        RenderPipelines.register(pipeline);
    }
}
