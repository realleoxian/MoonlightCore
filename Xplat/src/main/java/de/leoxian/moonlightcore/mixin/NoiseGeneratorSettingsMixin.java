package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.levelgen.NoiseGeneratorSettingsExtension;
import de.leoxian.moonlightcore.levelgen.SurfaceRuleRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorSettingsExtension {

    @Nullable
    @Unique
    private SurfaceRuleRegistry.Dimension mlcore_levelStemKey = null;

    @Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
    private void mlcore_surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if(this.mlcore_levelStemKey != null) {
            cir.setReturnValue(SurfaceRuleRegistry.getRuleSource(this.mlcore_levelStemKey, cir.getReturnValue()));
        }
    }

    @Override
    public void mlcore_setDimension(SurfaceRuleRegistry.Dimension key) {
        this.mlcore_levelStemKey = key;
    }

}
