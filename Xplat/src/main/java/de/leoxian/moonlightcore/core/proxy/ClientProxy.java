package de.leoxian.moonlightcore.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientProxy implements Proxy {

    @Override
    public @Nullable Level getLevel() {
        return Minecraft.getInstance().level;
    }

}
