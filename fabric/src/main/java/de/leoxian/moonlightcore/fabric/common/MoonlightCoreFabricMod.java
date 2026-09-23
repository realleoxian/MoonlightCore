package de.leoxian.moonlightcore.fabric.common;

import de.leoxian.moonlightcore.common.event.NewRegistryEvent;
import de.leoxian.moonlightcore.common.event.RegisterEvent;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.fabric.api.MoonlightCoreInitializer;
import de.leoxian.moonlightcore.fabric.common.mixin.accessor.MappedRegistryAccessor;
import de.leoxian.moonlightcore.internal.common.core.MoonlightCore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class MoonlightCoreFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        XplatAbstraction.INSTANCE.initialize();
        MoonlightCore.initialize();

        FabricLoader.getInstance().getEntrypointContainers("moonlightcore", MoonlightCoreInitializer.class).forEach(entrypoint -> {
            MoonlightCoreInitializer initializer = entrypoint.getEntrypoint();
            initializer.onInitialized();
        });

        ((MappedRegistryAccessor) BuiltInRegistries.REGISTRY).setFrozen(false);
        NewRegistryEvent.EVENT.doFire().onNewRegistries(registry -> {
            Identifier registryName = registry.key().identifier();
            if (BuiltInRegistries.REGISTRY.containsKey(registryName)) {
                throw new IllegalStateException("Attempted duplicate registration of registry " + registryName);
            }

            ((WritableRegistry) BuiltInRegistries.REGISTRY).register(registry.key(), registry, RegistrationInfo.BUILT_IN);
        });
        BuiltInRegistries.REGISTRY.freeze();

        Set<Identifier> ordered = getRegistrationOrder();
        for (Identifier registryName : ordered) {
             ResourceKey<? extends Registry<?>> registryKey = ResourceKey.createRegistryKey(registryName);
             Registry<?> registry = Objects.requireNonNull(BuiltInRegistries.REGISTRY.getValue(registryName));

             RegisterEvent.EVENT.doFire().onRegister(registryKey, new RegisterEvent.Output() {
                 @Override
                 public <T> T register(Identifier id, T value) {
                     return Registry.register((Registry<T>) registry, id, value);
                 }
             });
        }
    }

    private static Set<Identifier> getRegistrationOrder() {
        Set<Identifier> ordered = new LinkedHashSet<>();
        ordered.add(Registries.ATTRIBUTE.identifier());
        ordered.add(Registries.DATA_COMPONENT_TYPE.identifier());
        ordered.add(Registries.PARTICLE_TYPE.identifier());
        ordered.addAll(de.leoxian.moonlightcore.fabric.common.mixin.accessor.BuiltInRegistriesAccessor.getLOADERS().keySet());
        ordered.addAll(BuiltInRegistries.REGISTRY.keySet().stream().sorted(MoonlightCoreFabricMod::compareNamespace).toList());
        return ordered;
    }

    private static int compareNamespace(Identifier a, Identifier b) {
        int ret = a.getNamespace().compareTo(b.getNamespace());
        return ret != 0 ? ret : a.getPath().compareTo(b.getPath());
    }
}
