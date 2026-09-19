package de.leoxian.moonlightcore.fabric.common.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.common.command.CommandRegistrarContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public record FabricCommandRegistrarContext(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection, CommandBuildContext buildContext) implements CommandRegistrarContext {
}
