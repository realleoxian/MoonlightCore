package de.leowgc.moonlightcore.fabric;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.common.RegistryEvents;
import de.leowgc.moonlightcore.api.event.common.CommandRegistrationEvent;
import de.leowgc.moonlightcore.api.event.server.ServerLifecycleEvent;
import de.leowgc.moonlightcore.api.event.server.ServerTickEvent;
import de.leowgc.moonlightcore.core.MoonlightCore;
import de.leowgc.moonlightcore.fabric.api.MoonlightCoreInitializer;
import de.leowgc.moonlightcore.mixin.BuiltInRegistriesAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class MoonlightCoreFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MoonlightCore.init();

        FabricLoader.getInstance().getEntrypointContainers("moonlightcore", MoonlightCoreInitializer.class).stream().map(EntrypointContainer::getEntrypoint).forEach(MoonlightCoreInitializer::onInitialize);

        fireRegistryEvents();

        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> EventDispatcher.INSTANCE.fire(CommandRegistrationEvent.COMMAND_REGISTRATION, (listener) -> listener.bootstrap(dispatcher, environment, buildContext)));

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STARTING, (listener) -> listener.bootstrap(server)));
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STARTED, (listener) -> listener.bootstrap(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STOPPING, (listener) -> listener.bootstrap(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STOPPED, (listener) -> listener.bootstrap(server)));

        ServerTickEvents.START_SERVER_TICK.register((server) -> EventDispatcher.INSTANCE.fire(ServerTickEvent.SERVER_TICK, (listener) -> listener.bootstrap(server, ServerTickEvent.Phase.START)));
        ServerTickEvents.END_SERVER_TICK.register((server) -> EventDispatcher.INSTANCE.fire(ServerTickEvent.SERVER_TICK, (listener) -> listener.bootstrap(server, ServerTickEvent.Phase.END)));
    }

    @SuppressWarnings("unchecked")
    private void fireRegistryEvents() {
        EventDispatcher.INSTANCE.fire(RegistryEvents.NEW_REGISTRY, (listener) -> listener.bootstrap((registry, syncedRegistry) -> {
            FabricRegistryBuilder<?, ?> builder = FabricRegistryBuilder.from(registry);

            if(syncedRegistry) {
                builder = builder.attribute(RegistryAttribute.SYNCED);
            }

            builder.buildAndRegister();
        }));

        for(ResourceLocation registryName : BuiltInRegistriesAccessor.getLoaders().keySet()) {
            ResourceKey<? extends Registry<?>> registryKey = ResourceKey.createRegistryKey(registryName);
            Registry<?> registry = BuiltInRegistries.REGISTRY.get(registryName);

            EventDispatcher.INSTANCE.fire(RegistryEvents.REGISTER, (listener) -> listener.bootstrap(registryKey, new RegistryEvents.Register.Output() {
                @Override
                public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation id, Supplier<T> value) {
                    Registry.register((Registry<? super T>) registry, id, value.get());
                }
            }));
        }
    }

}
