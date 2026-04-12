package de.realleoxian.moonlightcore.api.transfer.transaction;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SnapshotJournal<@Nullable T> implements TransactionContext.CloseCallback, TransactionContext.RootCloseCallback {
    private static final Object NO_SNAPSHOT = new Object();

    private final List<T> snapshots = new ArrayList<>();
    private T originalState = null;

    public abstract T createSnapshot();

    public abstract void revertToSnapshot(T snapshot);

    public void onRootCommit(T originalState) {

    }

    public void releaseSnapshot(T snapshot) {

    }

    @SuppressWarnings("unchecked")
    public void updateSnapshots(TransactionContext transaction) {
        int depth = transaction.nestingDepth();

        for(int i = snapshots.size(); i <= depth; i++) {
            snapshots.add((T) NO_SNAPSHOT);
        }

        if(snapshots.get(depth) == NO_SNAPSHOT) {
            snapshots.set(depth, createSnapshot());
            transaction.addCloseCallback(this);
        }
    }

    @Override
    public void onClose(TransactionContext transaction, boolean wasAborted) {
        int depth = transaction.nestingDepth();
        T snapshot = snapshots.remove(depth);

        if(wasAborted) {
            revertToSnapshot(snapshot);
            releaseSnapshot(snapshot);
        } else if (depth <= 0) {
            if(originalState == null) {
                originalState = snapshot;
                transaction.addRootCloseCallback(this);
            } else {
                releaseSnapshot(snapshot);
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
