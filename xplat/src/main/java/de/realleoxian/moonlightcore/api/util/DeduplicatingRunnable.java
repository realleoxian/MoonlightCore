package de.realleoxian.moonlightcore.api.util;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public final class DeduplicatingRunnable {
    private final DelayedExecutor executor;
    private final Duration duration;
    private final Runnable task;
    @Nullable
    private volatile Future<?> future;

    public DeduplicatingRunnable(Duration duration, Runnable task, @Nullable ScheduledFuture<?> future) {
        this.executor = DelayedExecutor.get();
        this.duration = duration;
        this.task = task;
        this.future = future;
    }

    public DeduplicatingRunnable(Duration duration, Runnable task) {
        this (duration, task, null);
    }

    /**
     * Runs the underlying {@linkplain #task} once the 'delay' has elapsed, if {@code run}
     * isn't called again before its execution.
     */
    public synchronized void run() {
        if (this.future != null) {
            this.future.cancel(false);
        }
        this.future = executor.schedule(this.task, this.duration);
    }
}
