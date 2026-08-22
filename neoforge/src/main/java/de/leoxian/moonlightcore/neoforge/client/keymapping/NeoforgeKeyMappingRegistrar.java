package de.leoxian.moonlightcore.neoforge.client.keymapping;

import de.leoxian.moonlightcore.client.keymapping.KeyMappingRegistrar;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public record NeoforgeKeyMappingRegistrar(RegisterKeyMappingsEvent event) implements KeyMappingRegistrar {
    @Override
    public void register(KeyMapping keyMapping) {
        event.register(keyMapping);
    }

    @Override
    public void registerCategory(KeyMapping.Category category) {
        event.registerCategory(category);
    }
}
