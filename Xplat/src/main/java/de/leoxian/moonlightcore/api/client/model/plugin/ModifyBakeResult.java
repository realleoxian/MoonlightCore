package de.leoxian.moonlightcore.api.client.model.plugin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

public interface ModifyBakeResult {
    BakedModel onModifyBakeResult(Context context);

    @ApiStatus.NonExtendable
    interface Context {
        ResourceLocation getId();

        BakedModel getOriginal();

        ModelBakery getModelBakery();

        Function<Material, TextureAtlasSprite> getSpriteGetter();

        ModelState getTransform();
    }
}
