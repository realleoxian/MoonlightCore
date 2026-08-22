package de.leoxian.moonlightcore.common.command;

import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Consumer;

public interface CommandRegistrarContext {
    static void init(Consumer<CommandRegistrarContext> initializer) {
        XplatAbstraction.INSTANCE.commands(initializer);
    }

    CommandDispatcher<CommandSourceStack> dispatcher();

    Commands.CommandSelection selection();

    CommandBuildContext buildContext();
}
