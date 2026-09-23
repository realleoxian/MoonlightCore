package de.leoxian.moonlightcore.fabric.client.particle;

import de.leoxian.moonlightcore.client.particle.ParticleProviderRegistrar;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public enum FabricParticleProviderRegistrar implements ParticleProviderRegistrar {
	INSTANCE
	;

	@Override
	public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
		ParticleProviderRegistry.getInstance().register(type, provider);
	}

	@Override
	public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, SpriteParticleProvider<T> provider) {
		ParticleProviderRegistry.getInstance().register(type, provider::create);
	}
}
