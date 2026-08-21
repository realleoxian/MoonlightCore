package de.leoxian.moonlightcore.internal.common.transfer;

import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.storage.Storage;
import net.minecraft.core.component.*;

import java.util.Optional;

public final class StorageInternals {
    public static void checkSingleSlotIndex(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("May not access to other index than 0 on a single slot storage");
        }
    }

    public static void checkIndex(int index, Storage<?> storage) {
        if (index < 0) throw new IndexOutOfBoundsException("May not try to access to a negative storage slot");
        if (index >= storage.size()) throw new IndexOutOfBoundsException("May not exceed storage bounds");
    }

    public static void checkNotNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("May not use a negative amount");
        }
    }

    public static void checkNotEmpty(Resource resource) {
        if (resource.isEmpty()) {
            throw new IllegalArgumentException("May not use an empty resource");
        }
    }

    public static DataComponentPatch mergePatches(DataComponentPatch base, DataComponentPatch applied) {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        writePatchTo(base, builder);
        writePatchTo(applied, builder);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static void writePatchTo(DataComponentPatch patch, DataComponentPatch.Builder output) {
        for (final var entry : patch.entrySet()) {
            if (entry.getValue().isPresent()) {
                output.set((DataComponentType<Object>) entry.getKey(), entry.getValue());
            } else {
                output.remove(entry.getKey());
            }
        }
    }

    private StorageInternals() {

    }
}
