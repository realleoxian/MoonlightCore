package de.leoxian.moonlightcore.transfer.transaction;

import de.leoxian.moonlightcore.util.nullness.NullableType;

import java.util.ArrayList;

public abstract class SnapshotJournal<@NullableType T> {
    private static final Object NO_SNAPSHOT = new Object();

    private final ArrayList<T> snapshots = new ArrayList<>();
    protected T originalState = null;

    public abstract T createSnapshot();

    public abstract void revertToSnapshot(T snapshot);

    public void releaseSnapshot(T snapshot) {}

    public void onRootCommit(T originalState) {}

    @SuppressWarnings("unchecked")
    public void updateSnapshots(TransactionContext ctx) {
        int depth = ctx.nestingDepth();

        for(int i = this.snapshots.size(); i < depth; i++) {
            this.snapshots.add((T) NO_SNAPSHOT);
        }

        if(this.snapshots.get(depth) == NO_SNAPSHOT) {
            this.snapshots.set(depth, this.createSnapshot());

            ((Transaction) ctx).validateOpen();
            ((Transaction) ctx).closeSnapshotJournals.add(this);
        }
    }

    public void onClose(Transaction tx, boolean wasAborted) {
        int depth = tx.nestingDepth();
        T snapshot = this.snapshots.remove(depth);

        if(wasAborted) {
            this.revertToSnapshot(snapshot);
            this.releaseSnapshot(snapshot);
        } else if (depth <= 0) {
            if(this.originalState == null) {
                this.originalState = snapshot;
                tx.manager.rootCloseSnapshotJournals.add(this);
            } else {
                this.releaseSnapshot(snapshot);
            }
        } else if (snapshots.get(depth - 1) == NO_SNAPSHOT){
            this.snapshots.set(depth - 1, snapshot);
            tx.manager.getOpenTransaction(depth - 1).closeSnapshotJournals.add(this);
        } else {
            this.releaseSnapshot(snapshot);
        }
    }

    public void onRootClose() {
        T originalState = this.originalState;
        this.originalState = null;

        this.onRootCommit(originalState);
        this.releaseSnapshot(originalState);
    }
}
