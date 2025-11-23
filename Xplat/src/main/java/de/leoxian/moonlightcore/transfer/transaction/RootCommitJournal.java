package de.leoxian.moonlightcore.transfer.transaction;

import de.leoxian.moonlightcore.util.nullness.Nullable;

public class RootCommitJournal extends SnapshotJournal<@Nullable Void> {

    private final Runnable action;

    public RootCommitJournal(Runnable action) {
        this.action = action;
    }

    @Override
    public void onRootCommit(@Nullable Void originalState) {
        this.action.run();
    }

    @Override
    public @Nullable Void createSnapshot() {
        return null;
    }

    @Override
    public void revertToSnapshot(@Nullable Void snapshot) {

    }

}
