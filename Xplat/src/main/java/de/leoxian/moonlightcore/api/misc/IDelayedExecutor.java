package de.leoxian.moonlightcore.api.misc;

import java.time.Duration;
import java.util.concurrent.Future;

@FunctionalInterface
public interface IDelayedExecutor {
    Future<?> schedule(Runnable command, Duration delay);
}
