package de.realleoxian.moonlightcore.impl.runtime;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class XplatMoonlightCoreRuntime<C extends ModLoadingRuntimeContext> implements MoonlightCoreRuntime<C> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Deque<Runnable> initializeActions = new ArrayDeque<>();
    private boolean initialized = false;

    public void initializeRuntime() {
        if (this.initialized) return;
        this.initialized = true;

        try {
            Runnable action;
            while ((action = initializeActions.pop()) != null) {
                action.run();
            }
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to invoke moonlightcore's runtime initialization actions", e);
        }
    }

    @Override
    public void onRuntimeInitialized(Runnable action) {
        if (this.initialized) {
            action.run();
            return;
        }

        this.initializeActions.push(action);
    }
}
