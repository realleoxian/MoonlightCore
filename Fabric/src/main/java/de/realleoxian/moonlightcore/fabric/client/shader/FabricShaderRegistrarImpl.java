package de.realleoxian.moonlightcore.fabric.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import de.realleoxian.moonlightcore.api.client.shader.ShaderRegistrar;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FabricShaderRegistrarImpl implements ShaderRegistrar {
    private final List<Registration> registrations = new ArrayList<>();

    public FabricShaderRegistrarImpl() {
        CoreShaderRegistrationCallback.EVENT.register((context) -> {
            for (var registration : this.registrations) {
                registration.register(context);
            }
        });
    }

    @Override
    public void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) {
        this.registrations.add(new Registration(id, vertexFormat, loadCallback));
    }

    private record Registration(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) {
        void register(CoreShaderRegistrationCallback.RegistrationContext ctx) throws IOException {
            ctx.register(id, vertexFormat, loadCallback);
        }
    }
}
