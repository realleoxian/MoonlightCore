package de.leoxian.moonlightcore.neoforge.common.mixin.event;

import de.leoxian.moonlightcore.common.event.ServerConfigurationConnectionEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    protected abstract void finishCurrentTask(ConfigurationTask.Type taskTypeToFinish);

    public ServerConfigurationPacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Inject(
            method = "addOptionalTasks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Queue;addAll(Ljava/util/Collection;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$beforeConfigure(CallbackInfo ci) {
        ServerConfigurationPacketListenerImpl self = (ServerConfigurationPacketListenerImpl) (Object) this;
        ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.doFire().onSendConfiguration(self, this.server, this.configurationTasks::add, this::finishCurrentTask);
    }

    @Inject(
            method = "addOptionalTasks",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$atConfigure(CallbackInfo ci) {
        ServerConfigurationPacketListenerImpl self = (ServerConfigurationPacketListenerImpl) (Object) this;
        ServerConfigurationConnectionEvents.CONFIGURE.doFire().onSendConfiguration(self, this.server, this.configurationTasks::add, this::finishCurrentTask);
    }
}
