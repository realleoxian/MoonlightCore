package de.leoxian.moonlightcore.api.client.model.plugin;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public interface AfterBaking {
    void onModifyAfterBaking();

    interface Context {
        void setModel(ResourceLocation location, BakedModel model);

        ModelBakery getModelBakery();

        @UnmodifiableView
        Map<ResourceLocation, BakedModel> getModels();
    }
}
