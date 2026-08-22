package de.leoxian.moonlightcore.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

public interface ClientCommandsContext {
    CommandDispatcher<SharedSuggestionProvider> dispatcher();

    CommandBuildContext buildContext();
}
