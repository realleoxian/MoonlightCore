package de.realleoxian.moonlightcore.api.transfer.transaction;

import de.realleoxian.moonlightcore.impl.transfer.transaction.TransactionManager;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;

public non-sealed interface Transaction extends TransactionContext, AutoCloseable {

    static Transaction openRoot() {
        return TransactionManager.openRoot();
    }

    static Transaction open(@Nullable TransactionContext parent) {
        return TransactionManager.open(parent);
    }

    static @Nullable Transaction getCurrentUnsafe() {
        return TransactionManager.getCurrentUnsafe();
    }

    static TransactionLifecycle getLifecycle() {
        return TransactionManager.getLifecycle();
    }

    void commit();

    void abort();

    @Override
    void close();

    String getDebugName();

}
