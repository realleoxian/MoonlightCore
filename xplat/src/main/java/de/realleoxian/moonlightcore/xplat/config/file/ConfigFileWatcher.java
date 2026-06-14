package de.realleoxian.moonlightcore.xplat.config.file;

import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class ConfigFileWatcher extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int QUIET_TIME_MS = 500;
    private static final int RECHECK_DIRECTORIES_MS = 60_000;

    private static ConfigFileWatcher instance = null;

    public static ConfigFileWatcher get() {
        if (ConfigFileWatcher.instance == null) {
            try {
                ConfigFileWatcher.instance = new ConfigFileWatcher();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create ConfigFileWatcher", e);
            }
        }
        return ConfigFileWatcher.instance;
    }

    private final WatchService watchService;
    private final Map<Path, Runnable> fileChangingHandlers = new HashMap<>();
    private final Set<Path> directoriesToWatch = new HashSet<>();

    private final Map<WatchKey, Path> watchedDirectories = new HashMap<>();
    private long nextDirectoryCheckTime = System.currentTimeMillis();

    private ConfigFileWatcher() throws IOException {
        super("MoonlightCore Config File Watcher");
        this.setDaemon(true);
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    public synchronized void register(Path path, Runnable callback) {
        this.fileChangingHandlers.put(path, callback);
        if (this.directoriesToWatch.add(path.getParent())) {
            this.nextDirectoryCheckTime = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {
        try (this.watchService) {
            while (!Thread.currentThread().isInterrupted()) {
                runIteration();
            }
        } catch (InterruptedException ignored) {
            LOGGER.info("FileWatched was interrupted, stopping.");
        } catch (IOException e) {
            LOGGER.error("FileWatched encountered an unhandled IOException.", e);
        } finally {
            this.watchedDirectories.keySet().forEach(WatchKey::cancel);
        }
    }

    private void runIteration() throws InterruptedException {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > this.nextDirectoryCheckTime) {
            this.nextDirectoryCheckTime = currentTimeMillis + RECHECK_DIRECTORIES_MS;
            this.watchDirectories();
        }

        final var watchKey = this.watchService.poll(QUIET_TIME_MS, TimeUnit.MILLISECONDS);
        if (watchKey != null) {
            pollWatchKey(watchKey);
        }
    }

    private synchronized void pollWatchKey(WatchKey watchKey) throws InterruptedException {
        Path watchedDirectory = this.watchedDirectories.get(watchKey);
        if (watchedDirectory == null) {
            return;
        }

        for (final var watchEvent : watchKey.pollEvents()) {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) continue;
            if (!(watchEvent.context() instanceof Path path)) continue;

            final var fullPath = watchedDirectory.resolve(path);
            final var changeHandler = this.fileChangingHandlers.get(fullPath);
            if (changeHandler != null) {
                try {
                    changeHandler.run();
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to manage file changes on '{}'", fullPath, throwable);
                }
            }
        }

        if (!watchKey.reset()) {
            LOGGER.info("Failed to re-watch directory {}. It may have been deleted", watchedDirectory);
            this.watchedDirectories.remove(watchKey);
        }
    }

    private synchronized void watchDirectories() {
        for (Path directory : this.directoriesToWatch) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            if (!this.watchedDirectories.containsValue(directory) && Files.isDirectory(directory)) {
                try {
                    final var watchKey = directory.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
                    this.watchedDirectories.put(watchKey, directory);
                } catch (IOException e) {
                    LOGGER.error("Failed to watch directory {}", directory, e);
                }
            }
        }
    }
}
