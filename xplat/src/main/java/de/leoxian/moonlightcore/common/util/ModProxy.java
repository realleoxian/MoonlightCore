package de.leoxian.moonlightcore.common.util;

import de.leoxian.moonlightcore.common.platform.Platform;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModProxy<T> {
    private final Map<String, String> options = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Supplier<T> fallback;
    private final Class<T> type;
    private volatile T cached;

    public ModProxy(Class<T> type, Supplier<T> fallback) {
        this.type = type;
        this.fallback = fallback;
    }

    public ModProxy<T> put(final String modId, final String className) {
        this.options.put(modId, className);
        return this;
    }

    public T get() {
        T ret = this.cached;
        if (ret == null) {
            synchronized (this) {
                ret = this.cached;
                if (ret == null) {
                    for (final Map.Entry<String, String> entry : this.options.entrySet()) {
                        String modId = entry.getKey();
                        if (!Platform.isModLoaded(modId)) {
                            continue;
                        }

                        String className = entry.getValue();
                        try {
                            Class<?> clazz = Class.forName(className);
                            if (!this.type.isAssignableFrom(clazz)) {
                                throw new IllegalArgumentException("Class " + clazz + " does not implement " + this.type);
                            }

                            Constructor<?> constructor = clazz.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            ret = cached = this.type.cast(constructor.newInstance());
                            break;
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException("Failed to instantiate proxy target class");
                        }
                    }
                }

                if (ret == null) {
                    ret = cached = this.fallback.get();
                    if (ret == null) {
                        throw new IllegalStateException("Unable to instantiate proxy, not even using fallback?");
                    }
                }
            }
        }
        return ret;
    }
}
