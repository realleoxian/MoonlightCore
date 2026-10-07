package de.leoxian.moonlightcore.neoforge.common;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import net.neoforged.fml.common.Mod;

@Mod(value = "moonlightcore")
public class MoonlightCoreNeoforgeMod {
	public MoonlightCoreNeoforgeMod() {
		XplatAbstraction.INSTANCE.initialize();
		MoonlightCore.init();
	}
}
