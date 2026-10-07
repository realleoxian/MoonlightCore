package de.leoxian.moonlightcore.neoforge.client;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.internal.core.client.MoonlightCoreClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = "moonlightcore", dist = Dist.CLIENT)
public class MoonlightCoreNeoforgeClientMod {
	public MoonlightCoreNeoforgeClientMod() {
		XplatClientAbstraction.INSTANCE.get().initialize();
		MoonlightCoreClient.initializeClient();
	}
}
