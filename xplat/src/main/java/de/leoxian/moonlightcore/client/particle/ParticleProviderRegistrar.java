package de.leoxian.moonlightcore.client.particle;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Consumer;

public interface ParticleProviderRegistrar {
    /// Configure and register particle providers
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void configure(String namespace, Consumer<ParticleProviderRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.particles(namespace, initializer);
    }

    /// Register a [ParticleProvider] for the given particle type
    /// @param type The particle type
    /// @param provider The particle provider
    <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider);

    /// Register a particle provider with access to the sprite set
    /// @param type The particle type
    /// @param provider The particle provider
    <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, SpriteParticleProvider<T> provider);

    interface SpriteParticleProvider<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet spriteSet);
    }
}
