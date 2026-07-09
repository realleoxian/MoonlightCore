package de.leoxian.moonlightcore.internal.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ModLockHelper {
    private static final Map<String, Lock> LOCKS_BY_NAMESPACE = new ConcurrentHashMap<>();

    public static Lock getOrCreate(String namespace) {
        return LOCKS_BY_NAMESPACE.computeIfAbsent(namespace, k -> new ReentrantLock());
    }

    private ModLockHelper() {}
}
