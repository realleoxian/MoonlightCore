package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.client.model.BlockStateModelModifier;
import de.realleoxian.moonlightcore.api.event.EventBus;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public final class ModelEvents {
    public static final EventBus<RegisterModelsLocation> REGISTER_MODELS_LOCATION = EventBus.create((listeners) -> (context) -> {
        for (RegisterModelsLocation listener : listeners) {
            listener.onRegisterModelsLocation(context);
        }
    });
    public static final EventBus<ModifyUnbakedModel> MODIFY_UNBAKED_MODEL = EventBus.create((listeners) -> (context) -> {
        for (ModifyUnbakedModel listener : listeners) {
            @Nullable UnbakedModel modified = listener.onModifyUnbakedModel(context);

            if (modified != null) {
                return modified;
            }
        }

        return null;
    });
    public static final EventBus<ModifyBeforeModelBake> MODIFY_BEFORE_MODEL_BAKE = EventBus.create((listeners) -> (context) -> {
       for (ModifyBeforeModelBake listener : listeners) {
           @Nullable UnbakedModel modified = listener.onModifyBeforeBake(context);

           if (modified != null) {
               return modified;
           }
       }

        return null;
    });
    public static final EventBus<ModifyModelBakeResult> MODIFY_MODEL_BAKE_RESULT = EventBus.create((listeners) -> (context) -> {
        for (ModifyModelBakeResult listener : listeners) {
            @Nullable BakedModel modified = listener.onModifyBakeResult(context);

            if (modified != null) {
                return modified;
            }
        }

        return null;
    });
    public static final EventBus<ModifyAfterBake> MODIFY_AFTER_BAKE = EventBus.create((listeners) -> (context) -> {
        for (ModifyAfterBake listener : listeners) {
            listener.onModifyAfterBake(context);
        }
    });

    private ModelEvents() {}

    public interface RegisterModelsLocation {
        void onRegisterModelsLocation(Context context);

        @ApiStatus.NonExtendable
        interface Context {
            void addModel(ResourceLocation location);

            default void addModels(ResourceLocation... locations) {
                Arrays.stream(locations).forEach(this::addModel);
            }

            default void addModels(Iterable<ResourceLocation> locations) {
                locations.forEach(this::addModel);
            }
        }
    }

    public interface ModifyUnbakedModel {
        UnbakedModel onModifyUnbakedModel(ModifyUnbakedModel.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            UnbakedModel getOrLoadModel(ResourceLocation location);

            ResourceLocation getId();

            ModelBakery getModelBakery();
        }
    }

    public interface ModifyBeforeModelBake {
        UnbakedModel onModifyBeforeBake(ModifyBeforeModelBake.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            UnbakedModel getOrLoadModel(ResourceLocation location);

            ResourceLocation getId();

            Function<Material, TextureAtlasSprite> getSpriteGetter();

            ModelBakery getModelBakery();

            ModelState getTransform();
        }
    }

    public interface ModifyModelBakeResult {
        BakedModel onModifyBakeResult(ModifyModelBakeResult.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            ResourceLocation getId();

            ModelBakery getModelBakery();

            Function<Material, TextureAtlasSprite> getSpriteGetter();

            ModelState getTransform();
        }
    }

    public interface ModifyAfterBake {
        void onModifyAfterBake(ModifyAfterBake.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            void setModel(ResourceLocation location, BakedModel model);

            void registerBlockStateModifier(Block block, BlockStateModelModifier modelModifier);

            ModelBakery getModelBakery();

            @UnmodifiableView
            Map<ResourceLocation, BakedModel> getModels();
        }
    }
}
