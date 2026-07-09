package de.leoxian.moonlightcore.internal.common.util;

import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.Future;

public final class DelayedRunnable {
    private final Duration duration;
    private final Runnable task;

    private volatile @Nullable Future<?> future;

    public DelayedRunnable(Duration duration, Runnable task) {
        this.duration = duration;
        this.task = task;
    }

    public synchronized void run() {
        if (this.future != null) {
            this.future.cancel(false);
        }
        this.future = DelayedExecutor.INSTANCE.schedule(this.task, this.duration);
    }
}
