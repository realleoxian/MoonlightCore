package de.realleoxian.moonlightcore.api.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ClientCommandsRegistrar {
    void registerClientCommands(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandBuildContext buildContext);
}
