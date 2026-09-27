package de.leoxian.moonlightcore.internal.common.core;

import de.leoxian.moonlightcore.common.event.ServerConfigurationConnectionEvents;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.internal.common.network.c2s.C2SAcceptedValidConfigs;
import de.leoxian.moonlightcore.internal.common.network.c2s.C2SFinishConfigurationTask;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRequestValidConfigsPacket;
import de.leoxian.moonlightcore.internal.common.network.task.RequestValidConfigsTask;

public class MoonlightCore {
	public static void initialize() {
		ServerConfigurationNetworking.register(C2SFinishConfigurationTask.TYPE, C2SFinishConfigurationTask.STREAM_CODEC, C2SFinishConfigurationTask::handleConfiguration);
		ServerConfigurationNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handleConfiguration);

		ServerPlayNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handlePlay);

		ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.subscribe((listener, server, tasksSender, taskFinisher) -> {
			if (ServerConfigurationNetworking.canSend(listener, S2CRequestValidConfigsPacket.TYPE)) {
				tasksSender.accept(new RequestValidConfigsTask());
			}
		});
	}
}
