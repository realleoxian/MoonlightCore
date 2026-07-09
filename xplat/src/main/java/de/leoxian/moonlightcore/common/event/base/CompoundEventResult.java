package de.leoxian.moonlightcore.common.event.base;

public record CompoundEventResult<T>(EventResult result, T value) {
    private static final CompoundEventResult<?> PASS = new CompoundEventResult<>(EventResult.PASS, null);

    @SuppressWarnings("unchecked")
    public static <T> CompoundEventResult<T> pass() {
        return (CompoundEventResult<T>) PASS;
    }

    public boolean isValuePresent() {
        return this.value != null;
    }
}
