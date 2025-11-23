package de.leoxian.moonlightcore.transfer.transaction;

import de.leoxian.moonlightcore.util.nullness.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class TransactionManager {
    private static final ThreadLocal<TransactionManager> MANAGER = ThreadLocal.withInitial(TransactionManager::new);

    static TransactionManager getThreadManager() {
        return MANAGER.get();
    }

    final Thread thread = Thread.currentThread();

    final List<Transaction> stack = new ArrayList<>();
    int currentDepth = -1;

    final Queue<SnapshotJournal<?>> rootCloseSnapshotJournals = new ArrayDeque<>();
    boolean processingRootCallbacksQueue = false;

    Transaction open(@Nullable TransactionContext parent, Class<?> callerClass) {
        this.validateThread();

        if(parent != null) {
            var tx = (Transaction) parent;
            this.validateTransaction(tx);
            tx.validateOpen();
        } else if (this.currentDepth > -1) {
            String errorMessage = String.format(
                    "A root transaction of '%s' is already active on this thread (%s) when '%s' tried to open.",
                    getOpenTransaction(0).getDebugName(),
                    thread.getName(),
                    callerClass.getName()
            );

            throw new IllegalStateException(errorMessage);
        }

        this.currentDepth++;
        if(this.stack.size() == currentDepth) {
            this.stack.add(new Transaction(this, this.currentDepth, callerClass));
        }

        Transaction tx = this.stack.get(this.currentDepth);
        tx.open = true;
        return tx;
    }

    Transaction getOpenTransaction(int nestingDepth) {
        this.validateThread();

        if(nestingDepth < 0) {
            throw new IllegalStateException("Nesting depth may NOT be negative");
        } else if(nestingDepth > this.currentDepth) {
            throw new IllegalStateException("There is no open transaction at nesting depth " + nestingDepth);
        }

        Transaction tx = this.stack.get(nestingDepth);
        tx.validateOpen();

        return tx;
    }

    RuntimeException processRootClosingCallbacks(RuntimeException exception) {
        if(this.processingRootCallbacksQueue) {
            return exception;
        }
        this.processingRootCallbacksQueue = true;

        while(!this.rootCloseSnapshotJournals.isEmpty()) {
            SnapshotJournal<?> journal = this.rootCloseSnapshotJournals.remove();

            try {
                journal.onRootClose();
            } catch (Exception e) {
                if(exception == null) {
                    exception = new RuntimeException("Encountered an exception while invoking a transaction root close callback", e);
                }

                exception.addSuppressed(e);
            }
        }

        this.processingRootCallbacksQueue = false;
        return exception;
    }

    void validateTransaction(Transaction tx) {
        this.validateThread();

        if(currentDepth == -1 || this.stack.get(this.currentDepth) != tx) {
            String errorMessage = String.format(
                    "Transaction function was called on a transaction (%s) with depth %d, but the current transaction (%s) has depth %d",
                    tx.getDebugName(),
                    tx.nestingDepth(),
                    this.getOpenTransaction(currentDepth).getDebugName(),
                    this.currentDepth
            );

            throw new IllegalStateException(errorMessage);
        }
    }

    void validateThread() {
        if(Thread.currentThread() != this.thread) {
            String errorMessage = String.format(
                    "Attempted to access to a transaction that is on thread '%s' from thread '%s'",
                    this.thread.getName(),
                    Thread.currentThread().getName()
            );

            throw new IllegalStateException(errorMessage);
        }
    }

    private TransactionManager() {}
}
