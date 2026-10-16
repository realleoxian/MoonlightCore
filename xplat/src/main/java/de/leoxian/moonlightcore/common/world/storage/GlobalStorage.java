package de.leoxian.moonlightcore.common.world.storage;

import de.leoxian.moonlightcore.internal.common.world.storage.GlobalStorageManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Supplier;

public interface GlobalStorage {
    static <S extends GlobalStorage> S getOrCreate(Identifier id, Supplier<S> storage) {
        return GlobalStorageManager.getOrCreate(id, storage);
    }

    void serialize(ValueOutput output);

    void deserialize(ValueInput input);

    Identifier getId();
}
