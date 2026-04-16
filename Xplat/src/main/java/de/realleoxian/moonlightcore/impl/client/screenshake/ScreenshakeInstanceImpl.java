package de.realleoxian.moonlightcore.impl.client.screenshake;

import de.realleoxian.moonlightcore.api.client.misc.Easing;
import de.realleoxian.moonlightcore.api.client.screenshake.ScreenshakeInstance;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;

public class ScreenshakeInstanceImpl implements ScreenshakeInstance {
    private final int durationTicks;

    private final Easing startCurveEasing;
    private final Easing endCurveEasing;

    private final float startCurveIntensity;
    private final float endCurveIntensity;

    private final boolean rotationShake;
    private final boolean positionShake;
    private final boolean normalized;

    private int progress;

    ScreenshakeInstanceImpl(BuilderImpl builder) {
        this.durationTicks = builder.durationTicks;
        this.startCurveEasing = builder.startCurveEasing;
        this.endCurveEasing = builder.endCurveEasing;
        this.startCurveIntensity = builder.startCurveIntensity;
        this.endCurveIntensity = builder.endCurveIntensity;
        this.rotationShake = builder.rotationShake;
        this.positionShake = builder.positionShake;
        this.normalized = builder.normalized;
    }

    @Override
    public float tick(Camera camera) {
        this.progress++;
        float percentage = this.progress / (float) this.durationTicks;

        if (percentage >= 0.5F)
            return Mth.lerp(this.endCurveEasing.ease((percentage - 0.5F) * 2.0F,0, 1, 0.5F), this.endCurveIntensity, this.startCurveIntensity);
        return Mth.lerp(this.startCurveEasing.ease(percentage, 0, 1, 1), this.startCurveIntensity, this.endCurveIntensity);
    }

    @Override
    public Easing startCurveEasing() {
        return this.startCurveEasing;
    }

    @Override
    public Easing endCurveEasing() {
        return this.endCurveEasing;
    }

    @Override
    public boolean rotationShake() {
        return this.rotationShake;
    }

    @Override
    public boolean positionShake() {
        return this.positionShake;
    }

    @Override
    public boolean normalized() {
        return this.normalized;
    }

    @Override
    public float startCurveIntensity() {
        return this.startCurveIntensity;
    }

    @Override
    public float endCurveIntensity() {
        return this.endCurveIntensity;
    }

    @Override
    public int durationTicks() {
        return this.durationTicks;
    }

    @Override
    public int progress() {
        return this.progress;
    }

    public static class BuilderImpl implements ScreenshakeInstance.Builder {
        private final int durationTicks;

        private Easing startCurveEasing = Easing.LINEAR;
        private Easing endCurveEasing = Easing.LINEAR;

        private float startCurveIntensity = 1.0F;
        private float endCurveIntensity = 1.0F;

        private boolean rotationShake = true;
        private boolean positionShake = false;
        private boolean normalized = true;

        public BuilderImpl(int durationTicks) {
            this.durationTicks = durationTicks;
        }

        @Override
        public Builder normalized(boolean normalized) {
            this.normalized = normalized;
            return this;
        }

        @Override
        public Builder positionShake(boolean positionShake) {
            this.positionShake = positionShake;
            return this;
        }

        @Override
        public Builder rotationShake(boolean rotationShake) {
            this.rotationShake = rotationShake;
            return this;
        }

        @Override
        public Builder intensity(float startCurveIntensity, float endCurveIntensity) {
            this.startCurveIntensity = startCurveIntensity;
            this.endCurveIntensity = endCurveIntensity;
            return this;
        }

        @Override
        public Builder easing(Easing startCurveEasing, Easing endCurveEasing) {
            this.startCurveEasing = startCurveEasing;
            this.endCurveEasing = endCurveEasing;
            return this;
        }
    }
}
