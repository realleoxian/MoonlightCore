package de.leoxian.moonlightcore.core.proxy;

import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientProxy implements Proxy {

    @Override
    @Nullable
    public Level getLevel() {
        return Minecraft.getInstance().level;
    }

}
