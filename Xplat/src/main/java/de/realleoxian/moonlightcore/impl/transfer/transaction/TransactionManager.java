package de.realleoxian.moonlightcore.impl.transfer.transaction;

import de.realleoxian.moonlightcore.api.transfer.transaction.Transaction;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionLifecycle;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class TransactionManager {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final ThreadLocal<TransactionManager> MANAGER = ThreadLocal.withInitial(TransactionManager::new);

    public static Transaction openRoot() {
        return open(null);
    }

    public static Transaction open(@Nullable TransactionContext parent) {
        return getManager().open(parent, STACK_WALKER.getCallerClass());
    }

    public static @Nullable Transaction getCurrentUnsafe() {
        TransactionManager manager = getManager();
        int currentDepth = manager.currentDepth;

        if(currentDepth == -1) {
            return null;
        } else if (manager.stack.get(currentDepth).open) {
            return manager.stack.get(currentDepth);
        }

        throw new IllegalStateException("May not call 'getCurrentUnsafe()' from a close callback");
    }

    public static TransactionLifecycle getLifecycle() {
        TransactionManager manager = getManager();
        int currentDepth = manager.currentDepth;

        if(currentDepth == -1) {
            return manager.processingRootClosingCallbacks ? TransactionLifecycle.ROOT_CLOSING : TransactionLifecycle.NONE;
        } else {
            return manager.stack.get(currentDepth).open ? TransactionLifecycle.OPEN : TransactionLifecycle.CLOSING;
        }
    }

    private static TransactionManager getManager() {
        return MANAGER.get();
    }

    private final Thread currentThread = Thread.currentThread();
    private final List<TransactionImpl> stack = new ArrayList<>();
    private int currentDepth = -1;
    private final Queue<TransactionContext.RootCloseCallback> rootCloseCallbacks = new ArrayDeque<>();
    private boolean processingRootClosingCallbacks = false;

    private TransactionManager() {}

    private Transaction open(@Nullable TransactionContext parent, Class<?> callerClass) {
        validateThread();

        if(parent != null) {
            TransactionImpl tx = (TransactionImpl) parent;
            validateTransaction(tx);
            tx.validateOpen();
        } else if (currentDepth > -1) {
            String errorMessage = String.format(
                    "A transaction was already opened from '%s' on this thread ('%s') when '%s' tried to open other",
                    getOpenTransaction(0).getDebugName(),
                    currentThread.getName(),
                    callerClass.getName()
            );

            throw new IllegalStateException(errorMessage);
        }

        currentDepth++;
        if(stack.size() == currentDepth) {
            stack.add(new TransactionImpl(currentDepth, callerClass));
        }

        TransactionImpl tx = stack.get(currentDepth);
        tx.open = true;
        return tx;
    }

    private Transaction getOpenTransaction(int depth) {
        validateThread();

        if(depth < 0) {
            throw new IllegalStateException("Transaction depth may not be negative");
        } else if (depth > currentDepth) {
            throw new IllegalStateException("No transaction at depth %d its available".formatted(depth));
        }

        TransactionImpl tx = stack.get(depth);
        tx.validateOpen();
        return tx;
    }

    private RuntimeException processRootClosingCallback(@Nullable RuntimeException exception, boolean wasAborted) {
        if(processingRootClosingCallbacks) {
            return exception;
        }
        processingRootClosingCallbacks = true;

        while(!rootCloseCallbacks.isEmpty()) {
            TransactionContext.RootCloseCallback callback = rootCloseCallbacks.remove();

            try {
                callback.onRootClose(wasAborted);
            } catch (Exception e) {
                if(exception == null) {
                    exception = new RuntimeException("Encountered an error invoking root transaction close callbacks");
                }

                exception.addSuppressed(e);
            }
        }

        processingRootClosingCallbacks = false;
        return exception;
    }

    private void validateTransaction(Transaction transaction) {
        validateThread();

        if(currentDepth == -1 || stack.get(currentDepth) != transaction) {
            String errorMessage = String.format(
                    "Attempted to perform a transaction operation (%s) with depth %d, but current transaction (%s) its on depth %d",
                    transaction.getDebugName(), transaction.nestingDepth(),
                    getOpenTransaction(currentDepth).getDebugName(), getOpenTransaction(currentDepth).nestingDepth()

            );

            throw new IllegalStateException(errorMessage);
        }
    }

    private void validateThread() {
        if(Thread.currentThread() != currentThread) {
            String errorMessage = String.format(
                    "Attempted to access to a transaction that its on thread '%s' from thread '%s'",
                    Thread.currentThread().getName(),
                    currentThread.getName()
            );

            throw new IllegalStateException(errorMessage);
        }
    }

    private final class TransactionImpl implements Transaction {
        private final Queue<CloseCallback> closeCallbacks = new ArrayDeque<>();

        private final int nestingDepth;
        private final Class<?> callerClass;
        private boolean open;

        private TransactionImpl(int nestingDepth, Class<?> callerClass) {
            this.nestingDepth = nestingDepth;
            this.callerClass = callerClass;
        }

        @Override
        public void commit() {
            close(false);
        }

        @Override
        public void abort() {
            close(true);
        }

        @Override
        public void close() {
            if(currentDepth > -1 && open) {
                abort();
            }
        }

        @Override
        public String getDebugName() {
            return callerClass.getName();
        }

        @Override
        public void addCloseCallback(CloseCallback callback) {
            validateThread();
            validateOpen();
            closeCallbacks.add(callback);
        }

        @Override
        public void addRootCloseCallback(RootCloseCallback callback) {
            validateThread();

            if(currentDepth == -1) {
                throw new IllegalStateException("No transaction available on this thread");
            }

            rootCloseCallbacks.add(callback);
        }

        @Override
        public Transaction getOpenTransaction(int depth) {
            validateThread();
            return TransactionContext.this.getOpenTransaction(depth);
        }

        @Override
        public Transaction openNested() {
            validateTransaction(this);
            return open(this, callerClass);
        }

        @Override
        public int nestingDepth() {
            validateThread();
            return nestingDepth;
        }

        private void close(boolean wasAborted) {
            validateTransaction(this);
            validateOpen();
            open = false;

            RuntimeException closeErrors = null;
            while(!closeCallbacks.isEmpty()) {
                CloseCallback callback = closeCallbacks.remove();

                try {
                    callback.onClose(this, wasAborted);
                } catch (Exception e) {
                    if(closeErrors == null) {
                        closeErrors = new RuntimeException("Encountered an error invoking a transaction close callbacks");
                    }

                    closeErrors.addSuppressed(e);
                }
            }

            if(currentDepth == -1) {
                closeErrors = processRootClosingCallback(closeErrors, wasAborted);
            }

            currentDepth--;
            if(closeErrors != null) {
                throw closeErrors;
            }
        }

        void validateOpen() {
            if(!open) {
                throw new IllegalStateException("Attempted to perform transactional operations on a closed transaction");
            }
        }
    }
}
