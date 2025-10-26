package de.leoxian.moonlightcore.forge;

import de.leoxian.moonlightcore.core.MoonlightCore;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;

@Mod(MoonlightCore.MOD_ID)
public class MoonlightCoreForge {

     public MoonlightCoreForge() {
          MoonlightCore.initialize();
     }

}
