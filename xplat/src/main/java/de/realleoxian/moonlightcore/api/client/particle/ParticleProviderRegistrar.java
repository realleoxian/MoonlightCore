package de.realleoxian.moonlightcore.api.client.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public interface ParticleProviderRegistrar {
    <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);

    <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider.Sprite<T> provider);

    <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> func);
}
