package de.leoxian.moonlightcore.internal.common;

import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

// DON'T CALL ANYTHING IN HERE
@ApiStatus.Internal
@ApiStatus.Experimental
public interface XplatInternalsAbstraction {
    void unfreezeRegistry(Registry<?> registry);
}
