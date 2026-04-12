package de.realleoxian.moonlightcore.api.apilookup;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface ApiLookup<A, C extends @Nullable Object> {
    ResourceLocation name();

    Class<A> apiClass();

    Class<C> contextClass();
}

