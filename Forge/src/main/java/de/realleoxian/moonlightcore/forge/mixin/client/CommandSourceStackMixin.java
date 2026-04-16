package de.realleoxian.moonlightcore.forge.mixin.client;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.realleoxian.moonlightcore.api.client.command.ClientCommandSourceStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements ClientCommandSourceStack {

    @Override
    public void sendSuccess(Component message) {
        ((CommandSourceStack) (Object) this).sendSuccess(() -> message, false);
    }

    @Override
    public void sendFailure(Component message) {
        ((CommandSourceStack) (Object) this).sendFailure(message);
    }

    @Override
    public ClientLevel getClientLevel() {
        try {
            return (ClientLevel) ((CommandSourceStack) (Object) this).getUnsidedLevel();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LocalPlayer getClientPlayer() {
        try {
            return (LocalPlayer) ((CommandSourceStack) (Object) this).getEntityOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
