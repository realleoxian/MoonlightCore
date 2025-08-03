package de.leowgc.moonlightcore.mixin;

import de.leowgc.moonlightcore.api.world.surface.SurfaceRuleRegistry;
import de.leowgc.moonlightcore.world.NoiseGeneratorSettingsExtension;
import de.leowgc.moonlightcore.world.surface.SurfaceRuleRegistryImpl;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorSettingsExtension {

    @Unique
    private SurfaceRuleRegistry.Dimension mlcore_dimension;

    @Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
    public void mlcore_surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if(this.mlcore_dimension != null) {
            cir.setReturnValue(SurfaceRuleRegistryImpl.get(this.mlcore_dimension).merge(cir.getReturnValue()));
        }
    }

    @Override
    public void mlcore_setDimension(SurfaceRuleRegistry.Dimension dimension) {
        this.mlcore_dimension = dimension;
    }
}
