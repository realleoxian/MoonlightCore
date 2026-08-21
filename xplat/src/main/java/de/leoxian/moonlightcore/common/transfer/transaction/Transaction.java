package de.leoxian.moonlightcore.common.transfer.transaction;

import de.leoxian.moonlightcore.internal.common.transfer.transaction.TransactionManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Transaction extends TransactionContext, AutoCloseable {
    static Transaction open(@Nullable TransactionContext parent) {
        return TransactionManager.open(parent);
    }

    static Transaction openRoot() {
        return TransactionManager.openRoot();
    }

    static Lifecycle getLifecycle() {
        return TransactionManager.getLifecycle();
    }

    void addCloseCallback(CloseCallback closeCallback);

    void commit();

    void abort();

    @Override
    void close();

    @FunctionalInterface
    interface CloseCallback {
        void onTransactionClose(TransactionContext context, boolean wasAborted);
    }
}
