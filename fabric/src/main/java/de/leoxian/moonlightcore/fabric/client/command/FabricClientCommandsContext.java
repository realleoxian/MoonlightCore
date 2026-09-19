package de.leoxian.moonlightcore.fabric.client.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.client.command.ClientCommandsContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

public record FabricClientCommandsContext(CommandDispatcher<SharedSuggestionProvider> dispatcher, CommandBuildContext buildContext) implements ClientCommandsContext {
}
