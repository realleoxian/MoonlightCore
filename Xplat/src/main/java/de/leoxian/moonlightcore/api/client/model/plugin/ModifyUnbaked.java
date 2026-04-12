package de.leoxian.moonlightcore.api.client.model.plugin;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public interface ModifyUnbaked {
    UnbakedModel onModifyUnbakedModel(Context context);

    @ApiStatus.NonExtendable
    interface Context {
        UnbakedModel getOrLoadModel(ResourceLocation location);

        UnbakedModel getOriginal();

        ResourceLocation getId();

        ModelBakery getModelBakery();
    }
}
