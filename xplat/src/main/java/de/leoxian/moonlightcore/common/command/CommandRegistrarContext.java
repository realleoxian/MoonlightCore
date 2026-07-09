package de.leoxian.moonlightcore.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface CommandRegistrarContext {
    CommandDispatcher<CommandSourceStack> dispatcher();

    Commands.CommandSelection selection();

    CommandBuildContext buildContext();
}
