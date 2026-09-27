package de.leoxian.moonlightcore.internal.common.network.task;

import de.leoxian.moonlightcore.common.network.CustomConfigurationTask;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRequestValidConfigsPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Consumer;

public record RequestValidConfigsTask() implements CustomConfigurationTask {
	public static final Type TYPE = new Type("moonlightcore:request_valid_configs");

	@Override
	public void run(Consumer<CustomPacketPayload> output) {
		output.accept(S2CRequestValidConfigsPacket.INSTANCE);
	}

	@Override
	public Type type() {
		return TYPE;
	}
}
