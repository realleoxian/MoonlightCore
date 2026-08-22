package de.leoxian.moonlightcore.client.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Consumer;

public interface ClientCommandsContext {
    static void init(Consumer<ClientCommandsContext> initializer) {
        XplatClientAbstraction.INSTANCE.commands(initializer);
    }

    CommandDispatcher<SharedSuggestionProvider> dispatcher();

    CommandBuildContext buildContext();
}
