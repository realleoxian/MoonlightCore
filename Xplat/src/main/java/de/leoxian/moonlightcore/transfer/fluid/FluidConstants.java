package de.leoxian.moonlightcore.transfer.fluid;

public class FluidConstants {
    public static final int BUCKET = 81000;
    public static final int BOTTLE = 27000;
    public static final int BOWL = 27000;
    public static final int BLOCK = 81000;
    public static final int INGOT = 9000;
    public static final int NUGGET = 1000;
    public static final int DROPLET = 1;

    public static int fromBucketFraction(int numerator, int denominator) {
        int total = numerator * BUCKET;

        if(total % denominator != 0) {
            throw new IllegalArgumentException("Ot a valid number of droplets");
        }

        return total / denominator;
    }

    private FluidConstants() {}
}
