package de.realleoxian.moonlightcore.api.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import de.realleoxian.moonlightcore.api.event.CancellableEvent;
import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus;

public sealed class ViewportEvents extends EventBase {
    public static final Event<RenderFog> RENDER_FOG = Event.create(ViewportEvents.RenderFog.class);
    public static final Event<ComputeFogColor> COMPUTE_FOG_COLOR = Event.create(ViewportEvents.ComputeFogColor.class);
    public static final Event<ComputeCameraAngle> COMPUTE_CAMERA_ANGLE = Event.create(ViewportEvents.ComputeCameraAngle.class);

    public final GameRenderer renderer;
    public final Camera camera;
    public final float partialTick;

    protected ViewportEvents(GameRenderer renderer, Camera camera, float partialTick) {
        this.renderer = renderer;
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public static final class RenderFog extends ViewportEvents implements CancellableEvent {
        public final FogRenderer.FogMode mode;
        private float startDistance;
        private float endDistance;
        public FogShape shape;

        @ApiStatus.Internal
        public RenderFog(GameRenderer renderer, Camera camera, float partialTick, FogRenderer.FogMode mode, float startDistance, float endDistance, FogShape shape) {
            super(renderer, camera, partialTick);
            this.mode = mode;
            this.startDistance = startDistance;
            this.endDistance = endDistance;
            this.shape = shape;
        }
    }

    public static final class ComputeFogColor extends ViewportEvents implements CancellableEvent {
        public float red;
        public float green;
        public float blue;

        @ApiStatus.Internal
        public ComputeFogColor(GameRenderer renderer, Camera camera, float partialTick, float red, float green, float blue) {
            super(renderer, camera, partialTick);
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    public static final class ComputeCameraAngle extends ViewportEvents implements CancellableEvent {
        public float yaw;
        public float pitch;
        public float roll;

        @ApiStatus.Internal
        public ComputeCameraAngle(GameRenderer renderer, Camera camera, float partialTick, float yaw, float pitch, float roll) {
            super(renderer, camera, partialTick);
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }
    }
}
