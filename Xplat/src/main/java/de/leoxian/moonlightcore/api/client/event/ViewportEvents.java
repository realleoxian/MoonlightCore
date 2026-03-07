package de.leoxian.moonlightcore.api.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import de.leoxian.moonlightcore.api.event.EventBus;
import de.leoxian.moonlightcore.api.event.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;

public final class ViewportEvents {
    /**
     * @see RenderFog#onRenderFog(GameRenderer, Camera, RenderFog.Context, float)
     */
    public static final EventBus<RenderFog> RENDER_FOG = EventBus.create((listeners) -> (renderer, camera, context, partialTick) -> {
        for (RenderFog listener : listeners) {
            EventResult result = listener.onRenderFog(renderer, camera, context, partialTick);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.FALSE;
    });
    /**
     * @see ComputeFogColor#onComputeFogColor(GameRenderer, Camera, ComputeFogColor.Context, float)
     */
    public static final EventBus<ComputeFogColor> COMPUTE_FOG_COLOR = EventBus.create((listeners) -> (renderer, camera, context, partialTick) -> {
       for (ComputeFogColor listener : listeners) {
           EventResult result = listener.onComputeFogColor(renderer, camera, context, partialTick);

           if(result.cancelFurtherProcessing) {
               return result;
           }
       }

       return EventResult.FALSE;
    });
    /**
     * @see ComputeCameraAngle#onComputeCameraAngle(GameRenderer, Camera, ComputeCameraAngle.Context, float)
     */
    public static final EventBus<ComputeCameraAngle> COMPUTE_CAMERA_ANGLE = EventBus.create((listeners) -> (renderer, camera, context, partialTick) -> {
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

        /**
         * Invoked when rendering the fog, this can be used for <b>rendering</b> custom fog.
         * @param renderer      The game renderer
         * @param camera        The camera information
         * @param context       The context of the event, used to modify the data
         * @param partialTick   The current partial tick value when invoking this event
         * @return An {@link EventResult} that could cancel, or could allow the event to modify the data if {@linkplain EventResult#TRUE}
         */
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

        /**
         * Invoked when computing the current color of the fog, this can be used to customize the fog color
         * @param renderer      The game renderer
         * @param camera        The camera information
         * @param context       The context of the event, used to modify the data
         * @param partialTick   The current partial tick value when invoking this event
         * @return An {@link EventResult} that could cancel, or could allow the event to modify the data if {@linkplain EventResult#TRUE}
         */
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

        /**
         * Invoked when computing the current camera angle, this can be used to modify the camera angle
         * @param renderer      The game renderer
         * @param camera        The camera information
         * @param context       The context of the event, used to modify the data
         * @param partialTick   The current partial tick value when invoking this event
         * @return An {@link EventResult} that could cancel, or could allow the event to modify the data if {@linkplain EventResult#TRUE}
         */
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
