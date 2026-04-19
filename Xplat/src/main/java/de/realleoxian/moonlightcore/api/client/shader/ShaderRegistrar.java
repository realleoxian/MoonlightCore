package de.realleoxian.moonlightcore.api.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface ShaderRegistrar {
    void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback);
}
