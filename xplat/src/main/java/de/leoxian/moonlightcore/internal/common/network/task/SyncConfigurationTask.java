package de.leoxian.moonlightcore.internal.common.network.task;

import de.leoxian.moonlightcore.common.network.CustomConfigurationTask;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.common.network.c2s.C2SFinishConfigurationTask;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CNotifyConfigSyncFinishPacket;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CSyncLoadedConfigPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Set;
import java.util.function.Consumer;

public record SyncConfigurationTask(Set<Identifier> syncables) implements CustomConfigurationTask {
	public static final Type TYPE = new Type("moonlightcore:sync_config");

	@Override
	public void run(Consumer<CustomPacketPayload> output) {
		for (final var syncable : syncables) {
			var config = ConfigRegistry.getConfig(syncable);
			if (config == null) {
				continue;
			}
			output.accept(new S2CSyncLoadedConfigPacket(syncable, config.loadedConfig()));
		}

		output.accept(S2CNotifyConfigSyncFinishPacket.INSTANCE);
	}

	@Override
	public Type type() {
		return TYPE;
	}
}
