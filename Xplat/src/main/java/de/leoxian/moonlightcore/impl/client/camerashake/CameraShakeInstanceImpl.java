package de.leoxian.moonlightcore.impl.client.camerashake;

import de.leoxian.moonlightcore.api.client.camerashake.CameraShakeInstance;
import de.leoxian.moonlightcore.api.client.misc.Easing;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;

public final class CameraShakeInstanceImpl implements CameraShakeInstance {
    private final int durationTicks;

    private final float startIntensity;
    private final float middleIntensity;
    private final float endIntensity;

    private final Easing startCurveEasing;
    private final Easing endCurveEasing;

    private final boolean shakeRotation;
    private final boolean shakePosition;
    private final boolean normalized;

    int progress;

    CameraShakeInstanceImpl(BuilderImpl builder) {
        this.durationTicks = builder.durationTicks;
        this.startIntensity = builder.startIntensity;
        this.middleIntensity = builder.middleIntensity;
        this.endIntensity = builder.endIntensity;
        this.startCurveEasing = builder.startCurveEasing;
        this.endCurveEasing = builder.endCurveEasing;
        this.shakeRotation = builder.shakeRotation;
        this.shakePosition = builder.shakePosition;
        this.normalized = builder.normalized;
    }

    @Override
    public float tick(Camera camera) {
        this.progress++;
        float percentage = (float) (this.progress / this.durationTicks);

        if (this.middleIntensity != this.endIntensity) {
            if (percentage >= 0.5F) {
                return Mth.lerp(this.endCurveEasing.ease(percentage - 0.5F, 0, 1, 0.5F), this.middleIntensity, this.startIntensity);
            }

            return Mth.lerp(this.startCurveEasing.ease(percentage, 0, 1, 0.5F), this.startIntensity, this.middleIntensity);
        }

        return Mth.lerp(this.startCurveEasing.ease(percentage, 0, 1, 1), this.startIntensity, this.middleIntensity);
    }

    @Override
    public int getDurationTicks() {
        return this.durationTicks;
    }

    @Override
    public Easing getStartCurveEasing() {
        return this.startCurveEasing;
    }

    @Override
    public Easing getEndCurveEasing() {
        return this.endCurveEasing;
    }

    @Override
    public float getStartIntensity() {
        return this.startIntensity;
    }

    @Override
    public float getMiddleIntensity() {
        return this.middleIntensity;
    }

    @Override
    public float getEndIntensity() {
        return this.endIntensity;
    }

    @Override
    public boolean isRotationShake() {
        return this.shakeRotation;
    }

    @Override
    public boolean isPositionShake() {
        return this.shakePosition;
    }

    @Override
    public boolean isNormalized() {
        return this.normalized;
    }

    public static final class BuilderImpl implements CameraShakeInstance.Builder {
        private final int durationTicks;

        private float startIntensity = 0.0F;
        private float middleIntensity = 0.0F;
        private float endIntensity = 0.0F;

        private Easing startCurveEasing = Easing.LINEAR;
        private Easing endCurveEasing = Easing.LINEAR;

        private boolean shakeRotation = true;
        private boolean shakePosition = false;
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
        public Builder shakeRotation(boolean shakeRotation) {
            this.shakeRotation = shakeRotation;
            return this;
        }

        @Override
        public Builder shakePosition(boolean shakePosition) {
            this.shakePosition = shakePosition;
            return this;
        }

        @Override
        public Builder intensity(float startIntensity, float middleIntensity, float endIntensity) {
            this.startIntensity = startIntensity;
            this.middleIntensity = middleIntensity;
            this.endIntensity = endIntensity;
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
