package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

public class TransactionalRandom extends SnapshotJournal<Long> {
    private long seed = RandomSupport.generateUniqueSeed();

    public double nextDouble(TransactionContext ctx) {
        updateSnapshots(ctx);

        var random = new SingleThreadedRandomSource(this.seed);
        double rand = random.nextDouble();
        seed = random.nextLong();

        return rand;
    }

    @Override
    public Long createSnapshot() {
        return this.seed;
    }

    @Override
    public void revertToSnapshot(Long snapshot) {
        this.seed = snapshot;
    }
}
