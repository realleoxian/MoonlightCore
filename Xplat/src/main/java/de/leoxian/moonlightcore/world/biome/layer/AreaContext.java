package de.leoxian.moonlightcore.world.biome.layer;

import net.minecraft.util.LinearCongruentialGenerator;

public final class AreaContext {
    private static final int MAX_CACHE = 1024;

    private static long mixSeed (long seed, long seedModifier) {
        long i = LinearCongruentialGenerator.next(seedModifier, seedModifier);
        i = LinearCongruentialGenerator.next(i, seedModifier);
        i = LinearCongruentialGenerator.next(i, seedModifier);

        long j = LinearCongruentialGenerator.next(seed, i);
        j = LinearCongruentialGenerator.next(j, i);
        return LinearCongruentialGenerator.next(j, i);
    }

    private final long seed;
    private final int maxCache;
    private long returnValue;

    AreaContext(long seed, long seedModifier, int maxCache) {
        this.seed = mixSeed(seed, seedModifier);
        this.maxCache = maxCache;
    }

    public void initializeRandom(int x, int z) {
        long i = this.seed;
        i = LinearCongruentialGenerator.next(i, x);
        i = LinearCongruentialGenerator.next(i, z);
        i = LinearCongruentialGenerator.next(i, x);
        i = LinearCongruentialGenerator.next(i, z);

        this.returnValue = i;
    }

    public int nextRandom(int bound) {
        int i = Math.floorMod(this.returnValue >> 24, bound);
        this.returnValue = LinearCongruentialGenerator.next(this.returnValue, this.seed);

        return i;
    }

    public int random(int a, int b) {
        return this.nextRandom(2) == 0 ? a : b;
    }

    public int random (int a, int b, int c, int d) {
        int ran = this.nextRandom(4);

        if (ran == 0) return a;
        else if (ran == 1) return b;
        else return ran == 2 ? c : d;
    }

    public Area createResult(PixelTransformer transformer) {
        return new Area(transformer, this.maxCache);
    }

    public Area createResult(PixelTransformer transformer, Area area) {
        return new Area(transformer, Math.min(MAX_CACHE, area.maxCache() * 4));
    }
}
