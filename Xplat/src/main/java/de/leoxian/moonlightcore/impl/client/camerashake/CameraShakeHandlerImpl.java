package de.leoxian.moonlightcore.impl.client.camerashake;

import de.leoxian.moonlightcore.api.client.camerashake.CameraShakeInstance;
import de.leoxian.moonlightcore.api.client.event.ClientTickEvents;
import de.leoxian.moonlightcore.api.client.event.ViewportEvents;
import de.leoxian.moonlightcore.api.event.EventPriority;
import de.leoxian.moonlightcore.impl.internal.config.InternalClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class CameraShakeHandlerImpl {
    private static final List<CameraShakeInstance> INSTANCES = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private static float rotationIntensity;
    private static float positionIntensity;

    static {
        ClientTickEvents.TICK_END.subscribe(EventPriority.HIGHEST, (client) -> {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            clientTick(camera);
        });
    }

    public static void addCameraShake(CameraShakeInstance.Builder builder) {
        INSTANCES.add(new CameraShakeInstanceImpl((CameraShakeInstanceImpl.BuilderImpl) builder));
    }

    public static void cameraTick(Camera camera) {
        if (rotationIntensity > 0) {
            float yawOffset = randomizeOffset(rotationIntensity);
            float pitchOffset = randomizeOffset(rotationIntensity);
            camera.setRotation(camera.getYRot() + yawOffset, camera.getXRot() + pitchOffset);
        }

        boolean update = false;
        Vec3 pos = camera.getPosition();
        if (positionIntensity > 0) {
            double angleA = RANDOM.nextDouble() * Mth.PI * 2.0F;
            double angleB = RANDOM.nextDouble() * Mth.PI * 2.0F;

            float x = (float) (Math.cos(angleA) * Math.cos(angleB)) * randomizeOffset(positionIntensity);
            float y = (float) (Math.sin(angleA) * Math.cos(angleB)) * randomizeOffset(positionIntensity);
            float z = (float) Math.sin(angleB) * randomizeOffset(positionIntensity);
            pos = pos.add(x, y, z);
            update = true;
        }

        if (update) camera.setPosition(pos);
    }

    private static void clientTick(Camera camera) {
        double intensityModifier = InternalClientConfig.CONFIG.cameraShakeIntensityModifier.get();
        double rotationNormalize = 0;
        double rotation = 0;
        double positionNormalize = 0;
        double position = 0;

        for (CameraShakeInstance instance : INSTANCES) {
            double update = instance.tick(camera);

            if (instance.isRotationShake()) {
                if (instance.isNormalized())
                    rotationNormalize += update;
                else if (rotation < update)
                    rotation = update;
            }

            if (instance.isPositionShake()) {
                if (instance.isNormalized())
                    positionNormalize += update;
                else if (position < update)
                    position = update;
            }
        }
        rotationNormalize = Math.min(rotationNormalize, intensityModifier);
        positionNormalize = Math.min(positionNormalize, intensityModifier);
        rotation *= intensityModifier;
        position *= intensityModifier;

        rotationIntensity = (float) Math.max(Math.pow(rotationNormalize, 3), rotation);
        positionIntensity = (float) Math.max(Math.pow(positionNormalize / 2, 3), position);
        INSTANCES.removeIf(i -> ((CameraShakeInstanceImpl) i).progress >= i.getDurationTicks());
    }

    private static float randomizeOffset(float offset) {
        return Mth.nextFloat(RANDOM, -offset * 2, offset * 2);
    }

    private CameraShakeHandlerImpl() {}
}
