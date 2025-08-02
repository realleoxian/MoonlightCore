package de.leoxian.moonlightcore.api.util;

import java.util.HashMap;

public abstract class Easing {
    public static final HashMap<String, Easing> EASINGS = new HashMap<>();

    private final String name;

    public Easing(String name) {
        this.name = name;
        EASINGS.put(name, this);
    }

    /**
     * @param t The time
     * @param b The beginning value
     * @param c The value changed
     * @param d The duration timme
     * @return The eased value
     */
    public abstract float ease(float t, float b, float c, float d);

    public String name() {
        return this.name;
    }

    /**
     * Simple linear tweening - No easing
     */
    public static final Easing LINEAR = new Easing("linear") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return c * t / d + b;
        }
    };

    // ---------------[ QUADRATIC EASING: t^2 ]--------------------

    /**
     * Quadratic easing in - Accelerating from zero velocity
     */
    public static final Easing QUAD_IN = new Easing("quadin") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return c * (t /= d) * t + b;
        }
    };

    /**
     * Quadratic easing out - Decelerating to zero velocity
     */
    public static final Easing QUAD_OUT = new Easing("quadout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return -c * (t /= d) * (t - 2) + b;
        }
    };

    /**
     * Quadratic easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing QUAD_IN_OUT = new Easing("quadinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            if((t /= d / 2) < 1) {
                return c /2 * t * t + b;
            } else  {
                return -c / 2 * ((--t) * (t - 2) - 1) + b;
            }
        }
    };

    // ---------------[ CUBIC EASING: t^3 ]--------------------

    /**
     * Cubic easing in - Accelerating from zero velocity
     */
    public static final Easing CUBIC_IN = new Easing("cubicin") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return c * (t /= d) * t * t + b;
        }
    };

    /**
     * Cubic easing out - Decelerating to zero velocity
     */
    public static final Easing CUBIC_OUT = new Easing("cubicout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return c * ((t = t / d - 1) * t * t + 1) + b;
        }
    };

    /**
     * Cubic easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing CUBIC_IN_OUT = new Easing("cubicinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            if((t /= d / 2) < 1) {
                return c / 2 * t * t * t + b;
            } else {
                return c / 2 * ((t -= 2) * t * t + 2) + b;
            }
        }
    };

    // ---------------[ QUARTIC EASING: t^4 ]--------------------

    /**
     * Quartic easing in - Accelerating from zero velocity
     */
    public static final Easing QUART_IN = new Easing("quartin") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return -c * ((t = t / d - 1) * t * t * t - 1) + b;
        }
    };

    /**
     * Quartic easing out - Decelerating to zero velocity
     */
    public static final Easing QUART_OUT = new Easing("quartout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return c * ((t = t / d - 1) * t * t * t + 1) + b;
        }
    };

    /**
     * Quartic easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing QUART_IN_OUT = new Easing("quartinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            if((t /= d / 2) < 1) {
                return c / 2 * t * t * t * t + b;
            } else {
                return c / 2 * ((t -= 2) * t * t * t + 2) + b;
            }
        }
    };

    // ---------------[ QUINTIC EASING: t^5 ]--------------------

    /**
     * Quintic easing in - Accelerating from zero velocity
     */
    public static final Easing QUINT_IN = new Easing("quintin") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return -c * ((t = t / d - 1) * t * t * t * t - 1) + b;
        }
    };

    /**
     * Quintic easing out - Decelerating to zero velocity
     */
    public static final Easing QUINT_OUT = new Easing("quintout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
        }
    };

    /**
     * Quintic easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing QUINT_IN_OUT = new Easing("quintinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            if((t /= d / 2) < 1) {
                return c / 2 * t * t * t * t * t + b;
            } else {
                return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
            }
        }
    };

    // --------------------[ SINUSOIDAL EASING: sin(t) ]--------------------

    /**
     * Sinusoidal easing in - Accelerating from zero velocity
     */
    public static final Easing SINE_IN = new Easing("sinein") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (float) (-c * Math.cos(t / d * (Math.PI / 2)) + c + b);
        }
    };

    /**
     * Sinusoidal easing out - Decelerating to zero velocity
     */
    public static final Easing SINE_OUT = new Easing("sineout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (float) (c * Math.sin(t / d * (Math.PI)) + b);
        }
    };

    /**
     * Sinusoidal easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing SINE_IN_OUT = new Easing("sineinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (float) (-c / 2 * (Math.cos(Math.PI * t / d) - 1) + b);
        }
    };

    // --------------------[ EXPONENTIAL EASING: 2^t ]--------------------

    /**
     * Exponential easing in - Accelerating from zero velocity
     */
    public static final Easing EXPO_IN = new Easing("expoin") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (t == 0) ? b : (float) (c * Math.pow(2, 10 * (t / d - 1)) + b);
        }
    };

    /**
     * Exponential easing out - Decelerating to zero velocity
     */
    public static final Easing EXPO_OUT = new Easing("expoout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (t == d) ? b + c : (float) (c * (-Math.pow(2, -10 * t / d) + 1) + b);
        }
    };

    /**
     * Exponential easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing EXPO_IN_OUT = new Easing("expoinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            if(t == 0) {
                return b;
            }

            if(t == d) {
                return b + c;
            }

            if((t /= d /  2) < 1) {
                return (float) (c / 2 * Math.pow(2, 10 * (t - 1)) + b);
            } else {
                return (float) (c / 2 * (-Math.pow(2, -10 * --t) + 2) + b);
            }
        }
    };

    // --------------------[ CIRCULAR EASING: 2^t ]--------------------

    /**
     * Circular easing in - Accelerating from zero velocity
     */
    public static final Easing CIRC_IN = new Easing("circin") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (float) (-c * (Math.sqrt(1 - (t /= d) * t) - 1) + b);
        }
    };

    /**
     * Circular easing out - Decelerating to zero velocity
     */
    public static final Easing CIRC_OUT = new Easing("circout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            return (float) (c * Math.sqrt(1 - (t = t / d - 1) * t) + b);
        }
    };

    /**
     * Circular easing in/out - Accelerating until halfway to then decelerate
     */
    public static final Easing CIRC_IN_OUT = new Easing("circinout") {
        @Override
        public float ease(float t, float b, float c, float d) {
            if((t /= d / 2) < 1) {
                return (float) (-c * (Math.sqrt(1 - (t /= d) * t) - 1) + b);
            } else {
                return (float) (c * Math.sqrt(1 - (t = t / d - 1) * t) + b);
            }
        }
    };
}
