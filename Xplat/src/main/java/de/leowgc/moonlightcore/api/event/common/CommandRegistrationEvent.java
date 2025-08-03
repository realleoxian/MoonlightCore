package de.leowgc.moonlightcore.api.event.common;

import com.mojang.brigadier.CommandDispatcher;
import de.leowgc.moonlightcore.api.event.Event;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@FunctionalInterface
public interface CommandRegistrationEvent {
    Event<CommandRegistrationEvent> COMMAND_REGISTRATION = Event.create();

    void bootstrap(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection, CommandBuildContext context);
}
