package de.leoxian.moonlightcore.core.proxy;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface Proxy {
    Proxy PROXY = EnvironmentSide.unsafeRunIf(() -> ClientProxy::new, () -> CommonProxy::new);

    @Nullable Level getLevel();

}
