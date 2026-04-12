package de.realleoxian.moonlightcore.api.transfer.transaction;

public sealed interface TransactionContext permits Transaction {

    void addCloseCallback(CloseCallback callback);

    void addRootCloseCallback(RootCloseCallback callback);

    Transaction getOpenTransaction(int depth);

    Transaction openNested();

    int nestingDepth();

    @FunctionalInterface
    interface CloseCallback {
        void onClose(TransactionContext transaction, boolean wasAborted);
    }

    @FunctionalInterface
    interface RootCloseCallback {
        void onRootClose(boolean wasAborted);
    }

}
