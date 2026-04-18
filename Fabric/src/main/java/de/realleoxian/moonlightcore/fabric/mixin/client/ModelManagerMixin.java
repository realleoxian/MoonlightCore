package de.realleoxian.moonlightcore.fabric.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.ModelEvents;
import de.realleoxian.moonlightcore.fabric.client.model.FabricModelAfterBakingContext;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @Inject(
            method = "loadModels",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=dispatch",
                    shift = At.Shift.BEFORE
            )
    )
    private void moonlightcore$afterBaking(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        ModelEvents.AFTER_BAKING.invoker().onAfterBaking(new FabricModelAfterBakingContext(modelBakery));
    }
}
