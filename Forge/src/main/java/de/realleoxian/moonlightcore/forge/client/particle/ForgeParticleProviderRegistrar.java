package de.realleoxian.moonlightcore.forge.client.particle;

import de.realleoxian.moonlightcore.api.client.particle.ParticleProviderRegistrar;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeParticleProviderRegistrar implements ParticleProviderRegistrar {
    private final List<SpriteSetRegistration<?>> spriteSetRegistrations = new ArrayList<>();
    private final List<Registration<?>> registrations = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        this.spriteSetRegistrations.forEach(r -> r.register(event));
        this.registrations.forEach(r -> r.register(event));
    }

    @Override
    public <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory) {
        this.spriteSetRegistrations.add(new SpriteSetRegistration<>(particleType, factory));
    }

    @Override
    public <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> particleType, ParticleProvider<T> provider) {
        this.registrations.add(new Registration<>(particleType, provider));
    }

    private record SpriteSetRegistration<T extends ParticleOptions>(Supplier<? extends ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory) {
        public void register(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(particleType.get(), factory::apply);
        }
    }

    private record Registration<T extends ParticleOptions>(Supplier<? extends ParticleType<T>> particleType, ParticleProvider<T> provider) {
        public void register(RegisterParticleProvidersEvent event) {
            event.registerSpecial(particleType.get(), provider);
        }
    }
}
