package de.leoxian.moonlightcore.client.keymapping;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface KeyMappingRegistrar {
    static void init(String namespace, Consumer<KeyMappingRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.keyMappings(namespace, initializer);
    }

    void register(KeyMapping keyMapping);

    void registerCategory(KeyMapping.Category category);
}
