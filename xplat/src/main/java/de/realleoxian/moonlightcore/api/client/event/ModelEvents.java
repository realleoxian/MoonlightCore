package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ModelEvents {
    public static final Event<RegisterModelsLocation> REGISTER_MODELS_LOCATION = Event.create(RegisterModelsLocation.class);
    public static final Event<ModifyModelOnLoad> MODIFY_MODEL_ON_LOAD = Event.create(ModifyModelOnLoad.class);
    public static final Event<ModifyBeforeBake> MODIFY_BEFORE_BAKE = Event.create(ModifyBeforeBake.class);
    public static final Event<ModifyBakeResult> MODIFY_BAKE_RESULT = Event.create(ModifyBakeResult.class);

    private ModelEvents() {}

    public static final class RegisterModelsLocation extends EventBase {
        private final Consumer<ResourceLocation> registerFunc;

        @ApiStatus.Internal
        public RegisterModelsLocation(Consumer<ResourceLocation> registerFunc) {
            this.registerFunc = registerFunc;
        }

        public void addModels(ResourceLocation... modelLocations) {
            Arrays.stream(modelLocations).forEach(this.registerFunc);
        }

        public void addModels(Iterable<ResourceLocation> it) {
            it.forEach(this.registerFunc);
        }
    }

    public static final class ModifyModelOnLoad extends EventBase {
        public final UnbakedModel originalModel;
        public final ResourceLocation id;
        public final ModelBakery modelBakery;
        public final Function<ResourceLocation, UnbakedModel> loadFunction;
        public UnbakedModel model;

        @ApiStatus.Internal
        public ModifyModelOnLoad(UnbakedModel originalModel, ResourceLocation id, ModelBakery modelBakery, Function<ResourceLocation, UnbakedModel> loadFunction) {
            this.originalModel = originalModel;
            this.id = id;
            this.modelBakery = modelBakery;
            this.model = originalModel;
            this.loadFunction = loadFunction;
        }

        public UnbakedModel getOrLoadModel(ResourceLocation location) {
            return this.loadFunction.apply(location);
        }
    }

    public static final class ModifyBeforeBake extends EventBase {
        public final UnbakedModel originalModel;
        public final ResourceLocation id;
        public final ModelBakery modelBakery;
        public final ModelState modelState;
        public final Function<Material, TextureAtlasSprite> spriteGetter;
        public UnbakedModel model;

        @ApiStatus.Internal
        public ModifyBeforeBake(UnbakedModel originalModel, ResourceLocation id, ModelBakery modelBakery, ModelState modelState, Function<Material, TextureAtlasSprite> spriteGetter) {
            this.originalModel = originalModel;
            this.id = id;
            this.modelBakery = modelBakery;
            this.modelState = modelState;
            this.spriteGetter = spriteGetter;
            this.model = originalModel;
        }
    }

    public static final class ModifyBakeResult extends EventBase {
        public final BakedModel originalModel;
        public final ResourceLocation id;
        public final ModelBakery modelBakery;
        public final ModelState modelState;
        public final Function<Material, TextureAtlasSprite> spriteGetter;
        public BakedModel model;

        @ApiStatus.Internal
        public ModifyBakeResult(BakedModel originalModel, ResourceLocation id, ModelBakery modelBakery, ModelState modelState, Function<Material, TextureAtlasSprite> spriteGetter) {
            this.originalModel = originalModel;
            this.id = id;
            this.modelBakery = modelBakery;
            this.modelState = modelState;
            this.spriteGetter = spriteGetter;
            this.model = originalModel;
        }
    }
}
