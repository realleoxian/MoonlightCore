package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;

public interface StorageIO<T> {

    int insert(Transaction tx, T resource, int amount);

    int extract(Transaction tx, T resource, int amount);

}
