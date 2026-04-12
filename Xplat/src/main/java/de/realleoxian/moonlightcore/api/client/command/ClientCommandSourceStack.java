package de.realleoxian.moonlightcore.api.client.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface ClientCommandSourceStack extends SharedSuggestionProvider {

    static LiteralArgumentBuilder<ClientCommandSourceStack> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    static <T> RequiredArgumentBuilder<ClientCommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    void sendSuccess(Component message);

    void sendFailure(Component message);

    ClientLevel getClientLevel();

    LocalPlayer getClientPlayer();

    Vec3 getPosition();

    Vec2 getRotation();

}
