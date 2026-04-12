package de.realleoxian.moonlightcore.impl.transfer;

import de.realleoxian.moonlightcore.api.transfer.Resource;

public class StoragePreconditions {

    public static void notBlankNotNegative(Resource<?> resource, int i) {
        notBlank(resource);
        notNegative(i);
    }

    public static void notBlank(Resource<?> resource) {
        if(resource.isBlank()) {
            throw new IllegalStateException("Resource may not be blank");
        }
    }

    public static void notNegative(int i) {
        if (i < 0) {
            throw new IllegalStateException("Amount may not be negative");
        }
    }

    public static void singleSlotIndexCheck(int index) {
        if(index != 0) {
            throw new IndexOutOfBoundsException("Tried to access to other index than 0 on a single slot storage");
        }
    }

    private StoragePreconditions() {}
}
