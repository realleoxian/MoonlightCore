package de.realleoxian.moonlightcore.impl.client.screenshake;

import de.realleoxian.moonlightcore.api.client.event.ClientTickEvents;
import de.realleoxian.moonlightcore.api.client.screenshake.ScreenshakeInstance;
import de.realleoxian.moonlightcore.api.event.EventPriority;
import de.realleoxian.moonlightcore.core.config.CoreClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ScreenshakeHandlerImpl {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final List<ScreenshakeInstance> INSTANCES = new ArrayList<>();

    private static float rotationIntensity = 0.0F;
    private static float positionIntensity = 0.0F;

    static {
        ClientTickEvents.TICK_END.subscribe(EventPriority.HIGHEST, (mc) -> {
            if (!mc.isPaused()) {
                Camera mainCamera = mc.gameRenderer.getMainCamera();
                clientTick(mainCamera);
            }
        });
    }

    public static void addScreenshake(int durationTicks, Consumer<ScreenshakeInstance.Builder> builderModifier) {
        ScreenshakeInstanceImpl.BuilderImpl builder = new ScreenshakeInstanceImpl.BuilderImpl(durationTicks);
        builderModifier.accept(builder);

        INSTANCES.add(new ScreenshakeInstanceImpl(builder));
    }

    public static void cameraTick(Camera camera) {
        if (rotationIntensity > 0.01F) {
            float yawOffset = randomizeOffset(rotationIntensity);
            float pitchOffset = randomizeOffset(rotationIntensity);
            camera.setRotation(camera.getYRot() + yawOffset, camera.getXRot() + pitchOffset);
        }

        Vec3 pos = camera.getPosition();
        if (positionIntensity > 0.01F) {
            double a = RANDOM.nextDouble() * Mth.PI * 2;
            double b = RANDOM.nextDouble() * Mth.PI * 2;

            float x = (float) (Math.cos(a) * Math.cos(b)) * randomizeOffset(positionIntensity);
            float y = (float) (Math.sin(a) * Math.cos(b)) * randomizeOffset(positionIntensity);
            float z = (float) Math.sin(b) * randomizeOffset(positionIntensity);
            camera.setPosition(pos.add(x, y, z));
        }
    }

    private static void clientTick(Camera camera) {
        double modifier = CoreClientConfig.CONFIG.cameraShakeIntensityModifier.get();

        double rotationNormalize = 0;
        double rotation = 0;
        double positionNormalize = 0;
        double position = 0;

        for (ScreenshakeInstance instance : INSTANCES) {
            float update = instance.tick(camera);

            if (instance.rotationShake()) {
                if (instance.normalized()) rotationNormalize += update;
                else if (rotation < update) rotation = update;
            }

            if (instance.positionShake()) {
                if (instance.normalized()) positionNormalize += update;
                else if (position < update) position = update;
            }
        }
        rotationNormalize = Math.min(rotationNormalize, modifier);
        positionNormalize = Math.min(positionNormalize, modifier);
        rotation *= modifier;
        position *= modifier;

        rotationIntensity = (float) Math.max(Math.pow(rotationNormalize, 3), rotation);
        positionIntensity = (float) Math.max(Math.pow(positionNormalize / 2, 3), position);
        INSTANCES.removeIf(i -> i.progress() >= i.durationTicks());
    }

    private static float randomizeOffset(float offset) {
        return Mth.nextFloat(RANDOM, -offset * 2, offset * 2);
    }
}
