package de.leoxian.moonlightcore.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.Identifier;

public interface ArgumentTypeRegistrar {
    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(Identifier id, Class<A> argumentType, ArgumentTypeInfo<A, T> info);

    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(String id, Class<A> argumentType, ArgumentTypeInfo<A, T> info);
}
