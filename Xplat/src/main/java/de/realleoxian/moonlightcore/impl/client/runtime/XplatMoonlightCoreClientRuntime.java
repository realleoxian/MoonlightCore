package de.realleoxian.moonlightcore.impl.client.runtime;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntime;
import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class XplatMoonlightCoreClientRuntime<C extends ModLoadingRuntimeContext> implements MoonlightCoreClientRuntime<C> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Deque<Runnable> initializeActions = new ArrayDeque<>();
    private boolean initialized = false;

    public void initializeRuntime() {
        if (this.initialized) return;
        this.initialized = true;

        try {
            while (!initializeActions.isEmpty()) {
                Runnable action = this.initializeActions.remove();
                action.run();
            }
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to invoke moonlightcore's client runtime initialization actions", e);
        }
    }

    @Override
    public void onClientRuntimeInitialized(Runnable action) {
        if (this.initialized) {
            action.run();
            return;
        }

        this.initializeActions.push(action);
    }
}
