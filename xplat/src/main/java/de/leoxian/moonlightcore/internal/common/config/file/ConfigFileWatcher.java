package de.leoxian.moonlightcore.internal.common.config.file;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.common.config.Config;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConfigFileWatcher {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int QUIET_TIME_MS = 500;

	private static final ExecutorService WORKER_EXECUTOR = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("MoonlightCore Worker-", 0).factory());
	private static ScheduledExecutorService fileWatcher = null;

	private static final Lock WATCHER_LOCK = new ReentrantLock();
	private static final Lock UPDATES_LOCK = new ReentrantLock();

	private static WatchService watchService;
	private static final Map<Path, Runnable> FILE_CHANGE_HANDLERS = new ConcurrentHashMap<>();
	private static final Set<Path> INTERNAL_UPDATES = new HashSet<>();
	private static final Map<WatchKey, Path> WATCHED_DIRECTORIES = new ConcurrentHashMap<>();

	static {
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			LOGGER.error("Failed to initialize WatchService", e);
		}
	}

	public static void start(Executor executor) {
		WATCHER_LOCK.lock();
		try {
			if (fileWatcher != null) return;
			fileWatcher = Executors.newSingleThreadScheduledExecutor(
					Thread.ofVirtual().name("MoonlightCore Config File Watcher").factory()
			);

			fileWatcher.scheduleAtFixedRate(() -> {
				List<Map.Entry<Path, Runnable>> entriesToProcess = new ArrayList<>();
				WATCHER_LOCK.lock();
				try {
					if (watchService == null) return;

					WatchKey key = watchService.poll();
					while (key != null) {
						Path watchedDir = WATCHED_DIRECTORIES.get(key);
						if (watchedDir != null) {
							for (var event : key.pollEvents()) {
								if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
								if (!(event.context() instanceof Path filePath)) continue;

								Path fullPath = watchedDir.resolve(filePath);
								Runnable handler = FILE_CHANGE_HANDLERS.get(fullPath);

								if (handler != null) {
									if (!INTERNAL_UPDATES.remove(fullPath)) {
										entriesToProcess.add(Map.entry(fullPath, handler));
									}
								}
							}

							if (!key.reset()) {
								WATCHED_DIRECTORIES.remove(key);
							}
						} else {
							key.reset();
						}
						key = watchService.poll();
					}
				} catch (Exception e) {
					LOGGER.error("Error polling config file watch service", e);
				} finally {
					WATCHER_LOCK.unlock();
				}

				if (entriesToProcess.isEmpty()) return;

				CompletableFuture.supplyAsync(() -> {
					List<Runnable> readyCallbacks = new ArrayList<>();
					for (var entry : entriesToProcess) {
						try {
							readyCallbacks.add(entry.getValue());
						} catch (Throwable t) {
							LOGGER.error("Error processing config file change for {}", entry.getKey(), t);
						}
					}
					return readyCallbacks;
				}, WORKER_EXECUTOR).thenAcceptAsync(callbacks -> {
					for (var callback : callbacks) {
						try {
							callback.run();
						} catch (Throwable t) {
							LOGGER.error("Error applying config changes on target thread", t);
						}
					}
				}, executor);

			}, 0L, QUIET_TIME_MS, TimeUnit.MILLISECONDS);
		} finally {
			WATCHER_LOCK.unlock();
		}
	}

	public static void stop() {
		WATCHER_LOCK.lock();
		try {
			if (fileWatcher != null) {
				fileWatcher.shutdown();
				fileWatcher = null;
			}

			if (watchService != null) {
				try {
					watchService.close();
				} catch (IOException e) {
					LOGGER.error("Error closing watch service", e);
				}
				watchService = null;
			}
			WATCHED_DIRECTORIES.clear();
			FILE_CHANGE_HANDLERS.clear();
			INTERNAL_UPDATES.clear();
		} finally {
			WATCHER_LOCK.unlock();
		}
	}

	public static void register(Config<?> config, Runnable task) {
		WATCHER_LOCK.lock();
		try {
			Path filePath = config.filePath();
			Path parentDir = filePath.getParent();
			if (parentDir == null) return;

			FILE_CHANGE_HANDLERS.put(filePath, task);
			try {
				Files.createDirectories(parentDir);
			} catch (IOException e) {
				LOGGER.error("Failed to create config directory {}", parentDir, e);
				return;
			}

			boolean alreadyWatched = WATCHED_DIRECTORIES.containsValue(parentDir);
			if (!alreadyWatched && watchService != null) {
				try {
					WatchKey key = parentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
					WATCHED_DIRECTORIES.put(key, parentDir);
				} catch (IOException e) {
					LOGGER.error("Failed to watch directory {}", parentDir, e);
				}
			}
		} finally {
			WATCHER_LOCK.unlock();
		}
	}

	public static void markInternalUpdate(Path filePath) {
		UPDATES_LOCK.lock();
		try {
			INTERNAL_UPDATES.add(filePath);
		} finally {
			UPDATES_LOCK.unlock();
		}
	}

	private ConfigFileWatcher() {}
}
