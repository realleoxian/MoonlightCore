package de.realleoxian.moonlightcore.core.client;

import com.mojang.brigadier.tree.CommandNode;
import de.realleoxian.moonlightcore.api.client.MoonlightCoreClient;
import de.realleoxian.moonlightcore.api.client.command.ClientCommandSourceStack;

public class CoreClientMod {

    public static void initializeClient() {
        MoonlightCoreClient.commands("moonlightcore", (dispatcher, buildCtx) -> {
            CommandNode<ClientCommandSourceStack> apiNode = dispatcher.register(ClientCommandSourceStack.literal("moonlightcore-client-commands"));

            dispatcher.register(ClientCommandSourceStack.literal("mcc").redirect(apiNode));
        });
    }
    
}
