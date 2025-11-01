package de.leoxian.moonlightcore.transfer.transaction;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

public final class Transaction implements TransactionContext, AutoCloseable {

    public static Transaction openRoot() {
        return TransactionManager.getThreadManager().open(null);
    }

    public static Transaction open(@Nullable TransactionContext parent) {
        return TransactionManager.getThreadManager().open(parent);
    }

    @Nullable
    public static Transaction getCurrentUnsafe() {
        TransactionManager manager = TransactionManager.getThreadManager();
        int currentDepth = manager.currentDepth;

        if(currentDepth == -1) {
            return null;
        } else if(manager.stack.get(currentDepth).open) {
            return manager.stack.get(currentDepth);
        }

        throw new IllegalStateException("May not call getCurrentUnsafe() from a close callback");
    }

    public static Lifecycle getLifecycle() {
        TransactionManager manager = TransactionManager.getThreadManager();
        int currentDepth = manager.currentDepth;

        if(currentDepth == -1) {
            return manager.processingRootCallbacksQueue ? Lifecycle.ROOT_CLOSING : Lifecycle.NONE;
        } else {
            return manager.stack.get(currentDepth).open ? Lifecycle.OPEN : Lifecycle.CLOSING;
        }
    }

    final Queue<SnapshotJournal<?>> closeSnapshotJournals = new ArrayDeque<>();

    final TransactionManager manager;
    private final int nestingDepth;

    boolean open = false;

    Transaction(TransactionManager manager, int nestingDepth) {
        this.manager = manager;
        this.nestingDepth = nestingDepth;
    }

    public void commit() {
        this.close(false);
    }

    public void abort() {
        this.close(true);
    }

    @Override
    public Transaction openNested() {
        return this.manager.open(this);
    }

    @Override
    public Transaction getOpenTransaction(int nestingDepth) {
        return this.manager.getOpenTransaction(nestingDepth);
    }

    @Override
    public int nestingDepth() {
        this.manager.validateThread();
        return this.nestingDepth;
    }

    @Override
    public void close() {
        if(this.manager.currentDepth > -1 && this.open) {
            this.abort();
        }
    }

    private void close(boolean wasAborted) {
        this.manager.validateThread();
        this.validateOpen();
        this.open = false;

        RuntimeException closeException = null;

        while(!this.closeSnapshotJournals.isEmpty()) {
            SnapshotJournal<?> journal = this.closeSnapshotJournals.remove();

            try {
                journal.onClose(this, wasAborted);
            } catch (Exception e) {
                if(closeException == null) {
                    closeException = new RuntimeException("Encountered an exception while invoking a transaction close callback", e);
                }

                closeException.addSuppressed(e);
            }
        }

        if(this.manager.currentDepth == -1) {
            closeException = this.manager.processRootClosingCallbacks(closeException);
        }


        this.manager.currentDepth--;
        if(closeException != null) {
            throw closeException;
        }
    }

    void validateOpen() {
        if(!this.open) {
            throw new IllegalStateException("Transaction operation cannot be applied to a closed transaction");
        }
    }
}
