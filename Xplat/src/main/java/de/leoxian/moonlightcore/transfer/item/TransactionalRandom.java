package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TransactionalRandom extends SnapshotJournal<Long> {
    private long seed = RandomSupport.generateUniqueSeed();

    @Override
    public Long createSnapshot() {
        return seed;
    }

    @Override
    public void revertToSnapshot(Long snapshot) {
        seed = snapshot;
    }

    public double nextDouble(TransactionContext ctx) {
        updateSnapshots(ctx);

        var random = new SingleThreadedRandomSource(this.seed);
        double rand = random.nextDouble();
        seed = random.nextLong();

        return rand;
    }
}
