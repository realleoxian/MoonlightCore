package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.api.ext.MoonlightCoreServerConfigurationPacketListenerExtension;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Queue;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin implements MoonlightCoreServerConfigurationPacketListenerExtension {
    @Shadow
    @Final
    private Queue<ConfigurationTask> configurationTasks;

    @Shadow
    protected abstract void finishCurrentTask(ConfigurationTask.Type taskType);

    @Override
    public void moonlightcore$addTask(ConfigurationTask task) {
        this.configurationTasks.add(task);
    }

    @Override
    public void moonlightcore$completeTask(ConfigurationTask.Type type) {
        finishCurrentTask(type);
    }
}
