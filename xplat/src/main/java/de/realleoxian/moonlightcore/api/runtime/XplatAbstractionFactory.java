package de.realleoxian.moonlightcore.api.runtime;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface XplatAbstractionFactory {
    XplatAbstraction<?> make();
}
