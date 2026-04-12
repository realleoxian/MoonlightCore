package de.leoxian.moonlightcore.api.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;

import java.util.function.BiConsumer;

public interface ClientCommandsRegistrar {

    void registerClientCommands(BiConsumer<CommandDispatcher<ClientCommandSourceStack>, CommandBuildContext> initializer);

}
