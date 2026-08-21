package de.leoxian.moonlightcore.internal.common.transfer.transaction;

import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;

import java.util.ArrayDeque;
import java.util.Queue;

public final class TransactionImpl implements Transaction {
    final TransactionManager transactionManager;
    final Queue<CloseCallback> closeCallbacks = new ArrayDeque<>();
    final int depth;
    boolean open;

    TransactionImpl(TransactionManager transactionManager, int depth) {
        this.transactionManager = transactionManager;
        this.depth = depth;
    }

    @Override
    public void addCloseCallback(CloseCallback closeCallback) {
        this.transactionManager.validateOpen(this);
        this.closeCallbacks.add(closeCallback);
    }

    @Override
    public void commit() {
        this.transactionManager.validateThread();
        this.transactionManager.validateTransaction(this);
        this.transactionManager.validateOpen(this);
        close(false);
    }

    @Override
    public void abort() {
        this.transactionManager.validateThread();
        this.transactionManager.validateTransaction(this);
        this.transactionManager.validateOpen(this);
        close(true);
    }

    @Override
    public void close() {
        if (this.open && this.transactionManager.currentDepth > -1) {
            abort();
        }
    }

    @Override
    public void addRootCloseCallback(RootCloseCallback rootCloseCallback) {
        this.transactionManager.validateThread();
        this.transactionManager.rootCloseCallbacks.add(rootCloseCallback);
    }

    @Override
    public Transaction openNested() {
        this.transactionManager.validateThread();
        this.transactionManager.validateOpen(this);
        return this.transactionManager.openTransaction(this);
    }

    @Override
    public Transaction getOpenTransaction(int depth) {
        this.transactionManager.validateThread();
        this.transactionManager.validateOpen(this);
        return this.transactionManager.getOpenTransaction(depth);
    }

    @Override
    public int depth() {
        this.transactionManager.validateThread();
        return this.depth;
    }

    private void close(boolean wasAborted) {
        if (!this.open) return;
        this.open = false;

        RuntimeException exception = null;
        CloseCallback callback;
        while ((callback = this.closeCallbacks.poll()) != null) {
            try {
                callback.onTransactionClose(this, wasAborted);
            } catch (Exception e) {
                if (exception == null) {
                    exception = new RuntimeException("Unhandled exceptions were found processing close callbacks");
                }
                exception.addSuppressed(e);
            }
        }

        this.transactionManager.currentDepth--;
        if (this.transactionManager.currentDepth == -1) {
            exception = this.transactionManager.processRootCloseCallbacks(exception, wasAborted);
        }

        if (exception != null) {
            throw exception;
        }
    }
}
