package de.realleoxian.moonlightcore.impl.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.realleoxian.moonlightcore.api.misc.IDelayedExecutor;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;

import java.time.Duration;
import java.util.concurrent.*;

public final class DelayedExecutorImpl implements IDelayedExecutor {
    private static @Nullable DelayedExecutorImpl INSTANCE;

    public static DelayedExecutorImpl get() {
        if (DelayedExecutorImpl.INSTANCE == null) {
            DelayedExecutorImpl.INSTANCE = new DelayedExecutorImpl();
        }
        return DelayedExecutorImpl.INSTANCE;
    }

    private final ScheduledExecutorService service;

    private DelayedExecutorImpl() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("MoonlightCore Delayed Run executor %d")
                .build();

        var service = new ScheduledThreadPoolExecutor(1, threadFactory);
        service.setRemoveOnCancelPolicy(true);
        this.service = service;
    }

    @Override
    public Future<?> schedule(Runnable command, Duration delay) {
        return this.service.schedule(command, delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}
