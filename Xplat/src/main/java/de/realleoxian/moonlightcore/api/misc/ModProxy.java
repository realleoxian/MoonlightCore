package de.realleoxian.moonlightcore.api.misc;

import de.realleoxian.moonlightcore.impl.misc.ModProxyImpl;

import java.util.function.Supplier;

public interface ModProxy<T> {
    static <T> ModProxy<T> of(Supplier<T> fallback) {
        return new ModProxyImpl<>(fallback);
    }

    ModProxy<T> with(String modId, String className);

    T build();
}
