package de.realleoxian.moonlightcore.api.command;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;

public interface ArgumentTypeRegistrar {
    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(ResourceLocation name, Class<A> argumentType, ArgumentTypeInfo<A, T> argumentTypeInfo);
}
