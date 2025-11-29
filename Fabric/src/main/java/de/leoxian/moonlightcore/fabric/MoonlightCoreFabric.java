package de.leoxian.moonlightcore.fabric;

import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.event.common.*;
import de.leoxian.moonlightcore.fabric.api.MoonlightCoreInitializer;
import de.leoxian.moonlightcore.fabric.mixin.accessor.BuiltInRegistriesAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MoonlightCoreFabric implements ModInitializer {

     @Override
     public void onInitialize() {
         MoonlightCore.initialize();

         FabricLoader.getInstance().getEntrypointContainers("moonlightcore", MoonlightCoreInitializer.class).stream().map(EntrypointContainer::getEntrypoint).forEach(MoonlightCoreInitializer::onModInitialize);
         this.setupRegistryEvents();

         EntityEvent.ATTRIBUTE_CREATION.invoker().onEntityAttributeCreation(FabricDefaultAttributeRegistry::register);

         CommonLifecycleEvent.SETUP.invoker().run();

         ServerLifecycleEvents.SERVER_STARTING.register((server) -> ServerLifecycleEvent.ABOUT_TO_START.invoker().onLifecycleState(server));
         ServerLifecycleEvents.SERVER_STARTED.register((server) -> ServerLifecycleEvent.STARTED.invoker().onLifecycleState(server));
         ServerLifecycleEvents.SERVER_STOPPING.register((server) -> ServerLifecycleEvent.STOPPING.invoker().onLifecycleState(server));
         ServerLifecycleEvents.SERVER_STOPPED.register((server) -> ServerLifecycleEvent.STOPPED.invoker().onLifecycleState(server));

         ServerTickEvents.START_SERVER_TICK.register((server) -> TickEvent.SERVER_TICK.invoker().onServerTick(TickEvent.Phase.START, server));
         ServerTickEvents.END_SERVER_TICK.register((server) -> TickEvent.SERVER_TICK.invoker().onServerTick(TickEvent.Phase.END, server));

         ServerTickEvents.START_WORLD_TICK.register((level) -> TickEvent.LEVEL_TICK.invoker().onLevelTick(TickEvent.Phase.START, level, level.isClientSide()));
         ServerTickEvents.END_WORLD_TICK.register((level) -> TickEvent.LEVEL_TICK.invoker().onLevelTick(TickEvent.Phase.END, level, level.isClientSide()));

         ServerWorldEvents.LOAD.register((server, level) -> ServerLevelLifecycleEvent.LOAD.invoker().onLifecycleState(level));
         ServerWorldEvents.UNLOAD.register((server, level) -> ServerLevelLifecycleEvent.UNLOAD.invoker().onLifecycleState(level));

         CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RegisterCommandsEvent.EVENT.invoker().onCommandRegistration(dispatcher, environment, registryAccess));
     }

     @SuppressWarnings("unchecked")
     private void setupRegistryEvents() {
         Set<ResourceLocation> registryOrder = new HashSet<>(BuiltInRegistriesAccessor.getLOADERS().keySet());

         RegistryCreationEvent.EVENT.invoker().onRegistryCreation(new RegistryCreationEvent.Output() {
             @Override
             // I hate this code
             public <T> void register(Registry<T> registry, boolean sync) {
                 ResourceKey<? extends Registry<T>> key = registry.key();

                 if(registry instanceof MappedRegistry<T>) {
                     FabricRegistryBuilder<T, MappedRegistry<T>> builder = FabricRegistryBuilder.createSimple((ResourceKey<Registry<T>>) key);

                     if(sync) {
                        builder = builder.attribute(RegistryAttribute.SYNCED);
                     }

                     builder.buildAndRegister();
                 } else if (registry instanceof DefaultedMappedRegistry<T>) {
                    FabricRegistryBuilder<T, DefaultedMappedRegistry<T>> builder = FabricRegistryBuilder.createDefaulted((ResourceKey<Registry<T>>) key, ((DefaultedRegistry<T>) registry).getDefaultKey());

                    if(sync) {
                        builder = builder.attribute(RegistryAttribute.SYNCED);
                    }

                     builder.buildAndRegister();
                 }

                 registryOrder.add(registry.key().location());
             }
         });

         for(var registryId : registryOrder) {
            var registry =  BuiltInRegistries.REGISTRY.get(registryId);

            if(registry != null) {
                RegisterEvent.EVENT.invoker().onRegistration(registry.key(), new RegisterEvent.Output() {
                    @Override
                    public <T> void register(ResourceLocation id, Supplier<T> value) {
                        Registry.register((Registry<? super T>) registry, id, value.get());
                    }
                });
            }
         }
     }
}
