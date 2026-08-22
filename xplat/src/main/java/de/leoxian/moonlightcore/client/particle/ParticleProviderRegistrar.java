package de.leoxian.moonlightcore.client.particle;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Consumer;

public interface ParticleProviderRegistrar {
    static void init(String namespace, Consumer<ParticleProviderRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.particles(namespace, initializer);
    }

    <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider);

    <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, SpriteParticleProvider<T> registration);

    interface SpriteParticleProvider<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet spriteSet);
    }
}
