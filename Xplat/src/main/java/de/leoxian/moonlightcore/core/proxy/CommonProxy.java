package de.leoxian.moonlightcore.core.proxy;

import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.level.Level;

public class CommonProxy implements Proxy {

    @Override
    public @Nullable Level getLevel() {
        return null;
    }

}
