package de.leoxian.moonlightcore.common.transfer.storage;

import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;

public interface Storage<T extends Resource> {
    @SuppressWarnings("unchecked")
    static <T extends Resource> Class<Storage<T>> asClass() {
        return (Class<Storage<T>>) (Class<?>) Storage.class;
    }

    /// Inserts up the given amount of a resource into this storage at the given index
    /// @param transaction The transaction this operation is part of
    /// @param index The index to insert the resource into
    /// @param resource The resource to insert. **Must be non-empty**
    /// @param maxAmount The maximum amount to insert. **Must be positive**
    /// @return The amount that was inserted. Between `0` (inclusive) and `maxAmount` (inclusive)
    /// @throws IllegalArgumentException If the resource is empty or the maximum amount is negative
    int insert(Transaction transaction, int index, T resource, int maxAmount);

    /// Tries to inserts up the given amount of a resource on this storage
    /// @param transaction The transaction this operation is part of
    /// @param resource The resource to insert. **Must be non-empty**
    /// @param maxAmount The maximum amount to insert. **Must be positive**
    /// @return The amount that was inserted. Between `0` (inclusive) and `maxAmount` (inclusive)
    /// @throws IllegalArgumentException If the resource is empty or the maximum amount is negative
    default int insert(Transaction transaction, T resource, int maxAmount) {
        int inserted = 0;
        for (int i = 0; i < size(); i++) {
            inserted += insert(transaction, i, resource, maxAmount - inserted);
            if (inserted == maxAmount) {
                break;
            }
        }
        return inserted;
    }

    /// Check if a resource can be inserted at the given index on this storage
    /// @param index The index to check
    /// @param resource The resource to check
    /// @return Whether the resource can be inserted on the given index
    default boolean canInsert(int index, T resource) {
        return true;
    }

    /// @return Whether this storage supports insertion
    default boolean supportsInsertion() {
        return true;
    }

    /// Extracts up to the given amount of a resource from this storage at the given index.
    /// @param transaction The transaction this operation is part of
    /// @param index The index to extract the resource from
    /// @param resource The resource to extract. **Must be non-empty**
    /// @param maxAmount The maximum amount to extract. **Must be positive**
    /// @return The amount that was extracted. Between `0` (inclusive) and `maxAmount` (inclusive)
    /// @throws IllegalArgumentException If the resource is empty or the maximum amount is negative
    int extract(Transaction transaction, int index, T resource, int maxAmount);

    /// Tries to extract up to the given amount of a resource from this storage at the given index
    /// @param transaction The transaction this operation is part of
    /// @param resource The resource to extract. **Must be non-empty**
    /// @param maxAmount The maximum amount to extract. **Must be positive**
    /// @return The amount that was extracted. Between `0` (inclusive) and `maxAmount` (inclusive)
    /// @throws IllegalArgumentException If the resource is empty or the maximum amount is negative
    default int extract(Transaction transaction, T resource, int maxAmount) {
        int extracted = 0;
        for (int i = 0; i < size(); i++) {
            extracted += extract(transaction, i, resource, maxAmount - extracted);
            if (extracted == maxAmount) {
                break;
            }
        }
        return extracted;
    }

    /// Check if a resource can be extracted from the given index on this storage
    /// @param index The index to check
    /// @param resource The resource to check
    /// @return Whether the resource can be extracted from the given index
    default boolean canExtract(int index, T resource) {
        return true;
    }

    /// @return Whether this storage supports extraction
    default boolean supportsExtraction() {
        return true;
    }

    /// @param index The index the resource belongs to
    /// @return The resource at the given index, which may be empty
    T getResource(int index);

    /// @param index The index the resource amount belongs to
    /// @return The current amount of a resource on the given index, or 0 if no resource is present
    int getAmount(int index);

    /// The capacity of this storage at the given index and for the given resource, irrespective of the
    /// current amount or resource currently at that index.
    /// @param index The index to get the capacity for
    /// @param resource The resource to get the capacity for
    /// @return The capacity of the given index for the given resource
    int getCapacity(int index, T resource);

    /// @return The size of this storage
    int size();
}
