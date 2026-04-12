package de.realleoxian.moonlightcore.api.apilookup;

import de.realleoxian.moonlightcore.impl.apilookup.ApiLookupRegistryImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ApiLookupRegistry<A> {
    static <A> ApiLookupRegistry<A> create(ApiLookupRegistry.LookupFactory<A> lookupFactory) {
        return ApiLookupRegistryImpl.create(lookupFactory);
    }

    A create(ResourceLocation name, Class<?> apiClass, Class<?> contextClass);

    @UnmodifiableView
    List<A> getAPIs();

    @FunctionalInterface
    interface LookupFactory<A> {
        A create(ResourceLocation name, Class<?> apiClass, Class<?> contextClass);
    }
}

