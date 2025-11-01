package de.leoxian.moonlightcore.transfer.transaction;

public sealed interface TransactionContext permits Transaction {

    Transaction getOpenTransaction(int nestingDepth);

    Transaction openNested();

    int nestingDepth();

    enum Lifecycle {
        NONE,
        OPEN,
        CLOSING,
        ROOT_CLOSING
    }

}
