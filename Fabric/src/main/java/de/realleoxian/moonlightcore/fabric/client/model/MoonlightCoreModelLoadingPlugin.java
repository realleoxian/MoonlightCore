package de.realleoxian.moonlightcore.fabric.client.model;

import de.realleoxian.moonlightcore.api.client.event.ModelEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MoonlightCoreModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        ModelEvents.REGISTER_MODELS_LOCATION.invoker().onRegisterModelsLocation(pluginContext::addModels);

        pluginContext.modifyModelOnLoad().register((model, context) -> {
            ModelEvents.ModifyModelOnLoad.Context ctx = new ModelEvents.ModifyModelOnLoad.Context() {
                @Override
                public UnbakedModel getOrLoadModel(ResourceLocation location) {
                    return context.getOrLoadModel(location);
                }

                @Override
                public ResourceLocation getId() {
                    return context.id();
                }

                @Override
                public ModelBakery getModelBakery() {
                    return context.loader();
                }
            };

            UnbakedModel modified = ModelEvents.MODIFY_MODEL_ON_LOAD.invoker().onModifyModelOnLoad(model, ctx);
            return modified != null ? modified : model;
        });
        pluginContext.modifyModelBeforeBake().register((model, context) -> {
            var ctx = new ModelEvents.ModifyBeforeBake.Context() {
                @Override
                public ResourceLocation getId() {
                    return context.id();
                }

                @Override
                public ModelBakery getModelBakery() {
                    return context.loader();
                }

                @Override
                public Function<Material, TextureAtlasSprite> getSpriteGetter() {
                    return context.textureGetter();
                }

                @Override
                public ModelState getModelState() {
                    return context.settings();
                }
            };

            UnbakedModel modified = ModelEvents.MODIFY_BEFORE_BAKE.invoker().onModifyBeforeBake(model, ctx);
            return modified != null ? modified : model;
        });
        pluginContext.modifyModelAfterBake().register((model, context) -> {
            var ctx = new ModelEvents.ModifyBakeResult.Context() {
                @Override
                public ResourceLocation getId() {
                    return context.id();
                }

                @Override
                public ModelBakery getModelBakery() {
                    return context.loader();
                }

                @Override
                public Function<Material, TextureAtlasSprite> getSpriteGetter() {
                    return context.textureGetter();
                }

                @Override
                public ModelState getModelState() {
                    return context.settings();
                }
            };

            BakedModel modified = ModelEvents.MODIFY_BAKE_RESULT.invoker().onModifyBakeResult(model, ctx);
            return modified != null ? modified : model;
        });
    }
}
