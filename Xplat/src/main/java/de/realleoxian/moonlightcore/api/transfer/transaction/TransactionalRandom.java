package de.realleoxian.moonlightcore.api.transfer.transaction;

import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

public final class TransactionalRandom extends SnapshotJournal<Long> {
    private long seed = RandomSupport.generateUniqueSeed();

    public double nextDouble(TransactionContext tx) {
        updateSnapshots(tx);

        SingleThreadedRandomSource random = new SingleThreadedRandomSource(seed);
        double ret = random.nextDouble();
        seed = random.nextLong();

        return ret;
    }

    @Override
    public Long createSnapshot() {
        return seed;
    }

    @Override
    public void revertToSnapshot(Long snapshot) {
        seed = snapshot;
    }
}
