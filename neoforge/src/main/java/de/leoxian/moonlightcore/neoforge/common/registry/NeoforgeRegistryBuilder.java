package de.leoxian.moonlightcore.neoforge.common.registry;

import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber
public class NeoforgeRegistryBuilder<R> implements RegistryBuilder<R> {
    private static final Queue<Registry<?>> PENDING_REGISTRIES = new ConcurrentLinkedQueue<>();

    @SubscribeEvent
    public static void onNewRegistries(NewRegistryEvent event) {
        Registry<?> registry;
        while ((registry = PENDING_REGISTRIES.poll()) != null) {
            event.register(registry);
        }
    }

    private final net.neoforged.neoforge.registries.RegistryBuilder<R> builder;

    public NeoforgeRegistryBuilder(ResourceKey<Registry<R>> registryKey) {
        this.builder = new net.neoforged.neoforge.registries.RegistryBuilder<>(registryKey);
    }

    @Override
    public RegistryBuilder<R> sync(boolean sync) {
        this.builder.sync(sync);
        return this;
    }

    @Override
    public RegistryBuilder<R> defaultId(Identifier id) {
        this.builder.defaultKey(id);
        return this;
    }

    @Override
    public Registry<R> build() {
        // Create the registry instance using NeoForge's builder
        Registry<R> registry = this.builder.create();

        // Queue it for registration during NewRegistryEvent
        PENDING_REGISTRIES.add(registry);

        return registry;
    }
}
