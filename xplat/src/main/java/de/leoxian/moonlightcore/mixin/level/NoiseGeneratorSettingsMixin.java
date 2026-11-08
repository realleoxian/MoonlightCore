package de.leoxian.moonlightcore.mixin.level;

import de.leoxian.moonlightcore.common.level.SurfaceRuleRegistry;
import de.leoxian.moonlightcore.internal.common.level.NoiseGeneratorSettingsExtension;
import de.leoxian.moonlightcore.internal.common.level.SurfaceRuleRegistryImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorSettingsExtension {
    @Unique
    @Nullable
    private ResourceKey<LevelStem> moonlightcore$dimensionKey = null;

    @Inject(
            method = "surfaceRule",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (this.moonlightcore$dimensionKey != null) {
            SurfaceRules.RuleSource vanilla = cir.getReturnValue();
            cir.setReturnValue(((SurfaceRuleRegistryImpl) SurfaceRuleRegistry.get(this.moonlightcore$dimensionKey)).getOrCreate(vanilla));
        }
    }

    @Override
    public void moonlightcore$setDimensionKey(ResourceKey<LevelStem> key) {
        this.moonlightcore$dimensionKey = key;
    }
}
