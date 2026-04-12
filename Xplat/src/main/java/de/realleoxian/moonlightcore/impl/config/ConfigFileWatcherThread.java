package de.realleoxian.moonlightcore.impl.config;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ThreadSafe
public class ConfigFileWatcherThread extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int QUIET_TIME_MS = 500;
    private static final int RECHECK_DIRECTORIES_MS = 60_000;

    private final WatchService watchService;
    private final Map<Path, Runnable> callbacks;
    private final Set<Path> directoriesToWatch;

    private final Map<WatchKey, Path> watchedDirectories = new HashMap<>();
    private final Set<Path> changedPaths = new HashSet<>();

    private long nextDirectoryCheckTime = System.currentTimeMillis();

    public ConfigFileWatcherThread() throws IOException {
        super("MoonlightCore | Config File Watcher");
        this.setDaemon(true);
        this.callbacks = new HashMap<>();
        this.directoriesToWatch = new HashSet<>();
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    public synchronized void addCallback(ModConfig config, Runnable callback) {
        this.callbacks.put(config.getFilePath(), callback);

        if (this.directoriesToWatch.add(config.getFilePath().getParent())) {
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
            LOGGER.info("Config File Watcher was interrupted, stopping.");
        } catch (IOException e) {
            LOGGER.error("Config File Watched encountered an unhandled IOException, stopping.", e);
        } finally {
            this.watchedDirectories.keySet().forEach(WatchKey::cancel);
        }
    }

    private void runIteration() throws InterruptedException {
        long time = System.currentTimeMillis();
        if (time > this.nextDirectoryCheckTime) {
            this.nextDirectoryCheckTime = time + RECHECK_DIRECTORIES_MS;
            watchDirectories();
        }

        WatchKey key = this.watchService.poll(QUIET_TIME_MS, TimeUnit.MILLISECONDS);
        if (key != null) {
            pollWatchKey(key);
        } else {
            notifyChanges();
        }
    }

    private synchronized void pollWatchKey(WatchKey key) throws InterruptedException {
        Path watchedDirectory = watchedDirectories.get(key);
        if (watchedDirectory == null) return;

        for (WatchEvent<?> event : key.pollEvents()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                this.callbacks.keySet().stream().filter(path -> path.getParent().equals(watchedDirectory)).forEach(this.changedPaths::add);
                break;
            } else if (event.context() instanceof Path path) {
                Path fullPath = watchedDirectory.resolve(path);

                if (this.callbacks.containsKey(fullPath)) {
                    this.changedPaths.add(fullPath);
                }
            }
        }

        if (!key.reset()) {
            LOGGER.info("Failed to re-watch directory {}. It may have been deleted", watchedDirectory);
            this.watchedDirectories.remove(key);
        }
    }

    private synchronized void notifyChanges() {
        if (this.changedPaths.isEmpty()) return;

        LOGGER.debug("Detected changes in files: \n{}", this.changedPaths.stream().map(Path::toString).collect(Collectors.joining("\n")));
        List<Runnable> actions = this.changedPaths.stream().map(callbacks::get).filter(Objects::nonNull).toList();

        try {
            CompletableFuture.runAsync(() -> actions.forEach(Runnable::run)).get();
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.warn("Failed to execute files changes: {}", this.changedPaths.stream().map(Path::toString).collect(Collectors.joining("\n")));
        }
    }

    private synchronized void watchDirectories() {
        for (Path directory : this.directoriesToWatch) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            if (this.watchedDirectories.containsValue(directory) && Files.isDirectory(directory)) {
                try {
                    WatchKey key = directory.register(this.watchService, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.OVERFLOW);
                    this.watchedDirectories.put(key, directory);
                } catch (IOException e) {
                    LOGGER.error("Failed to watch directory: {}", directory, e);
                }
            }
        }
    }
}
