package de.realleoxian.moonlightcore.forge.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import de.realleoxian.moonlightcore.api.client.shader.ShaderRegistrar;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgeShaderRegistrarImpl implements ShaderRegistrar {
    private final List<Registration> registrations = new ArrayList<>();

    @SubscribeEvent
    private void onRegisterShaders(RegisterShadersEvent event) {
        try {
            for (var registration : this.registrations) {
                registration.register(event);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to register shaders", e);
        }
    }

    @Override
    public void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) {
        this.registrations.add(new Registration(id, vertexFormat, loadCallback));
    }

    private record Registration(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) {
        void register(RegisterShadersEvent event) throws IOException {
            ShaderInstance instance = new ShaderInstance(event.getResourceProvider(), id(), vertexFormat());
            event.registerShader(instance, loadCallback);
        }
    }
}
