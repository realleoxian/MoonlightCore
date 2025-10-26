package de.leoxian.moonlightcore.forge.handler;

import de.leoxian.moonlightcore.event.common.CommonLifecycleEvent;
import de.leoxian.moonlightcore.event.common.RegistryCreationEvent;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusCommonEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CommonLifecycleEvent.SETUP.invoker().run();
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRegisterEvent(RegisterEvent event) {
        var registryKey = event.getRegistryKey();
        de.leoxian.moonlightcore.event.common.RegisterEvent.EVENT.invoker().onRegistration(registryKey, new de.leoxian.moonlightcore.event.common.RegisterEvent.Output() {
            @Override
            public <T> void register(ResourceLocation id, Supplier<T> value) {
                event.register((ResourceKey<? extends Registry<T>>) registryKey, id, value);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RegistryCreationEvent.EVENT.invoker().onRegistryCreation(new RegistryCreationEvent.Output() {
            @Override
            public <T> void register(Registry<T> registry, boolean sync) {
                RegistryBuilder<T> builder = new RegistryBuilder<T>().setName(registry.key().location());

                if(!sync) {
                    builder = builder.disableSync();
                }

                if(registry instanceof DefaultedRegistry<?>) {
                    builder = builder.setDefaultKey(((DefaultedRegistry<T>) registry).getDefaultKey());
                }

                event.create(builder).get();
            }
        });
    }

}
