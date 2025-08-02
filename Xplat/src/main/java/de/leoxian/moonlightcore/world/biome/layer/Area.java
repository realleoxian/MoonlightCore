package de.leoxian.moonlightcore.world.biome.layer;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

import java.util.Arrays;
import java.util.concurrent.locks.StampedLock;

public final class Area {
    private final StampedLock lock = new StampedLock();
    private final PixelTransformer transformer;
    private final long[] keys;
    private final int[] values;
    private final int mask;

    Area(PixelTransformer transformer, int size) {
        this.transformer = transformer;
        size = Mth.smallestEncompassingPowerOfTwo(size);

        this.keys = new long[size];
        Arrays.fill(this.keys, Long.MIN_VALUE);
        this.values = new int[size];
        this.mask = size - 1;
    }

    public int get(int x, int z) {
        long key = ChunkPos.asLong(x, z);
        int index = (int) HashCommon.mix(key) & this.mask;
        long stamp = this.lock.readLock();

        if(this.keys[index] == key) {
            int value = this.values[index];
            this.lock.unlockRead(stamp);

            return value;
        } else {
            this.lock.unlockRead(stamp);
            stamp = this.lock.writeLock();

            int value = this.transformer.apply(x, z);
            this.keys[index] = key;
            this.values[index] = value;
            this.lock.unlockWrite(stamp);

            return value;
        }
    }

    public int maxCache() {
        return this.mask + 1;
    }
}
