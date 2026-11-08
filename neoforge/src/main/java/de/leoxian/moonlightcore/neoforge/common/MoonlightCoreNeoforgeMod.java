package de.leoxian.moonlightcore.neoforge.common;

import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.neoforge.common.level.biome.modifier.MoonlightCoreBiomeModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(value = "moonlightcore")
public class MoonlightCoreNeoforgeMod {
	public MoonlightCoreNeoforgeMod(IEventBus eventBus) {
		XplatAbstraction.INSTANCE.initialize();
		MoonlightCore.init();

		registerBiomeModifier(eventBus);
	}

	private void registerBiomeModifier(IEventBus eventBus) {
		eventBus.addListener(RegisterEvent.class, event -> {
			if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS)) {
				event.register(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MoonlightCore.id("moonlightcore"), () -> MoonlightCoreBiomeModifier.CODEC);
			}
		});
	}
}
