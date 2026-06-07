package de.realleoxian.moonlightcore.api.internal;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface XplatAbstractionFactory {
    XplatAbstraction<?> make();
}
