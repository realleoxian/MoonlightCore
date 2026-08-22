package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.CompoundEventResult;
import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public final class ViewportEvents {
    public static final Event<RenderFog> RENDER_FOG = Event.create(RenderFog.class, listeners -> (gameRenderer, camera, partialTick, fogEnvironment, fogData) -> {
        var ret = new CompoundEventResult<FogData>(EventResult.SUCCESS, fogData);
        for (final var listener : listeners) {
            ret = listener.onRenderFog(gameRenderer, camera, partialTick, fogEnvironment, fogData);
            if (ret.result().cancelFurtherEventProcessing()) {
                break;
            }
        }
        return ret;
    });
    public static final Event<ComputeFogColor> COMPUTE_FOG_COLOR = Event.create(ComputeFogColor.class, listeners -> (gameRenderer, camera, partialTick, context) -> {
       var ret = EventResult.SUCCESS;
       for (final var listener : listeners) {
           ret = listener.onComputeFogColor(gameRenderer, camera, partialTick, context);
           if (ret.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return ret;
    });
    public static final Event<ComputeCameraAngles> COMPUTE_CAMERA_ANGLES  = Event.create(ComputeCameraAngles.class, listeners -> (gameRenderer, camera, partialTick, context) -> {
       var ret = EventResult.SUCCESS;
       for (final var listener : listeners) {
           ret = listener.onComputeCameraAngles(gameRenderer, camera, partialTick, context);
           if (ret.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return ret;
    });

    private ViewportEvents() {}

    @FunctionalInterface
    public interface RenderFog {
        CompoundEventResult<FogData> onRenderFog(GameRenderer gameRenderer, Camera camera, float partialTick, @Nullable FogEnvironment fogEnvironment, FogData fogData);
    }

    @FunctionalInterface
    public interface ComputeFogColor {
        EventResult onComputeFogColor(GameRenderer gameRenderer, Camera camera, float partialTick, Context context);

        @ApiStatus.NonExtendable
        interface Context {
            void red(float red);

            float red();

            void green(float green);

            float green();

            void blue(float blue);

            float blue();
        }
    }

    @FunctionalInterface
    public interface ComputeCameraAngles {
        EventResult onComputeCameraAngles(GameRenderer gameRenderer, Camera camera, float partialTick, Context context);

        @ApiStatus.NonExtendable
        interface Context {
            void yaw(float yaw);

            float yaw();

            void pitch(float pitch);

            float pitch();

            void roll(float roll);

            float roll();
        }
    }
}
