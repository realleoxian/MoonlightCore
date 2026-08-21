package de.leoxian.moonlightcore.common.transfer.transaction;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface TransactionContext {
    void addRootCloseCallback(RootCloseCallback rootCloseCallback);

    Transaction openNested();

    Transaction getOpenTransaction(final int depth);

    int depth();

    @FunctionalInterface
    interface RootCloseCallback {
        void onRootClose(boolean wasAborted);
    }

    enum Lifecycle {
        NONE,
        OPEN,
        CLOSED,
        ROOT_CLOSING
    }
}
