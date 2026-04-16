package de.realleoxian.moonlightcore.forge.client.keymapping;

import de.realleoxian.moonlightcore.api.client.keymapping.KeyMappingRegistrar;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ForgeKeyMappingRegistrar implements KeyMappingRegistrar {
    private final List<KeyMapping> keyMappings = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterKeyMapping(RegisterKeyMappingsEvent event) {
        this.keyMappings.forEach(event::register);
    }

    @Override
    public KeyMapping register(KeyMapping keyMapping) {
        this.keyMappings.add(keyMapping);
        return keyMapping;
    }
}
