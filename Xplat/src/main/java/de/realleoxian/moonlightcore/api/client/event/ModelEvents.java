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
    public static final EventBus<RegisterModelsLocation> REGISTER_MODELS_LOCATION = EventBus.create(RegisterModelsLocation.class, (listeners) -> (context) -> {
        for (RegisterModelsLocation listener : listeners) {
            listener.onRegisterModelsLocation(context);
        }
    });
    public static final EventBus<ModifyModelOnLoad> MODIFY_MODEL_ON_LOAD = EventBus.create(ModifyModelOnLoad.class, (listeners) -> (model,context) -> {
        for (ModifyModelOnLoad listener : listeners) {
            @Nullable UnbakedModel modified = listener.onModifyModelOnLoad(model, context);

            if (modified != null) {
                return modified;
            }
        }

        return null;
    });
    public static final EventBus<ModifyBeforeBake> MODIFY_BEFORE_BAKE = EventBus.create(ModifyBeforeBake.class, (listeners) -> (model, context) -> {
        for (ModifyBeforeBake listener : listeners) {
            @Nullable UnbakedModel modified = listener.onModifyBeforeBake(model, context);

            if (modified != null) {
                return modified;
            }
        }

        return null;
    });
    public static final EventBus<ModifyBakeResult> MODIFY_BAKE_RESULT = EventBus.create(ModifyBakeResult.class, (listeners) -> (model, context) -> {
        for (ModifyBakeResult listener : listeners) {
            @Nullable BakedModel modified = listener.onModifyBakeResult(model, context);

            if (modified != null) {
                return modified;
            }
        }

        return null;
    });
    public static final EventBus<AfterBaking> AFTER_BAKING = EventBus.create(AfterBaking.class, (listeners) -> (context) -> {
        for (AfterBaking listener : listeners) {
            listener.onAfterBaking(context);
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

    public interface ModifyModelOnLoad {
        UnbakedModel onModifyModelOnLoad(UnbakedModel model, ModifyModelOnLoad.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            UnbakedModel getOrLoadModel(ResourceLocation location);

            ResourceLocation getId();

            ModelBakery getModelBakery();
        }
    }

    public interface ModifyBeforeBake {
        UnbakedModel onModifyBeforeBake(UnbakedModel model, ModifyBeforeBake.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            ResourceLocation getId();

            ModelBakery getModelBakery();

            Function<Material, TextureAtlasSprite> getSpriteGetter();

            ModelState getModelState();
        }
    }

    public interface ModifyBakeResult {
        BakedModel onModifyBakeResult(BakedModel model, ModifyBakeResult.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            ResourceLocation getId();

            ModelBakery getModelBakery();

            Function<Material, TextureAtlasSprite> getSpriteGetter();

            ModelState getModelState();
        }
    }

    public interface AfterBaking {
        void onAfterBaking(AfterBaking.Context context);

        @ApiStatus.NonExtendable
        interface Context {
            void setModel(ResourceLocation location, BakedModel model);

            void modifyBlockStateModels(Block block, BlockStateModelModifier modelModifier);

            ModelBakery getModelBakery();

            @UnmodifiableView
            Map<ResourceLocation, BakedModel> getModels();
        }
    }
}
