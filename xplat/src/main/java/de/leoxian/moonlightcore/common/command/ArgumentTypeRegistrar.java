package de.leoxian.moonlightcore.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public interface ArgumentTypeRegistrar {
    static void init(String namespace, Consumer<ArgumentTypeRegistrar> initializer) {
        XplatAbstraction.INSTANCE.argumentTypes(namespace, initializer);
    }

    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(Identifier id, Class<A> argumentType, ArgumentTypeInfo<A, T> info);

    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(String id, Class<A> argumentType, ArgumentTypeInfo<A, T> info);
}
