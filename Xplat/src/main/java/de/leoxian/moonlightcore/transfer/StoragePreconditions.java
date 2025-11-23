package de.leoxian.moonlightcore.transfer;

public final class StoragePreconditions {

    public static void notBlank(TransferResource<?> resource) {
        if(resource.isBlank()) {
            throw new IllegalArgumentException("Transfer resource may not be blank.");
        }
    }

    public static void notNegative(int amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Amount may not be negative, but it is: " + amount);
        }
    }

    public static void notBlankNotNegative(TransferResource<?> resource, int amount) {
        notBlank(resource);
        notNegative(amount);
    }

}
