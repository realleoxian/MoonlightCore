package de.leoxian.moonlightcore.levelgen.noise;

import net.minecraft.util.LinearCongruentialGenerator;

public class AreaContext {
    private static final int MAX_CACHE = 1024;

    private final int maxCache;
    private final long seed;
    private long rval;

    public AreaContext(int maxCache, long seed, long seedModifier) {
        this.seed = mixSeed(seed, seedModifier);
        this.maxCache = maxCache;
    }

    public Area createArea(PixelTransformer transformer) {
        return new Area(transformer, this.maxCache);
    }

    public Area createArea(PixelTransformer transformer, Area area) {
        return new Area(transformer, Math.min(MAX_CACHE, area.maxCache() * 4));
    }

    public void initRandom(long x, long z) {
        long i = this.seed;
        i = LinearCongruentialGenerator.next(i, x);
        i = LinearCongruentialGenerator.next(i, z);
        i = LinearCongruentialGenerator.next(i, x);
        i = LinearCongruentialGenerator.next(i, z);

        this.rval = i;
    }

    public int nextRandom(int bound) {
        int i = Math.floorMod(this.rval >> 24, bound);
        this.rval = LinearCongruentialGenerator.next(this.rval, this.seed);

        return i;
    }

    public int random(int a, int b) {
        return this.nextRandom(2) == 0 ? a : b;
    }

    public int random(int a, int b, int c, int d) {
        int i = this.nextRandom(4);

        if(i == 0) {
            return a;
        } else if(i == 1) {
            return b;
        } else {
            return i == 2 ? c : d;
        }
    }

    private static long mixSeed(long seed, long modifier) {
        long i = LinearCongruentialGenerator.next(modifier, modifier);
        i = LinearCongruentialGenerator.next(i, modifier);
        i = LinearCongruentialGenerator.next(i, modifier);

        long j = LinearCongruentialGenerator.next(seed, i);
        j = LinearCongruentialGenerator.next(j, i);

        return LinearCongruentialGenerator.next(j, i);
    }
}
