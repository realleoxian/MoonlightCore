package de.leoxian.moonlightcore.client.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Consumer;

public interface ClientCommandsContext {
    /// Configure and register new modded client commands registrar
    /// @param initializer The initializer of the registrar
    static void configure(Consumer<ClientCommandsContext> initializer) {
        XplatClientAbstraction.INSTANCE.commands(initializer);
    }

    /// @return The command dispatcher to register commands to
    CommandDispatcher<SharedSuggestionProvider> dispatcher();

    /// @return Object exposing access to the game's holders
    CommandBuildContext buildContext();
}
