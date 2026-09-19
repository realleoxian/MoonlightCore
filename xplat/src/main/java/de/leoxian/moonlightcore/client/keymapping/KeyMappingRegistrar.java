package de.leoxian.moonlightcore.client.keymapping;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface KeyMappingRegistrar {
    /// Configure and register modded key mappings
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void configure(String namespace, Consumer<KeyMappingRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.keyMappings(namespace, initializer);
    }

    /// Register a new key mapping
    /// @param keyMapping The key mapping being registered
    void register(KeyMapping keyMapping);

    /// Register a new key mapping category
    /// @param category The category being registered
    void registerCategory(KeyMapping.Category category);
}
