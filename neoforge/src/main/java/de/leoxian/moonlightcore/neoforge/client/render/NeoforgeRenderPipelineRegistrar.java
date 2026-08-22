package de.leoxian.moonlightcore.neoforge.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public record NeoforgeRenderPipelineRegistrar(RegisterRenderPipelinesEvent event) implements RenderPipelineRegistrar {
    @Override
    public void register(RenderPipeline pipeline) {
        event.registerPipeline(pipeline);
    }
}
