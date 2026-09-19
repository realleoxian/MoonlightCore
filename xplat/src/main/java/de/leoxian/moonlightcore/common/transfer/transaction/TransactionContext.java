package de.leoxian.moonlightcore.common.transfer.transaction;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface TransactionContext {
    /// Registers a close callback that wll get invoked when the root transaction gets closed.
    /// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
    void addRootCloseCallback(RootCloseCallback rootCloseCallback);

    /// Opens a new nested transaction. Equivalent to invoke [Transaction#open(de.leoxian.moonlightcore.common.transfer.transaction.TransactionContext)] with a nonnull-parent
    /// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
    Transaction openNested();

    /// Gets a transaction that hasn't been closed yet.
    /// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
    /// @throws IllegalStateException If the transaction at the given depth isn't open
    Transaction getOpenTransaction(final int depth);

    /// @return the nesting depth of this transaction
    /// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
    int depth();

    @FunctionalInterface
    interface RootCloseCallback {
        /// A callback that gets invoked when the root transaction gets closed.
        /// @param wasAborted Whether the root transaction was aborted
        void onRootClose(boolean wasAborted);
    }

    enum Lifecycle {
        NONE,
        OPEN,
        CLOSED,
        ROOT_CLOSING
    }
}
