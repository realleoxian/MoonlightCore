package de.leoxian.moonlightcore.event.common;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface RegisterCommandsEvent {
     /**
      * @see #onCommandRegistration(CommandDispatcher, Commands.CommandSelection, CommandBuildContext)
      */
     Event<RegisterCommandsEvent> EVENT = EventFactory.create(RegisterCommandsEvent.class);

     /**
      * Invoked after the server registers it's commands
      * @param dispatcher The command dispatcher to register commands to
      * @param selection The selection where the command can be executed
      * @param context The command registry for building arguments
      */
     void onCommandRegistration(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection, CommandBuildContext context);
}
