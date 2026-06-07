package de.realleoxian.moonlightcore.api.client.screenshake;

import de.realleoxian.moonlightcore.mixin.client.CameraInvoker;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public final class ScreenshakeHandler {
    static final List<ScreenshakeInstance> INSTANCES = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private static float rotationIntensity = 0.0F;
    private static float positionIntensity = 0.0F;

    public static void cameraTick(Camera camera) {
        if (rotationIntensity > 0.01F) {
            float yawOffset = randomOffset(rotationIntensity);
            float pitchOffset = randomOffset(rotationIntensity);
            ((CameraInvoker) camera).setRotation(camera.getYRot() + yawOffset, camera.getXRot() + pitchOffset);
        }

        var pos = camera.getPosition();
        if (positionIntensity > 0.01F) {
            double a = RANDOM.nextDouble() * Mth.PI * 2;
            double b = RANDOM.nextDouble() * Mth.PI * 2;

            float x = (float) (Math.cos(a) * Math.cos(b)) * randomOffset(positionIntensity);
            float y = (float) (Math.sin(a) * Math.cos(b)) * randomOffset(positionIntensity);
            float z = (float) Math.sin(b) * randomOffset(positionIntensity);
            ((CameraInvoker) camera).setPosition(pos.add(x, y, z));
        }
    }

    public static void clientTick(Camera camera) {
        double modifier = 0.0F; // TODO: Add a config for this

        double rotationNormalize = 0;
        double rotation = 0;
        double positionNormalize = 0;
        double position = 0;
        for (final var instance : INSTANCES) {
            float intensity = instance.tick(camera);

            if (instance.rotationShake) {
                if (instance.normalized) rotationNormalize += intensity;
                else if (rotation < intensity) rotation = intensity;
            }
            if (instance.positionShake) {
                if (instance.normalized) positionNormalize += intensity;
                else if (position < intensity) position = intensity;
            }
        }

        rotationNormalize = Math.min(rotationNormalize, modifier);
        positionNormalize = Math.min(positionNormalize, modifier);
        rotation *= modifier;
        position *= modifier;

        rotationIntensity = (float) Math.max(Math.pow(rotationNormalize, 3), rotation);
        positionIntensity = (float) Math.max(Math.pow(positionNormalize / 2, 3), position);
        INSTANCES.removeIf(i -> i.progress() >= i.durationTicks);
    }

    public static float randomOffset(float offset) {
        return Mth.nextFloat(RANDOM, -offset * 2, offset * 2);
    }

    private ScreenshakeHandler() {}
}
