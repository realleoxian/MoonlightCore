package de.realleoxian.moonlightcore.impl.misc;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.misc.ModProxy;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModProxyImpl<T> implements ModProxy<T> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private record Entry<T>(String modId, String className, Supplier<T> proxy) {
    }

    private final List<Entry<T>> entries = new ArrayList<>();
    private @Nullable T proxy = null;

    private final Supplier<T> fallback;

    public ModProxyImpl(Supplier<T> fallback) {
        this.fallback = fallback;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ModProxy<T> with(String modId, String className) {
        this.entries.add(new Entry<>(modId, className, () -> {
            try {
                return (T) Class.forName(className).getConstructor().newInstance();
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't create proxy: " + className, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Couldn't find no-arg constructor for: " + className, e);
            }
        }));
        return this;
    }

    @Override
    public T build() {
        if (this.proxy != null) {
            LOGGER.debug("Cannot build a new proxy instance unless you invalidate the current one");
            return this.proxy;
        }

        List<Entry<T>> possibleProxies = this.entries.stream().filter(e -> MoonlightCore.isModLoaded(e.modId())).toList();
        for (Entry<T> entry : possibleProxies) {
            try {
                this.proxy = entry.proxy().get();

                if (this.proxy != null) {
                    return this.proxy;
                }
            } catch (Exception e) {
                LOGGER.error("Couldn't initialize mod proxy: {}", entry.className);
            }
        }

        if (this.proxy == null) {
            this.proxy = fallback.get();
        }

        return this.proxy;
    }
}
