package de.leoxian.moonlightcore.event.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public interface RegisterClientCommandEvent {
    Event<RegisterClientCommandEvent> EVENT = EventFactory.create(RegisterClientCommandEvent.class);

    /**
     * Invoked to allow mods to register client commands
     * @param dispatcher Returns the command dispatcher for registering commands to be executed on the client
     * @param context The context to build commands for
     */
    void onClientCommandRegistration(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandBuildContext context);

    interface ClientCommandSourceStack extends SharedSuggestionProvider {
        static LiteralArgumentBuilder<ClientCommandSourceStack> literal(String name) {
            return LiteralArgumentBuilder.literal(name);
        }

        static <T>RequiredArgumentBuilder<ClientCommandSourceStack, T> argument(String name, ArgumentType<T> type) {
            return RequiredArgumentBuilder.argument(name, type);
        }

        void mlcore$sendSuccess(Supplier<Component> message, boolean broadcastToAdmins);

        void mlcore$sendFailure(Component message);

        LocalPlayer mlcore$getPlayer();

        Vec3 mlcore$getPosition();

        Vec2 mlcore$getRotation();

        ClientLevel mlcore$getLevel();
    }
}
