package de.leoxian.moonlightcore.api.client.model.plugin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

public interface ModifyBeforeBake {
    UnbakedModel onModifyBeforeBake(Context context);

    @ApiStatus.NonExtendable
    interface Context {
        UnbakedModel getOrLoadModel(ResourceLocation location);

        ResourceLocation getId();

        UnbakedModel getOriginal();

        Function<Material, TextureAtlasSprite> getSpriteGetter();

        ModelBakery getModelBakery();

        ModelState getTransform();
    }
}
