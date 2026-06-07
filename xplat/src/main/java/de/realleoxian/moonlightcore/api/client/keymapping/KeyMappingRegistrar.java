package de.realleoxian.moonlightcore.api.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface KeyMappingRegistrar {
    KeyMapping register(KeyMapping keyMapping);

    default KeyMapping register(String name, InputConstants.Type type, int keyCode, String category) {
        return register(new KeyMapping(name, type, keyCode, category));
    }

    default KeyMapping register(String name, int keyCode, String category) {
        return register(new KeyMapping(name, keyCode, category));
    }
}
