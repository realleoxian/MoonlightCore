package de.leowgc.moonlightcore.forge.handler;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.common.RegistryEvents;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryEventHandler {

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        EventDispatcher.INSTANCE.fire(RegistryEvents.NEW_REGISTRY, (listener) -> listener.bootstrap((registry, syncedRegistry) -> {
            RegistryBuilder<?> registryBuilder = RegistryBuilder.of(registry.key().location());

            if(!syncedRegistry) {
                registryBuilder.disableSaving();
            }

            if(registry instanceof DefaultedMappedRegistry<?> defaultedMappedRegistry) {
                registryBuilder.setDefaultKey(defaultedMappedRegistry.getDefaultKey());
            }

            event.create(registryBuilder);
        }));
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        ResourceKey<? extends Registry<?>> currentRegistryKey = event.getRegistryKey();

        EventDispatcher.INSTANCE.fire(RegistryEvents.REGISTER, (listener) -> listener.bootstrap(currentRegistryKey, new RegistryEvents.Register.Output() {
            @Override
            public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation id, Supplier<T> value) {
                event.register(registryKey, id, value::get);
            }
        }));
    }

}
