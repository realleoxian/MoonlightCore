package de.leoxian.moonlightcore.api.client.camerashake;

import de.leoxian.moonlightcore.api.client.misc.Easing;
import de.leoxian.moonlightcore.impl.client.camerashake.CameraShakeInstanceImpl;
import net.minecraft.client.Camera;

public interface CameraShakeInstance {
    static Builder builder(int durationTicks) {
        return new CameraShakeInstanceImpl.BuilderImpl(durationTicks);
    }

    float tick(Camera camera);

    int getDurationTicks();

    Easing getStartCurveEasing();

    Easing getEndCurveEasing();

    float getStartIntensity();

    float getMiddleIntensity();

    float getEndIntensity();

    boolean isRotationShake();

    boolean isPositionShake();

    boolean isNormalized();

    interface Builder {
        Builder normalized(boolean normalized);

        Builder shakeRotation(boolean shakeRotation);

        Builder shakePosition(boolean shakePosition);

        Builder intensity(float startIntensity, float middleIntensity, float endIntensity);

        default Builder intensity(float startIntensity, float middleIntensity) {
            return intensity(startIntensity, middleIntensity, middleIntensity);
        }

        default Builder intensity(float intensity) {
            return intensity(intensity, intensity, intensity);
        }

        Builder easing(Easing startCurveEasing, Easing endCurveEasing);

        default Builder easing(Easing easing) {
            return easing(easing, easing);
        }
    }
}
