package de.realleoxian.moonlightcore.api.misc;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;

import java.time.Duration;
import java.util.concurrent.Future;

public class DelayedRunner {
    private final IDelayedExecutor executor;
    private final Duration delay;
    private @Nullable Future<?> future;

    public DelayedRunner(Duration delay, IDelayedExecutor executor) {
        this.delay = delay;
        this.executor = executor;
    }

    public DelayedRunner(Duration delay) {
        this (delay, IDelayedExecutor.get());
    }

    public synchronized void run(Runnable action) {
        if (this.future != null) {
            this.future.cancel(false);
        }

        this.future = executor.schedule(action, this.delay);
    }
}
