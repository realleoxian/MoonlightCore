package de.leoxian.moonlightcore.impl.apilookup;

import de.leoxian.moonlightcore.api.apilookup.ApiLookup;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public abstract class ApiLookupImpl<A, C extends @Nullable Object> implements ApiLookup<A, C> {
    protected final ResourceLocation name;
    protected final Class<A> apiClass;
    protected final Class<C> contextClass;

    protected ApiLookupImpl(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        this.name = name;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
    }

    @Override
    public ResourceLocation name() {
        return null;
    }

    @Override
    public Class<A> apiClass() {
        return apiClass;
    }

    @Override
    public Class<C> contextClass() {
        return contextClass;
    }
}
