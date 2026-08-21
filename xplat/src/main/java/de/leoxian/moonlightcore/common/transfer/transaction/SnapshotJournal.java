package de.leoxian.moonlightcore.common.transfer.transaction;

import de.leoxian.moonlightcore.internal.common.transfer.transaction.TransactionImpl;
import de.leoxian.moonlightcore.internal.common.transfer.transaction.TransactionManager;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public abstract class SnapshotJournal<T extends @Nullable Object> implements TransactionContext.RootCloseCallback, Transaction.CloseCallback {
    private static final Object NO_SNAPSHOT = new Object();

    private final ArrayList<T> snapshots = new ArrayList<>();
    private @Nullable T originalState = null;

    protected abstract T createSnapshot();

    protected abstract void readSnapshot(T snapshot);

    protected void releaseSnapshot(T snapshot) {
    }

    protected void onRootCommit(T originalState) {

    }

    public void updateSnapshots(final TransactionContext transaction) {
        int depth = transaction.depth();
        snapshots.ensureCapacity(depth);
        for (int i = snapshots.size(); i < depth; i++) {
            snapshots.add((T) NO_SNAPSHOT);
        }

        if (snapshots.get(depth) == NO_SNAPSHOT) {
            snapshots.set(depth, createSnapshot());

            TransactionImpl impl = TransactionManager.get().validateTransaction(transaction);
            TransactionManager.get().validateOpen(impl);
        }
    }

    @Override
    public void onTransactionClose(TransactionContext transaction, boolean wasAborted) {
        int depth = transaction.depth();
        T snapshot = this.snapshots.remove(depth);

        if (wasAborted){
            readSnapshot(snapshot);
            releaseSnapshot(snapshot);
        } else if (depth <= 0) {
            if (originalState == null) {
                originalState = snapshot;
                transaction.addRootCloseCallback(this);
            } else {
                readSnapshot(snapshot);
            }
        } else if (snapshots.get(depth - 1) == NO_SNAPSHOT) {
            snapshots.set(depth - 1, snapshot);
            transaction.getOpenTransaction(depth - 1).addCloseCallback(this);
        } else {
            releaseSnapshot(snapshot);
        }
    }

    @Override
    public void onRootClose(boolean wasAborted) {
        T originalState = this.originalState;
        this.originalState = null;

        onRootCommit(originalState);
        releaseSnapshot(originalState);
    }
}
