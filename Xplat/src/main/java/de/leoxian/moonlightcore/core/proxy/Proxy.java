package de.leoxian.moonlightcore.core.proxy;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.level.Level;

public interface Proxy {
    Proxy PROXY = EnvironmentSide.unsafeRunIf(() -> ClientProxy::new, () -> CommonProxy::new);

    @Nullable
    Level getLevel();

}
