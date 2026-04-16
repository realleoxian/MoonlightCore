package de.realleoxian.moonlightcore.forge.registry;

import de.realleoxian.moonlightcore.api.registry.RegistryInformation;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.forge.platform.ModEventBusRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.List;

public final class ForgeRegistryInformationRegistrar implements RegistryInformationRegistrar, ModEventBusRegister {
    private final List<RegistryBuilder<?>> registries = new ArrayList<>();

    @Override
    public void registerToEventBus(IEventBus eventBus) {
        eventBus.addListener((NewRegistryEvent event) -> this.registries.forEach(event::create));
    }

    @Override
    public void register(RegistryInformation<?> information) {
        RegistryBuilder<?> forgeBuilder = RegistryBuilder.of(information.name().location());

        if (information.defaultKey() != null) forgeBuilder = forgeBuilder.setDefaultKey(information.defaultKey());
        if (!information.isSync()) forgeBuilder = forgeBuilder.disableSync();
        this.registries.add(forgeBuilder);
    }
}
