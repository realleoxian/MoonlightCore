package de.leoxian.moonlightcore.fabric.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import de.leoxian.moonlightcore.common.command.ArgumentTypeRegistrar;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.Identifier;

public enum FabricArgumentTypeRegistrar implements ArgumentTypeRegistrar {
    INSTANCE
    ;

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(Identifier id, Class<A> argumentType, ArgumentTypeInfo<A, T> info) {
        ArgumentTypeRegistry.registerArgumentType(id, argumentType, info);
    }
}
