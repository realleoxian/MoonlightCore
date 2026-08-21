package de.leoxian.moonlightcore.internal.common.transfer.transaction;

import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.common.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class TransactionManager {
    private static final ThreadLocal<TransactionManager> MANAGER = ThreadLocal.withInitial(TransactionManager::new);

    public static Transaction open(final @Nullable TransactionContext parent) {
        return get().openTransaction(parent);
    }

    public static Transaction openRoot() {
        return open(null);
    }

    public static TransactionContext.Lifecycle getLifecycle() {
        TransactionManager manager = get();
        manager.validateThread();

        if (manager.currentDepth >= 0 && manager.stack.get(manager.currentDepth) != null) {
            return manager.stack.get(manager.currentDepth).open ? TransactionContext.Lifecycle.OPEN : TransactionContext.Lifecycle.CLOSED;
        }
        return manager.processingRootCloseCallbacks ? TransactionContext.Lifecycle.ROOT_CLOSING : TransactionContext.Lifecycle.NONE;
    }

    public static TransactionManager get() {
        return MANAGER.get();
    }

    final Thread currentThread = Thread.currentThread();
    private final List<TransactionImpl> stack = new ArrayList<>();
    final Deque<TransactionContext.RootCloseCallback> rootCloseCallbacks = new ArrayDeque<>();
    private boolean processingRootCloseCallbacks;
    int currentDepth = -1;

    Transaction openTransaction(@Nullable TransactionContext parent) {
        validateThread();

        if (parent != null) {
            TransactionImpl transaction = validateTransaction(parent);
            validateOpen(transaction);
        }

        int depth = ++this.currentDepth;
        TransactionImpl transaction = new TransactionImpl(this, depth);
        if (depth < this.stack.size()) {
            this.stack.set(depth, transaction);
        } else {
            this.stack.add(transaction);
        }
        return transaction;
    }

    TransactionImpl getOpenTransaction(int depth) {
        validateThread();
        if (depth < 0) throw new IndexOutOfBoundsException("May not access a transaction at a negative depth");
        if (depth > this.currentDepth || depth >= this.stack.size()) throw new IndexOutOfBoundsException("May not access a transaction outside the current transaction stack");

        TransactionImpl transaction = this.stack.get(depth);
        validateOpen(transaction);
        return transaction;
    }

    @Nullable
    RuntimeException processRootCloseCallbacks(@Nullable RuntimeException exception, boolean wasAborted) {
        if (this.processingRootCloseCallbacks) return exception;

        this.processingRootCloseCallbacks = true;
        try {
            TransactionContext.RootCloseCallback callback;
            while ((callback = this.rootCloseCallbacks.poll()) != null) {
                try {
                    callback.onRootClose(wasAborted);
                } catch (Exception e) {
                    if (exception == null) {
                        exception = new RuntimeException("Unhandled exceptions were found processing root close callbacks");
                    }
                    exception.addSuppressed(e);
                }
            }
        } finally {
            this.processingRootCloseCallbacks = false;
        }
        return exception;
    }

    void validateThread() {
        if (this.currentThread != Thread.currentThread()) {
            throw new IllegalStateException("May not perform transaction operations that belong to %s from %s".formatted(this.currentThread.getName(), Thread.currentThread().getName()));
        }
    }

    public TransactionImpl validateTransaction(TransactionContext context) {
        if (!(context instanceof TransactionImpl transaction)) {
            throw new IllegalArgumentException("Cannot validate foreign transaction implementations");
        }

        validateTransaction(transaction);
        return transaction;
    }

    void validateTransaction(TransactionImpl transaction) {
        if (this.currentDepth != transaction.depth()) {
            throw new IllegalStateException("May not perform transaction operations on a non-current transaction");
        }

        if (this.stack.get(this.currentDepth) != transaction) {
            throw new IllegalStateException("Transaction is not the transaction currently occupying its stack depth");
        }
    }

    public void validateOpen(TransactionImpl transaction) {
        if (!transaction.open) {
            throw new IllegalStateException("May not perform transaction operations on a closed transaction");
        }
    }
}
