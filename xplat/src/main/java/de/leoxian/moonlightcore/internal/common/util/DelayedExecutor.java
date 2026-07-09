package de.leoxian.moonlightcore.internal.common.util;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DelayedExecutor {
    public static final DelayedExecutor INSTANCE = new DelayedExecutor();

    private final ScheduledThreadPoolExecutor service;

    private DelayedExecutor() {
        this.service = new ScheduledThreadPoolExecutor(1);
        this.service.setRemoveOnCancelPolicy(true);
    }

    public Future<?> schedule(Runnable task, Duration delay) {
        return this.service.schedule(task,delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}
