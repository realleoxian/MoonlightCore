package de.realleoxian.moonlightcore.api.client.screenshake;

import de.realleoxian.moonlightcore.api.client.misc.Easing;
import net.minecraft.client.Camera;

public interface ScreenshakeInstance {
    float tick(Camera camera);

    Easing startCurveEasing();

    Easing endCurveEasing();

    boolean rotationShake();

    boolean positionShake();

    boolean normalized();

    float startCurveIntensity();

    float endCurveIntensity();

    int durationTicks();

    int progress();

    interface Builder {
        Builder normalized(boolean normalized);

        Builder positionShake(boolean positionShake);

        Builder rotationShake(boolean rotationShake);

        Builder intensity(float startCurveIntensity, float endCurveIntensity);

        default Builder intensity(float intensity) {
            return this.intensity(intensity, intensity);
        }

        Builder easing(Easing startCurveEasing, Easing endCurveEasing);

        default Builder easing(Easing easing) {
            return this.easing(easing, easing);
        }
    }
}
