package de.leoxian.moonlightcore.neoforge.common.mixin.event;

import de.leoxian.moonlightcore.common.event.ServerConfigurationConnectionEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin extends ServerCommonPacketListenerImpl {
	@Shadow
	@Final
	private Queue<ConfigurationTask> configurationTasks;
	@Shadow
	private @Nullable ConfigurationTask currentTask;

	@Shadow
	public abstract void finishCurrentTask(ConfigurationTask.Type taskTypeToFinish);

	@Unique
	private boolean moonlightcore$earlyTaskExecution;
	@Unique
	private boolean moonlightcore$sentConfiguration;

	public ServerConfigurationPacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
		super(server, connection, cookie);
	}

	@Inject(
			method = "startConfiguration",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerConfigurationPacketListenerImpl;send(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V",
					shift = At.Shift.AFTER,
					ordinal = 2
			),
			cancellable = true
	)
	private void moonlightcore$startConfiguration(CallbackInfo ci) {
		ServerConfigurationPacketListenerImpl packetListener = (ServerConfigurationPacketListenerImpl) (Object) this;
		if (!this.moonlightcore$sentConfiguration) {
			ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.doFire().onSendConfiguration(packetListener, this.server, this.configurationTasks::add, this::finishCurrentTask);

			this.moonlightcore$sentConfiguration = true;
			this.moonlightcore$earlyTaskExecution = true;
		}

		if (this.moonlightcore$earlyTaskExecution) {
			if (moonlightcore$pollEarlyTasks()) {
				ci.cancel();
				return;
			} else {
				this.moonlightcore$earlyTaskExecution = false;
			}
		}

		if (this.currentTask != null || !this.configurationTasks.isEmpty()) {
			throw new IllegalStateException("All early tasks should have been completed, current: " + this.currentTask.type().id() + ", queued: " + this.configurationTasks.size());
		}

		ServerConfigurationConnectionEvents.CONFIGURE.doFire().onSendConfiguration(packetListener, this.server, this.configurationTasks::add, this::finishCurrentTask);
	}

	@Unique
	private boolean moonlightcore$pollEarlyTasks() {
		if (!this.moonlightcore$earlyTaskExecution) {
			throw new IllegalStateException("Early task execution has finished");
		}

		if (this.currentTask != null) {
			throw new IllegalStateException("Task " + this.currentTask.type().id() + " has not finished yet");
		}

		if (!this.isAcceptingMessages()) {
			return false;
		}

		final ConfigurationTask task = this.configurationTasks.poll();
		if (task != null) {
			this.currentTask = task;
			task.start(this::send);
			return true;
		}
		return false;
	}
}
