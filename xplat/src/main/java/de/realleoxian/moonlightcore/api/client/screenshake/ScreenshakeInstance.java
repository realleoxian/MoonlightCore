package de.realleoxian.moonlightcore.api.client.screenshake;

import de.realleoxian.moonlightcore.api.client.util.Easing;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;

public class ScreenshakeInstance {
    public static Builder builder(int durationTicks) {
        return new Builder(durationTicks);
    }

    public final int durationTicks;
    public final Easing startCurveEasing;
    public final Easing endCurveEasing;

    public final float startCurveIntensity;
    public final float endCurveIntensity;

    public final boolean rotationShake;
    public final boolean positionShake;
    public final boolean normalized;

    private int progress;

    protected ScreenshakeInstance(Builder builder) {
        this.durationTicks = builder.durationTicks;
        this.startCurveEasing = builder.startCurveEasing;
        this.endCurveEasing = builder.endCurveEasing;
        this.startCurveIntensity = builder.startCurveIntensity;
        this.endCurveIntensity = builder.endCurveIntensity;
        this.rotationShake = builder.rotationShake;
        this.positionShake = builder.positionShake;
        this.normalized = builder.normalized;
        this.progress = 0;
    }

    public void start() {
        this.progress = 0;
        ScreenshakeHandler.INSTANCES.add(this);
    }

    public float tick(Camera camera) {
        this.progress++;
        float percentage = this.progress / (float) this.durationTicks;
        if (percentage >= 0.5F) return Mth.lerp(this.endCurveEasing.ease((percentage - 0.5F) * 2.0F, 0, 1, 0.5F), this.endCurveIntensity, this.startCurveIntensity);
        return Mth.lerp(this.startCurveEasing.ease(percentage, 0, 1, 1), this.startCurveIntensity, this.endCurveIntensity);
    }

    public int progress() {
        return this.progress;
    }

    public static class Builder {
        private final int durationTicks;

        private Easing startCurveEasing = Easing.LINEAR;
        private Easing endCurveEasing = Easing.LINEAR;

        private float startCurveIntensity = 1.0F;
        private float endCurveIntensity = 1.0F;

        private boolean rotationShake = true;
        private boolean positionShake = false;
        private boolean normalized = true;

        private Builder(int durationTicks) {
            this.durationTicks = durationTicks;
        }

        public Builder normalized(boolean normalized) {
            this.normalized = normalized;
            return this;
        }

        public Builder positionShake(boolean positionShake) {
            this.positionShake = positionShake;
            return this;
        }

        public Builder rotationShake(boolean rotationShake) {
            this.rotationShake = rotationShake;
            return this;
        }

        public Builder intensity(float startCurveIntensity, float endCurveIntensity) {
            this.startCurveIntensity = startCurveIntensity;
            this.endCurveIntensity = endCurveIntensity;
            return this;
        }

        public Builder easing(Easing startCurveEasing, Easing endCurveEasing) {
            this.startCurveEasing = startCurveEasing;
            this.endCurveEasing = endCurveEasing;
            return this;
        }

        public ScreenshakeInstance build() {
            return new ScreenshakeInstance(this);
        }
    }
}
