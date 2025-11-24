package de.leoxian.moonlightcore.util.nullness;

import de.leoxian.moonlightcore.util.LazySupplier;

import java.util.Objects;
import java.util.function.Supplier;

@FunctionalInterface
public interface NonnullSupplier<@Nonnull T> extends Supplier<T> {

    static <T> NonnullSupplier<T> of(Supplier<@Nullable T> sup) {
        return of(sup, () -> "Unexpected null value from supplier");
    }

    static <T> NonnullSupplier<T> of(Supplier<@Nullable T> sup, NonnullSupplier<String> errorMessage) {
        return  () -> {
            T res = sup.get();
            Objects.requireNonNull(res, errorMessage.get());
            return res;
        };
    }

    static <T> NonnullSupplier<T> lazy(Supplier<@Nonnull T> sup) {
        return LazySupplier.of(sup)::get;
    }

    T get();

    default NonnullSupplier<T> lazy() {
        return lazy(this);
    }

}
