package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class StorageUtils {

    public static <T extends TransferResource<?>> int move(@Nullable TransactionContext ctx, @Nullable Storage<T> from, @Nullable Storage<T> to, Predicate<T> filter, int amount) {
        Objects.requireNonNull(filter, "Filter may not be null");
        StorageInternals.checkNonNegative(amount);

        if(from == null || to == null || amount == 0) {
            return 0;
        }

        try (Transaction tx = Transaction.open(ctx)) {
            int size = from.size();
            int totalMoved = 0;

            for(int idx = 0; idx < size; idx++) {
                T fromResource = from.getResource(idx);

                if(fromResource.isEmpty() || !filter.test(fromResource)) {
                    continue;
                }

                int maxExtracted;
                try(Transaction simulatedExtract = tx.openNested()) {
                    maxExtracted = from.extract(simulatedExtract, idx, fromResource, amount - totalMoved);
                }

                if(maxExtracted == 0) {
                    continue;
                }

                try(Transaction transferTransaction = tx.openNested()) {
                    int inserted = to.insert(tx, fromResource, maxExtracted);

                    if(inserted !=  from.extract(transferTransaction, idx, fromResource, inserted)) {
                        continue;
                    }

                    totalMoved += inserted;
                    transferTransaction.commit();

                    if(totalMoved >= amount) {
                        break;
                    }
                }
            }

            tx.commit();
            return totalMoved;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ItemResource> int insertStacking(Transaction tx, T resource, int amount, List<SingleSlotStorage<T>> slots) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        int currentAmount = 0;

        for(SingleSlotStorage<T> slot : slots) {
            if(!slot.isResourceValid(resource)) {
                continue;
            }

            currentAmount += slot.insert(tx, resource, amount - currentAmount);
            if(currentAmount == amount) {
                return currentAmount;
            }
        }

        for(SingleSlotStorage<T> slot : slots) {
            currentAmount += slot.insert(tx, resource, amount - currentAmount);

            if(currentAmount == amount) {
                return currentAmount;
            }
        }

        return currentAmount;
    }

    public static <T extends TransferResource<?>> boolean isValid(Storage<T> storage, T resource) {
        StorageInternals.checkNonEmpty(resource);

        int size = storage.size();
        for(int i = 0; i < size; i++) {
            if(storage.isResourceValid(i, resource)) {
                return true;
            }
        }

        return false;
    }

    public static <T extends TransferResource<?>> boolean isFull(Storage<T> storage) {
        int size = storage.size();

        for(int i = 0; i < size; i++) {
            if(storage.getAmount(i) < storage.get(i).getCapacity(storage.getResource(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEmpty(Storage<? extends TransferResource<?>> storage) {
        int size = storage.size();

        for(int i = 0; i < size; i++) {
            if(storage.getAmount(i) > 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEmpty(TransferResource<?> resource, int amount) {
        return amount <= 0 || resource.isEmpty();
    }

    private StorageUtils() {}
}
