package de.leoxian.moonlightcore.impl.internal.config;

import de.leoxian.moonlightcore.api.config.ModConfig;
import de.leoxian.moonlightcore.api.config.schema.ConfigProperty;
import de.leoxian.moonlightcore.api.config.schema.ConfigSchema;
import net.minecraft.resources.ResourceLocation;

public final class InternalClientConfig {
    public static final InternalClientConfig CONFIG = ModConfig.configure(ModConfig.Type.CLIENT, new ResourceLocation("moonlightcore", "client"), InternalClientConfig::new);

    public final ConfigProperty<Double> cameraShakeIntensityModifier;

    private InternalClientConfig(ConfigSchema.Builder builder) {
        this.cameraShakeIntensityModifier = builder.comment("The intensity modifier of camera shakes").defineDouble("cameraShakeIntensityModifier", 0.0D, 10.0D, () -> 0.0D);
    }
}
