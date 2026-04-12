package de.leoxian.moonlightcore.api.client.model.plugin;

import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public interface RegisterModelsLocation {
    void onRegisterModelsLocation(Context context);

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
