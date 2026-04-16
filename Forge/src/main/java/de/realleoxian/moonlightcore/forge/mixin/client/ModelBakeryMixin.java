package de.realleoxian.moonlightcore.forge.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.realleoxian.moonlightcore.api.client.event.ModelEvents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @ModifyReturnValue(
            method = "getModel",
            at = @At(
                    value = "RETURN"
            )
    )
    private UnbakedModel moonlightcore$getModel(UnbakedModel original, ResourceLocation modelLocation) {
        ModelEvents.ModifyUnbakedModel.Context context = new ModelEvents.ModifyUnbakedModel.Context() {
            @Override
            public UnbakedModel getOrLoadModel(ResourceLocation location) {
                return getModel(location);
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

        UnbakedModel modified = ModelEvents.MODIFY_UNBAKED_MODEL.invoker().onModifyUnbakedModel(context);
        return modified != null ? modified : original;
    }

    @Redirect(
            method = "lambda$bakeModels$9",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelBakery$ModelBakerImpl;bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;"
            )
    )
    private BakedModel moonlightcore$bakeModels(ModelBakery.ModelBakerImpl instance, ResourceLocation p_252176_, ModelState p_249765_, BiFunction<ResourceLocation, Material, TextureAtlasSprite> p_248669_) {
        BakedModel original = instance.bake(p_252176_, p_249765_);
        ModelEvents.ModifyModelBakeResult.Context context = new ModelEvents.ModifyModelBakeResult.Context() {
            @Override
            public ResourceLocation getId() {
                return p_252176_;
            }

            @Override
            public ModelBakery getModelBakery() {
                return (ModelBakery) (Object) ModelBakeryMixin.this;
            }

            @Override
            public Function<Material, TextureAtlasSprite> getSpriteGetter() {
                return material -> p_248669_.apply(p_252176_, material);
            }
        };

        BakedModel modified = ModelEvents.MODIFY_MODEL_BAKE_RESULT.invoker().onModifyBakeResult(context);
        return modified != null ? modified : original;
    }
}
