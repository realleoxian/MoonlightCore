package de.leoxian.moonlightcore.fabric.common.registry;

import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

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
    public Registry<R> build() {
        if (this.defaultId != null) {
            FabricRegistryBuilder<R, DefaultedMappedRegistry<R>> builder = FabricRegistryBuilder.createDefaulted(this.registryKey, this.defaultId);
            if (synced) {
                builder = builder.attribute(RegistryAttribute.SYNCED);
            }
            return builder.buildAndRegister();
        }

        FabricRegistryBuilder<R, MappedRegistry<R>> builder = FabricRegistryBuilder.create(this.registryKey);
        if (synced) {
            builder = builder.attribute(RegistryAttribute.SYNCED);
        }
        return builder.buildAndRegister();
    }
}
