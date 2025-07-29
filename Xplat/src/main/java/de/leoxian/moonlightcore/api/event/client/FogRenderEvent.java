package de.leoxian.moonlightcore.api.event.client;

import com.mojang.blaze3d.shaders.FogShape;
import de.leoxian.moonlightcore.api.event.Event;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;

public interface FogRenderEvent {
    Event<FogRendering> FOG_RENDERING = Event.create();
    Event<FogColorCompute> FOG_COLOR_COMPUTE = Event.create();

    @FunctionalInterface
    interface FogRendering {
        void bootstrap(GameRenderer renderer, Context context, float partialTick);

        interface Context {
            void setFarPlaneDistance(float distance);

            void setNearPlaneDistance(float distance);

            void setFogShape(FogShape shape);

            void scaleFarPlaneDistance(float factor);

            void scaleNearPlaneDistance(float factor);

            float getFarPlaneDistance();

            float getNearPlaneDistance();

            Camera getCamera();

            FogShape getFogShape();

            FogRenderer.FogMode getMode();

            FogType getType();
        }
    }

    @FunctionalInterface
    interface FogColorCompute {
        void bootstrap(GameRenderer renderer, Context context, float partialTick);

        interface Context {
            void build();

            void setRed(float red);

            void setGreen(float green);

            void setBlue(float blue);

            float getRed();

            float getGreen();

            float getBlue();

            boolean isValid();

            Camera getCamera();
        }
    }
}
