package de.leoxian.moonlightcore.api.transfer;

import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;

import java.util.Objects;
import java.util.function.Predicate;

public class StorageUtil {

    public static <T> int move(@Nullable TransactionContext transaction, Storage<T> from, Storage<T> to, Predicate<T> filter, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        Objects.requireNonNull(filter, "Filter may not be 'null'");

        int totalMoved = 0;
        try (Transaction tx = Transaction.open(transaction)) {
            for (int i = 0; i < from.size(); i++) {
                T resource = from.getResource(i);
                if (from.isBlank(i) || !filter.test(resource)) {
                    continue;
                }

                int maxExtracted = simulateExtract(tx, from, resource, maxAmount);
                if (maxExtracted > 0) {
                    try (Transaction exchangeTx = tx.openNested()) {
                        int inserted = to.insert(exchangeTx, resource, maxExtracted);

                        if (from.extract(exchangeTx, resource, inserted) == inserted) {
                            totalMoved += inserted;
                            exchangeTx.commit();
                        }
                    }
                }

                if (totalMoved == maxExtracted) {
                   tx.commit();
                   return totalMoved;
                }
            }

            tx.commit();
            return totalMoved;
        } catch (Exception e) {
            CrashReport report = new CrashReport("Failed to move from an storage to other", e);
            report.addCategory("Move details")
                    .setDetail("Origin storage", from::toString)
                    .setDetail("Destiny storage", to::toString)
                    .setDetail("Filter", filter::toString)
                    .setDetail("Max amount", maxAmount)
                    .setDetail("Transaction", transaction);

            throw new ReportedException(report);
        }
    }

    public static <T> int insertStacking(@Nullable TransactionContext transaction, Storage<T> storage, T resource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);

        int inserted = 0;
        try (Transaction tx = Transaction.open(transaction)) {
            for(int i = 0; i < storage.size(); i++) {
                if (!storage.isBlank(i) && storage.getResource(i) == resource) {
                    inserted += storage.insert(tx, resource, maxAmount - inserted);

                    if (inserted == maxAmount) {
                        tx.commit();
                        return inserted;
                    }
                }
            }

            if(inserted == maxAmount) {
                tx.commit();
                return inserted;
            }

            for (int i = 0; i < storage.size(); i++) {
                if (storage.isBlank(i)) {
                    inserted += storage.insert(tx,  resource, maxAmount - inserted);

                    if (inserted == maxAmount) {
                        tx.commit();
                        return inserted;
                    }
                }
            }

            return inserted;
        } catch (Exception e) {
            CrashReport report = new CrashReport("Failed to insert stacking into a storage", e);
            report.addCategory("Insert details")
                    .setDetail("Storage", storage::toString)
                    .setDetail("Resource", resource::toString)
                    .setDetail("Max amount", maxAmount)
                    .setDetail("Transaction", transaction);

            throw new ReportedException(report);
        }
    }

    public static <T> int simulateInsert(@Nullable TransactionContext transaction, Storage<T> storage, int index, T resource, int maxAmount) {
        try (Transaction tx = Transaction.open(transaction)) {
            return storage.insert(tx, index, resource, maxAmount);
        }
    }

    public static <T> int simulateInsert(@Nullable TransactionContext transaction, Storage<T> storage, T resource, int maxAmount) {
        try (Transaction tx = Transaction.open(transaction)) {
            return storage.insert(tx, resource, maxAmount);
        }
    }

    public static <T> int simulateExtract(@Nullable TransactionContext transaction, Storage<T> storage, int index, T resource, int maxAmount) {
        try (Transaction tx = Transaction.open(transaction)) {
            return storage.extract(tx, index, resource, maxAmount);
        }
    }

    public static <T> int simulateExtract(@Nullable TransactionContext transaction, Storage<T> storage, T resource, int maxAmount) {
        try (Transaction tx = Transaction.open(transaction)) {
            return storage.extract(tx, resource, maxAmount);
        }
    }

    private StorageUtil() {}
}
