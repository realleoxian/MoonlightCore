package de.leoxian.moonlightcore.neoforge.client.particle;

import de.leoxian.moonlightcore.client.particle.ParticleProviderRegistrar;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public record NeoforgeParticleProviderRegistrar(RegisterParticleProvidersEvent event) implements ParticleProviderRegistrar {
    @Override
    public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
        event.registerSpecial(type, provider);
    }

    @Override
    public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, SpriteParticleProvider<T> registration) {
        event.registerSpriteSet(type, registration::create);
    }
}
