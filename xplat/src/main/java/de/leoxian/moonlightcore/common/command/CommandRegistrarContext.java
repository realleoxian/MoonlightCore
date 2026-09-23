package de.leoxian.moonlightcore.common.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Consumer;

public interface CommandRegistrarContext {
	/// Configure and register new modded commands
	/// @param initializer The initializer of the registrar
	static void configure(Consumer<CommandRegistrarContext> initializer) {
		XplatAbstraction.INSTANCE.commands(initializer);
	}

	/// @return The command dispatcher to register commands to
	CommandDispatcher<CommandSourceStack> dispatcher();

	/// @return Environment selection the registrations should be done for, used for commands that are dedicated or integrated server only
	Commands.CommandSelection selection();

	/// @return Object exposing access to the game's holders
	CommandBuildContext buildContext();
}
