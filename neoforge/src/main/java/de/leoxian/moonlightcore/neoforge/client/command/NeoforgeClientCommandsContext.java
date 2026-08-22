package de.leoxian.moonlightcore.neoforge.client.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.client.command.ClientCommandsContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

public record NeoforgeClientCommandsContext(RegisterClientCommandsEvent event) implements ClientCommandsContext {
    @Override
    public CommandDispatcher<SharedSuggestionProvider> dispatcher() {
        return (CommandDispatcher<SharedSuggestionProvider>) (CommandDispatcher) event.getDispatcher();
    }

    @Override
    public CommandBuildContext buildContext() {
        return event.getBuildContext();
    }
}
