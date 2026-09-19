package de.leoxian.moonlightcore.fabric.client.keymapping;

import de.leoxian.moonlightcore.client.keymapping.KeyMappingRegistrar;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public enum FabricKeyMappingRegistrar implements KeyMappingRegistrar {
    INSTANCE
    ;

    @Override
    public void register(KeyMapping keyMapping) {
        KeyMappingHelper.registerKeyMapping(keyMapping);
    }

    @Override
    public void registerCategory(KeyMapping.Category category) {
        // no-op
    }
}
