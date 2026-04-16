package de.realleoxian.moonlightcore.api.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import de.realleoxian.moonlightcore.api.event.EventBus;
import de.realleoxian.moonlightcore.api.event.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;

public final class ViewportEvents {
    public static final EventBus<RenderFog> RENDER_FOG = EventBus.create(RenderFog.class, (listeners) -> (renderer, camera, context, partialTick) -> {
        for (RenderFog listener : listeners) {
            EventResult result = listener.onRenderFog(renderer, camera, context, partialTick);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.FALSE;
    });
    public static final EventBus<ComputeFogColor> COMPUTE_FOG_COLOR = EventBus.create(ComputeFogColor.class, (listeners) -> (renderer, camera, context, partialTick) -> {
       for (ComputeFogColor listener : listeners) {
           EventResult result = listener.onComputeFogColor(renderer, camera, context, partialTick);

           if(result.cancelFurtherProcessing) {
               return result;
           }
       }

       return EventResult.FALSE;
    });
    public static final EventBus<ComputeCameraAngle> COMPUTE_CAMERA_ANGLE = EventBus.create(ComputeCameraAngle.class, (listeners) -> (renderer, camera, context, partialTick) -> {
        for (ComputeCameraAngle listener : listeners) {
            EventResult result = listener.onComputeCameraAngle(renderer, camera, context, partialTick);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.FALSE;
    });

    private ViewportEvents() {}

    public interface RenderFog {
        EventResult onRenderFog(GameRenderer renderer, Camera camera, Context context, float partialTick);

        interface Context {
            void setStartDistance(float distance);

            void setEndDistance(float distance);

            void setShape(FogShape shape);

            float getStartDistance();

            float getEndDistance();

            FogShape getShape();

            FogRenderer.FogMode getMode();
        }
    }

    public interface ComputeFogColor {
        EventResult onComputeFogColor(GameRenderer renderer, Camera camera, Context context, float partialTick);

        interface Context {
            void setRed(float red);

            void setGreen(float green);

            void setBlue(float blue);

            float getRed();

            float getGreen();

            float getBlue();
        }
    }

    public interface ComputeCameraAngle {
        EventResult onComputeCameraAngle(GameRenderer renderer, Camera camera, Context context, float partialTick);

        interface Context {
            void setYaw(float yaw);

            void setPitch(float pitch);

            void setRoll(float roll);

            float getYaw();

            float getPitch();

            float getRoll();
        }
    }

}
