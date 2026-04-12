package de.leoxian.moonlightcore.api.misc;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public final class NBTUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <T> void store(CompoundTag outputTag, String key, Codec<T> codec, T value) {
        codec.encodeStart(NbtOps.INSTANCE, value).get()
                .ifRight(partial -> {
                    LOGGER.error("Failed to encode value '{}' to field '{}'. Error:", value, key);
                    LOGGER.error(partial.message());
                }).ifLeft(tag -> outputTag.put(key, tag));
    }

    public static <T> void storeNullable(CompoundTag outputTag, String key, Codec<T> codec, @Nullable T value) {
        if(value != null) {
            store(outputTag, key, codec, value);
        }
    }

    public static <T> Optional<T> readOpt(CompoundTag inputTag, String key, Codec<T> codec) {
        Tag tag = inputTag.get(key);

        if(tag == null) {
            return Optional.empty();
        } else {
            return codec.parse(NbtOps.INSTANCE, tag).get()
                    .ifRight(partial -> {
                        LOGGER.error("Failed to decode value '{}' from field '{}'. Error: ", tag, key);
                        LOGGER.error(partial.message());
                    }).left();
        }
    }

    public static <T> @Nullable T readNullable(CompoundTag inputTag, String key, Codec<T> codec) {
        return readOpt(inputTag, key, codec).orElse(null);
    }

    public static <T> T readOrGet(CompoundTag inputTag, String key, Codec<T> codec, Supplier<T> fallbackValue) {
        return readOpt(inputTag, key, codec).orElseGet(fallbackValue);
    }

    public static <T> T readOrThrow(CompoundTag inputTag, String key, Codec<T> codec) {
        return readOpt(inputTag, key, codec).orElseThrow();
    }

    private NBTUtils() {}
}
