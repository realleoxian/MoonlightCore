package de.leowgc.moonlightcore.api.transfer;

import de.leowgc.moonlightcore.transfer.TransactionImpl;

public interface Transaction extends AutoCloseable {

    static Transaction openRoot() {
        return TransactionImpl.openRoot();
    }

    void commit();

    void abort();

    void addCloseCallback(CloseCallback callback);

    Transaction openNested();

    State getState();

    @Override
    void close();

    default boolean isOpened() {
        return this.getState() == State.OPENED;
    }

    default boolean isCommited() {
        return this.getState() == State.COMMITTED;
    }

    default boolean isAborted() {
        return this.getState() == State.ABORTED;
    }

    default boolean isClosed() {
        return this.getState() == State.CLOSED;
    }

    interface CloseCallback {
        void onClose(State state);
    }

    enum State {
        OPENED,
        COMMITTED,
        ABORTED,
        CLOSED
    }
}
