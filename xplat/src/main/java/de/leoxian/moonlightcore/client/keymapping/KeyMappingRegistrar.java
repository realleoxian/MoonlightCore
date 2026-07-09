package de.leoxian.moonlightcore.client.keymapping;

import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface KeyMappingRegistrar {
    void register(KeyMapping keyMapping);

    void registerCategory(KeyMapping.Category category);
}
