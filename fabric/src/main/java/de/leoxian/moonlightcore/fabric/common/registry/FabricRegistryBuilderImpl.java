package de.leoxian.moonlightcore.fabric.common.registry;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Lifecycle;
import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class FabricRegistryBuilderImpl<R> implements RegistryBuilder<R> {
    private final ResourceKey<Registry<R>> registryKey;
    private boolean synced = false;
    private Identifier defaultId = null;

    public FabricRegistryBuilderImpl(ResourceKey<Registry<R>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public RegistryBuilder<R> sync(boolean sync) {
        this.synced = sync;
        return this;
    }

    @Override
    public RegistryBuilder<R> defaultId(Identifier id) {
        this.defaultId = id;
        return this;
    }

    @Override
    public Supplier<Registry<R>> build() {
        return Suppliers.memoize(() -> {
            Registry<R> registry = this.defaultId != null ?
                    new DefaultedMappedRegistry<>(this.defaultId.toString(), this.registryKey, Lifecycle.stable(), false) :
                    new MappedRegistry<>(this.registryKey, Lifecycle.stable(), false);

            if (this.synced) {
                RegistryAttributeHolder.get(this.registryKey).addAttribute(RegistryAttribute.SYNCED);
            }

            return registry;
        });
    }
}
