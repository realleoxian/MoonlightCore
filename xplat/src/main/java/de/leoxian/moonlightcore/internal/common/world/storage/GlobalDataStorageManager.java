package de.leoxian.moonlightcore.internal.common.world.storage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import de.leoxian.moonlightcore.common.event.ServerLifecycleEvents;
import de.leoxian.moonlightcore.common.world.storage.GlobalDataStorage;
import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class GlobalDataStorageManager extends SavedData {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalDataStorageManager.class);
	static final Map<Identifier, Supplier<? extends GlobalDataStorage>> FACTORIES = new HashMap<>();
	static final Map<Identifier, GlobalDataStorage> STORAGES = new HashMap<>();
	static final GlobalDataStorageManager INSTANCE = new GlobalDataStorageManager();
	static boolean loaded = false;

	static {
		ServerLifecycleEvents.STARTING.subscribe(server -> {
			SavedDataType<GlobalDataStorageManager> type = new SavedDataType<>(
					MoonlightCore.id("data_storage"),
					() -> INSTANCE,
					GlobalDataStorageManager.codec(server),
					(DataFixTypes) null
			);

			server.getDataStorage().computeIfAbsent(type);
			GlobalDataStorageManager.loaded = true;
		});

		ServerLifecycleEvents.STOPPED.subscribe(server -> {
			GlobalDataStorageManager.resetStorages();
		});
	}

	public static void init() {
		// no-op
	}

	@SuppressWarnings("unchecked")
	public static <T extends GlobalDataStorage> T getOrCreate(final Identifier id) {
		GlobalDataStorage storage = STORAGES.get(id);
		if (storage == null) {
			Supplier<? extends GlobalDataStorage> factory = FACTORIES.get(id);
			if (factory == null) {
				throw new IllegalStateException("May not try to get some global data storage that isn't registered or doesn't exists '" + id + "'");
			}
			storage = factory.get();
			STORAGES.put(id, storage);
		}
		return (T) storage;
	}

	public static Codec<GlobalDataStorageManager> codec(MinecraftServer server) {
		return Codec.of(new Encoder<>() {
			@Override
			public <T> DataResult<T> encode(GlobalDataStorageManager input, DynamicOps<T> ops, T prefix) {
				try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
					TagValueOutput output = TagValueOutput.createWithContext(reporter, server.registryAccess());
					for (final var entry : STORAGES.entrySet()) {
						ValueOutput entryOutput = output.child(entry.getKey().toString());
						entry.getValue().serialize(entryOutput);
					}
					return DataResult.success(NbtOps.INSTANCE.convertTo(ops, output.buildResult()));
				}
			}
		}, new Decoder<>() {
			@Override
			public <T> DataResult<Pair<GlobalDataStorageManager, T>> decode(DynamicOps<T> ops, T input) {
				try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
					CompoundTag tag = (CompoundTag) ops.convertTo(NbtOps.INSTANCE, input);
					ValueInput tagValueInput = TagValueInput.create(reporter, server.registryAccess(), tag);
					for (final String tagKey : tag.keySet()) {
						Identifier id = Identifier.tryParse(tagKey);
						if (id == null) {
							continue;
						}

						GlobalDataStorage storage = STORAGES.get(id);
						if (storage == null) {
							Supplier<? extends GlobalDataStorage> factory = FACTORIES.get(id);
							if (factory == null) {
								LOGGER.warn("Found global data storage '{}', but it doesn't have a factory registered, skipping.", id);
								continue;
							}
							storage = factory.get();
							STORAGES.put(id, storage);
						}

						ValueInput storageInput = tagValueInput.child(tagKey).orElseThrow();
						storage.reset();
						storage.deserialize(storageInput);
					}
					return DataResult.success(Pair.of(INSTANCE, ops.empty()));
				}
			}
		});
	}

	@ApiStatus.Internal
	public static void resetStorages() {
		for (final GlobalDataStorage storage : STORAGES.values()) {
			storage.reset();
		}
		loaded = false;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	private GlobalDataStorageManager() {
	}
}
