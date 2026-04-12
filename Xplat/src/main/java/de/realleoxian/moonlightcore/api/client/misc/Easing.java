package de.realleoxian.moonlightcore.api.client.misc;

public interface Easing {
    Easing LINEAR = (t, b, c, d) -> c * t / d + b;

    // --------------------[ QUAD EASING: t^2 ]--------------------

    Easing QUAD_IN = (t, b, c, d) -> c * (t /= d) * t + b;

    Easing QUAD_OUT = (t, b, c, d) -> -c * (t /= d) * (t - 2) + b;

    Easing QUAD_IN_OUT = (t, b, c, d) -> {
        if((t /= d / 2) < 1) return c / 2 * t * t + b;
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    };

    // --------------------[ CUBIC EASING: t^3 ]--------------------

    Easing CUBIC_IN = (t, b, c, d) -> c * (t /= d) * t * t + b;

    Easing CUBIC_OUT = (t, b, c, d) -> c* ((t = t / d - 1) * t * t + 1) + b;

    Easing CUBIC_IN_OUT = (t, b, c, d) -> {
        if((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t + 2) + b;
    };

    // --------------------[ QUART EASING: t^4 ]--------------------

    Easing QUART_IN = (t, b, c, d) -> c * (t /= d) * t * t * t + b;

    Easing QUART_OUT = (t, b, c, d) -> c* ((t = t / d - 1) * t * t * t + 1) + b;

    Easing QUART_IN_OUT = (t, b, c, d) -> {
        if((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t * t + 2) + b;
    };

    // --------------------[ QUINT EASING: t^5 ]--------------------

    Easing QUINT_IN = (t, b, c, d) -> c * (t /= d) * t * t * t * t + b;

    Easing QUINT_OUT = (t, b, c, d) -> c* ((t = t / d - 1) * t * t * t * t + 1) + b;

    Easing QUINT_IN_OUT = (t, b, c, d) -> {
        if((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    };

    // --------------------[ SIN EASING: sin(t) ]--------------------

    Easing SINE_IN = (t, b, c, d) -> -c * (float) Math.cos(t / d  * (Math.PI / 2)) + c + b;

    Easing SINE_OUT = (t, b, c, d) -> c * (float) Math.sin(t /d * (Math.PI / 2)) + b;

    Easing SINE_IN_OUT = (t, b, c, d) -> -c /2 * ((float) Math.cos(Math.PI * t / d) - 1) + b;

    // --------------------[ EXPO EASING: 2^t ]--------------------

    Easing EXPO_IN = (t, b, c, d) -> (t == 0) ? b : c * (float) Math.pow(2, 10 * (t / d - 1)) + b;

    Easing EXPO_OUT = (t, b, c, d) -> (t == d) ? b + c : c * (-(float) Math.pow(2, -10 * t / d) + 1) + b;

    Easing EXPO_IN_OUT = (t, b, c, d) -> {
        if(t == 0) return b;
        if(t == d) return b + c;
        if((t /= d / 2) < 1) return c / 2 * (float) Math.pow(2, 10 * (t - 1)) + b;

        return c / 2 * (-(float) Math.pow(2, -10 * --t) + 2) + b;
    };

    // --------------------[ ELASTIC EASING: Exponentially decaying sine wave ]--------------------

    Easing ELASTIC_IN = (t, b, c, d) -> {
        if(t == 0) return b;
        if((t /= d)  == 1) return b + c;

        float p = d * .3f;
        float a = c;
        float s = p / 4;

        return -(a *(float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p)) + b;
    };

    Easing ELASTIC_OUT = (t, b, c, d) -> {
        if(t == 0) return b;
        if((t /= d)  == 1) return b + c;

        float p = d * .3f;
        float a = c;
        float s = p / 4;

        return (a *(float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p)) + b;
    };

    Easing ELASTIC_IN_OUT = (t, b, c, d) -> {
        if(t == 0) return b;
        if((t /= d/2) == 2) return b + c;

        float p=d*(.3f*1.5f);
        float a=c;
        float s=p/4;

        if (t < 1) return -.5f * (a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p )) + b;
        return a * (float) Math.pow(2, -10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * (float) Math.PI) / p ) * .5f + c + b;
    };

    // --------------------[ CIRCULAR EASING: sqrt(1 - t^2) ]--------------------

    Easing CIRC_IN = (t, b, c, d) -> -c * ((float) Math.sqrt(1 - (t /= d)  * t) - 1) + b;

    Easing CIRC_OUT = (t, b, c, d) -> c * ((float) Math.sqrt(1 - (t = t / d - 1) - 1) * t) + b;

    Easing CIRC_IN_OUT = (t, b, c, d) -> {
        if((t /= d / 2) < 1) return -c / 2 * ((float) Math.sqrt(1 - t * t) - 1) + b;
        return c / 2 * ((float) Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    };

    // --------------------[ BOUNCE EASING: Exponentially decaying parabolic bounce ]--------------------

    Easing BOUNCE_OUT = (t, b, c, d) -> {
        if((t /= d) < (1 / 2.75f)) return c * (7.5625f * t * t) + b;
        else if (t < (2 / 2.75f)) return c * (7.5625f * (t -= (1.5f / 2.75f)) * t + .75f) + b;
        else if(t < (2.5 / 2.75)) return c * (7.5625f * (t -= (2.25f / 2.75f)) * t + .9375f) + b;

        return c * (7.5625f * (t -= (2.625f / 2.75f)) * t + .984375f) + b;
    };

    Easing BOUNCE_IN = (t, b, c, d) -> c - BOUNCE_OUT.ease(d - t, 0, c, d) + b;

    Easing BOUNCE_IN_OUT = (t, b, c, d) -> {
        if(t < d / 2) return BOUNCE_IN.ease(t * 2, 0, c, d) * .5f + b;
        return BOUNCE_OUT.ease(t * 2 - d, 0, c, d) * .5f + c * .5f + b ;
    };

    // --------------------[ BACK EASING: overshooting cubic easing: (s  + 1) * t^3 - s * t^2 ]--------------------

    Back BACK_IN = new BackIn();

    Back BACK_OUT = new BackOut();

    Back BACK_IN_OUT = new BackInOut();

    float ease(float time, float beginningValue, float changedValue, float duration);

    abstract class Back implements Easing {
        // 10% overshoot
        private static final float DEFAULT_OVERSHOOT = 1.70158f;

        protected float overshoot;

        public Back(float overshoot) {
            this.overshoot = overshoot;
        }

        public Back() {
            this(DEFAULT_OVERSHOOT);
        }

        public void overshoot(float overshoot) {
            this.overshoot = overshoot;
        }
    }

    final class BackIn extends Back {
        public BackIn(float overshoot) {
            super(overshoot);
        }

        public BackIn() {
            super();
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float s = this.overshoot;
            return c * (t /= d) * t * ((s + 1) * t - s) + b;
        }
    }

    final class BackOut extends Back {
        public BackOut(float overshoot) {
            super(overshoot);
        }

        public BackOut() {
            super();
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float s = this.overshoot;
            return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
        }
    }

    final class BackInOut extends Back {
        public BackInOut(float overshoot) {
            super(overshoot);
        }

        public BackInOut() {
            super();
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float s = this.overshoot;

            if((t /= d / 2) < 1) return c / 2 * (t * t * (((s *= 1.525F) + 1) * t - s)) + b;
            return c / 2 * ((t -= 2) * t * (((s *= 1.525F) + 1) * t + s) + 2) + b;
        }
    }
}