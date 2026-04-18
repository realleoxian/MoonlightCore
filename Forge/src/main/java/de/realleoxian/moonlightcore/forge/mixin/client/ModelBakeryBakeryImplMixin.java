package de.realleoxian.moonlightcore.forge.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.ModelEvents;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(ModelBakery.ModelBakerImpl.class)
public class ModelBakeryBakeryImplMixin {
    @Shadow
    @Final
    ModelBakery this$0;
    @Shadow
    @Final
    private Function<Material, TextureAtlasSprite> modelTextureGetter;

    @ModifyVariable(
            method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/resources/model/ModelBakery$ModelBakerImpl;getModel(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/UnbakedModel;"
            )
    )
    private UnbakedModel moonlightcore$modifyBeforeBake(UnbakedModel model, ResourceLocation location, ModelState state) {
        var context = new ModelEvents.ModifyBeforeBake.Context() {
            @Override
            public ResourceLocation getId() {
                return location;
            }

            @Override
            public ModelBakery getModelBakery() {
                return ModelBakeryBakeryImplMixin.this.this$0;
            }

            @Override
            public Function<Material, TextureAtlasSprite> getSpriteGetter() {
                return ModelBakeryBakeryImplMixin.this.modelTextureGetter;
            }

            @Override
            public ModelState getModelState() {
                return state;
            }
        };

        var modified = ModelEvents.MODIFY_BEFORE_BAKE.invoker().onModifyBeforeBake(model, context);
        return modified != null ? modified : model;
    }

    @Redirect(
            method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/UnbakedModel;bake(Lnet/minecraft/client/resources/model/ModelBaker;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/BakedModel;"
            )
    )
    private BakedModel moonlightcore$modifyAfterBake(UnbakedModel instance, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> materialTextureAtlasSpriteFunction, ModelState state, ResourceLocation location) {
        BakedModel model = instance.bake(modelBaker, materialTextureAtlasSpriteFunction, state, location);

        var context = new ModelEvents.ModifyBakeResult.Context() {
            @Override
            public ResourceLocation getId() {
                return location;
            }

            @Override
            public ModelBakery getModelBakery() {
                return ModelBakeryBakeryImplMixin.this.this$0;
            }

            @Override
            public Function<Material, TextureAtlasSprite> getSpriteGetter() {
                return ModelBakeryBakeryImplMixin.this.modelTextureGetter;
            }

            @Override
            public ModelState getModelState() {
                return state;
            }
        };

        var modified = ModelEvents.MODIFY_BAKE_RESULT.invoker().onModifyBakeResult(model, context);
        return modified != null ? modified : model;
    }

    @Redirect(
            method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/BlockModel;bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;"
            )
    )
    private BakedModel moonlightcore$modifyAfterBake(BlockModel instance, ModelBaker baker, BlockModel parent, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location, boolean guiLight3d) {
        BakedModel model = instance.bake(baker, parent, spriteGetter, state, location, guiLight3d);

        var context = new ModelEvents.ModifyBakeResult.Context() {
            @Override
            public ResourceLocation getId() {
                return location;
            }

            @Override
            public ModelBakery getModelBakery() {
                return ModelBakeryBakeryImplMixin.this.this$0;
            }

            @Override
            public Function<Material, TextureAtlasSprite> getSpriteGetter() {
                return ModelBakeryBakeryImplMixin.this.modelTextureGetter;
            }

            @Override
            public ModelState getModelState() {
                return state;
            }
        };

        var modified = ModelEvents.MODIFY_BAKE_RESULT.invoker().onModifyBakeResult(model, context);
        return modified != null ? modified : model;
    }
}
