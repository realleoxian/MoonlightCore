package de.leoxian.moonlightcore.neoforge.common.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.common.command.CommandRegistrarContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public record NeoforgeCommandRegistrarContext(RegisterCommandsEvent event) implements CommandRegistrarContext {
    @Override
    public CommandDispatcher<CommandSourceStack> dispatcher() {
        return event.getDispatcher();
    }

    @Override
    public Commands.CommandSelection selection() {
        return event.getCommandSelection();
    }

    @Override
    public CommandBuildContext buildContext() {
        return event.getBuildContext();
    }
}
