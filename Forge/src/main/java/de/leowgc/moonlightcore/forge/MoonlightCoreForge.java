package de.leowgc.moonlightcore.forge;

import de.leowgc.moonlightcore.core.MoonlightCore;
import net.minecraftforge.fml.common.Mod;

@Mod(value = MoonlightCore.MOD_ID)
public final class MoonlightCoreForge {

    public MoonlightCoreForge() {
        MoonlightCore.init();
    }

}
