package de.leoxian.moonlightcore.neoforge.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import de.leoxian.moonlightcore.common.command.ArgumentTypeRegistrar;
import de.leoxian.moonlightcore.neoforge.common.ModDeferredRegisters;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class NeoforgeArgumentTypeRegistrar implements ArgumentTypeRegistrar {
    private final String modId;

    public NeoforgeArgumentTypeRegistrar(String modId) {
        this.modId = modId;
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(Identifier id, Class<A> argumentType, ArgumentTypeInfo<A, T> info) {
        ModDeferredRegisters.get(Registries.COMMAND_ARGUMENT_TYPE, id.getNamespace()).register(id.getPath(), () -> info);
        ArgumentTypeInfos.registerByClass(argumentType, info);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(String id, Class<A> argumentType, ArgumentTypeInfo<A, T> info) {
        register(Identifier.fromNamespaceAndPath(this.modId, id), argumentType, info);
    }
}
