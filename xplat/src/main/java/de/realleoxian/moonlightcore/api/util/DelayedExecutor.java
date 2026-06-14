package de.realleoxian.moonlightcore.api.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.time.Duration;
import java.util.concurrent.*;

public final class DelayedExecutor {
    private static DelayedExecutor instance;

    public static DelayedExecutor get() {
        if (DelayedExecutor.instance == null) {
            DelayedExecutor.instance = new DelayedExecutor();
        }
        return DelayedExecutor.instance;
    }

    private final ScheduledThreadPoolExecutor service;

    private DelayedExecutor() {
        final var threadFactory = new ThreadFactoryBuilder().setNameFormat("MoonlightCore deduplicating run executor %d").build();
        final var service = new ScheduledThreadPoolExecutor(1, threadFactory);
        service.setRemoveOnCancelPolicy(true);
        this.service = service;
    }

    public Future<?> schedule(Runnable task, Duration delay) {
        return this.service.schedule(task, delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}
