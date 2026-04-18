package de.realleoxian.moonlightcore.forge.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.ModelEvents;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Inject(
            method = "getModel",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void moonlightcore$modifyOnLoad(ResourceLocation modelLocation, CallbackInfoReturnable<UnbakedModel> cir) {
        var context = new ModelEvents.ModifyModelOnLoad.Context() {
            @Override
            public UnbakedModel getOrLoadModel(ResourceLocation location) {
                return ModelBakeryMixin.this.getModel(location);
            }

            @Override
            public ResourceLocation getId() {
                return modelLocation;
            }

            @Override
            public ModelBakery getModelBakery() {
                return (ModelBakery) (Object) ModelBakeryMixin.this;
            }
        };

        UnbakedModel model = ModelEvents.MODIFY_MODEL_ON_LOAD.invoker().onModifyModelOnLoad(cir.getReturnValue(), context);
        if (model != null) {
            cir.setReturnValue(model);
        }
    }
}
