package de.realleoxian.moonlightcore.core.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import net.minecraft.resources.ResourceLocation;

public final class CoreClientConfig {
    public static final CoreClientConfig CONFIG = ModConfig.configure(ModConfig.Type.CLIENT, new ResourceLocation("moonlightcore", "client"), CoreClientConfig::new);

    public final ConfigProperty<Double> cameraShakeIntensityModifier;

    private CoreClientConfig(ConfigSchema.Builder builder) {
        this.cameraShakeIntensityModifier = builder.comment("The intensity modifier used on camera shakes")
                .defineDouble("cameraShakeIntensityModifier", 0.0, 10.0, () -> 1.0);

        builder.build();
    }
}
