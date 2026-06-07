package de.realleoxian.moonlightcore.api.client.screenshake;

import de.realleoxian.moonlightcore.api.client.util.Easing;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PositionedScreenshakeInstance extends ScreenshakeInstance {
    public static PositionedScreenshakeInstance create(Builder builder, Vec3 position, Easing falloffEasing, float falloffDistance, float maxDistance) {
        return new PositionedScreenshakeInstance(builder, position, falloffEasing, falloffDistance, maxDistance);
    }

    public static PositionedScreenshakeInstance create(Builder builder, Vec3 position, float falloffDistance, float maxDistance) {
        return create(builder, position, Easing.LINEAR, falloffDistance, maxDistance);
    }

    public final Vec3 position;
    public final Easing falloffEasing;
    public final float falloffDistance;
    public final float maxDistance;

    protected PositionedScreenshakeInstance(Builder builder, Vec3 position, Easing falloffEasing, float falloffDistance, float maxDistance) {
        super(builder);
        this.position = position;
        this.falloffEasing = falloffEasing;
        this.falloffDistance = falloffDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public float tick(Camera camera) {
        float intensity = super.tick(camera);
        float distance = (float) this.position.distanceTo(camera.getPosition());
        if (distance > this.maxDistance) {
            return 0;
        }

        float distanceMultiplier = 1;
        if (distance > this.falloffDistance) {
            float remaining = this.maxDistance - this.falloffDistance;
            float current = distanceMultiplier - falloffDistance;
            distanceMultiplier = 1 - current / remaining;
        }

        final var lookDirection = camera.getLookVector();
        final var directionToScreenshake = this.position.subtract(camera.getPosition()).normalize();
        float angle = Math.max(0, lookDirection.dot(new Vector3f((float) directionToScreenshake.x, (float) directionToScreenshake.y, (float) directionToScreenshake.z)));
        return (intensity + (intensity * angle)) * 0.5F * distanceMultiplier;
    }
}
