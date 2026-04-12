package de.leoxian.moonlightcore.api.client.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ParticleProviderRegistrar {
    <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory);

    <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> particleType, ParticleProvider<T> provider);
}
