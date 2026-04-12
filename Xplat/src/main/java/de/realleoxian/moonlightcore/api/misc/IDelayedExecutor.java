package de.realleoxian.moonlightcore.api.misc;

import de.realleoxian.moonlightcore.impl.util.DelayedExecutorImpl;

import java.time.Duration;
import java.util.concurrent.Future;

@FunctionalInterface
public interface IDelayedExecutor {
    static IDelayedExecutor get() {
        return DelayedExecutorImpl.get();
    }

    Future<?> schedule(Runnable command, Duration delay);
}
