package de.leoxian.moonlightcore.internal.common.world.storage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import de.leoxian.moonlightcore.common.world.storage.GlobalStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class GlobalStorageManager extends SavedData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalStorageManager.class);
    private static final Map<Identifier, GlobalStorage> STORAGES = new HashMap<>();

    public static Codec<GlobalStorageManager> makeCodec(MinecraftServer server) {
        return Codec.of(new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(GlobalStorageManager input, DynamicOps<T> ops, T prefix) {
                try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
                    TagValueOutput output = TagValueOutput.createWithContext(reporter, server.registryAccess());
                    for (final GlobalStorage storage : STORAGES.values()) {
                        ValueOutput entryOutput = output.child(storage.getId().toString());
                        storage.serialize(entryOutput);
                    }
                    return DataResult.success(NbtOps.INSTANCE.convertTo(ops, output.buildResult()));
                }
            }
        }, new Decoder<>() {
            @Override
            public <T> DataResult<Pair<GlobalStorageManager, T>> decode(DynamicOps<T> ops, T input) {
                try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
                    ValueInput tagValueInput = TagValueInput.create(reporter, server.registryAccess(), (CompoundTag) ops.convertTo(NbtOps.INSTANCE, input));
                    for (final GlobalStorage storage : STORAGES.values()) {
                        ValueInput storageEntry = tagValueInput.childOrEmpty(storage.getId().toString());
                        storage.deserialize(storageEntry);
                    }
                    return DataResult.success(Pair.of(new GlobalStorageManager(), ops.empty()));
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <S extends GlobalStorage> S getOrCreate(Identifier id, Supplier<S> storage) {
        return (S) STORAGES.computeIfAbsent(id, _ -> storage.get());
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
