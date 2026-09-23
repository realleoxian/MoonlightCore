package de.leoxian.moonlightcore.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public interface ArgumentTypeRegistrar {
	/// Configure and register custom command argument types.
	/// @param initializer The initializer of the registrar
	static void configure(Consumer<ArgumentTypeRegistrar> initializer) {
		XplatAbstraction.INSTANCE.argumentTypes(initializer);
	}

	/// Registers a new [ArgumentType] that can be used on commands
	/// @param id The identifier of the argument type
	/// @param argumentType The argument type class
	/// @param info The argument type serializer
	/// @param <A> The argument type
	/// @param <T> The argument type properties
	<A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(Identifier id, Class<A> argumentType, ArgumentTypeInfo<A, T> info);
}
