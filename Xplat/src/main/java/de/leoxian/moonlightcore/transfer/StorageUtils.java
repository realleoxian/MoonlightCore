package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class StorageUtils {

    public static <T> int move(@Nullable TransactionContext context, @Nullable Storage<T> from, @Nullable Storage<T> to, Predicate<T> filter, int maxAmount) {
        Objects.requireNonNull(filter, "Filter may not be null");
        if(from == null || to == null) return 0;

        int totalMoved = 0;

        try(Transaction iterationTransaction = Transaction.open(context)) {
            for(StorageView<T> view : from.nonEmptyViews()) {
                T resource = view.getResource();
                if(!filter.test(resource)) continue;

                int maxExtracted = simulateExtract(iterationTransaction, view, resource, maxAmount - totalMoved);
                try(Transaction transferTransaction = iterationTransaction.openNested()) {
                    int accepted = to.extract(transferTransaction, resource, maxExtracted);

                    if(view.extract(transferTransaction, resource, accepted) == accepted) {
                        totalMoved += accepted;
                        transferTransaction.commit();
                    }
                }

                if(maxAmount == totalMoved) {
                    iterationTransaction.commit();
                    return totalMoved;
                }
            }
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Moving sources between storages");
            report.addCategory("Move details")
                    .setDetail("Input storage", from::toString)
                    .setDetail("Output storage", to::toString)
                    .setDetail("Filter", filter::toString)
                    .setDetail("Max amount", maxAmount)
                    .setDetail("Transaction", context);

            throw new ReportedException(report);
        }

        return totalMoved;
    }

    public static <T> @Nullable ResourceStack<T> extractAny(TransactionContext context, @Nullable Storage<T> storage, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        if(storage == null) return null;

        try {
            for(StorageView<T> view : storage.nonEmptyViews()) {
                T resource = view.getResource();
                int amount = view.extract(context, resource, maxAmount);

                if(amount > 0) {
                    return new ResourceStack<>(resource, amount);
                }
            }
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Extracting resources from storage");
            report.addCategory("Extraction details")
                    .setDetail("Storage", storage::toString)
                    .setDetail("Max amount", maxAmount)
                    .setDetail("Transaction", context);

            throw new ReportedException(report);
        }

        return null;
    }

    public static <T> int insertStacking(TransactionContext context, List<? extends SingleSlotStorage<T>> slots, T resource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int amount = 0;

        try {
            for(SingleSlotStorage<T> slot : slots) {
                if(!slot.isResourceBlank()) {
                    amount += slot.insert(context, resource, maxAmount - amount);

                    if(amount == maxAmount) {
                        return amount;
                    }
                }
            }

            for(SingleSlotStorage<T> slot : slots) {
                amount += slot.insert(context, resource, maxAmount - amount);

                if(amount == maxAmount) {
                    return amount;
                }
            }
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Inserting resources into slots");
            report.addCategory("Slotted insertion details")
                    .setDetail("Slots", () -> Objects.toString(slots, null))
                    .setDetail("Resource", () -> Objects.toString(resource, null))
                    .setDetail("Max amount", maxAmount)
                    .setDetail("Transaction", context);

            throw new ReportedException(report);
        }

        return amount;
    }

    public static <T> @Nullable T findStoredResource(@Nullable Storage<T> storage) {
        return findStoredResource(storage, r -> true);
    }

    public static <T> @Nullable T findStoredResource(@Nullable Storage<T> storage, Predicate<T> filter) {
        Objects.requireNonNull(filter, "Filter may not be null");
        if(storage == null) return null;

        for(StorageView<T> view : storage.nonEmptyViews()) {
            if(filter.test(view.getResource())) {
                return view.getResource();
            }
        }

        return null;
    }

    public static <T> @Nullable T findExtractableResource(@Nullable TransactionContext context, @Nullable Storage<T> storage) {
        return findExtractableResource(context, storage, r -> true);
    }

    public static <T> @Nullable T findExtractableResource(@Nullable TransactionContext context, @Nullable Storage<T> storage, Predicate<T> filter) {
        Objects.requireNonNull(filter, "Filter may not be null");
        if(storage == null) return null;

        try(Transaction nested = Transaction.open(context)) {
            for(StorageView<T> view : storage.nonEmptyViews()) {
                T resource = view.getResource();

                if(filter.test(resource) && view.extract(nested, resource, Integer.MAX_VALUE) > 0) {
                    return resource;
                }
            }
        }

        return null;
    }

    public static <T> @Nullable ResourceStack<T> findExtractableStack(@Nullable TransactionContext context, @Nullable Storage<T> storage) {
        return findExtractableStack(context, storage, r -> true);
    }

    public static <T> @Nullable ResourceStack<T> findExtractableStack(@Nullable TransactionContext context, @Nullable Storage<T> storage, Predicate<T> filter) {
        T extractableResource = findExtractableResource(context, storage, filter);

        if(extractableResource != null) {
            int extractableAmount = simulateExtract(context, storage, extractableResource, Integer.MAX_VALUE);

            if(extractableAmount > 0) {
                return new ResourceStack<>(extractableResource, extractableAmount);
            }
        }

        return null;
    }

    public static <T> int simulateInsert(TransactionContext context, StorageIO<T> storage, T resource, int maxAmount) {
        try(Transaction simulated = context.openNested()) {
            return storage.insert(simulated, resource, maxAmount);
        }
    }

    public static <T> int simulateExtract(TransactionContext context, StorageIO<T> storage, T resource, int maxAmount) {
        try(Transaction simulated = context.openNested()) {
            return storage.extract(simulated, resource, maxAmount);
        }
    }

    private StorageUtils() {}
}
