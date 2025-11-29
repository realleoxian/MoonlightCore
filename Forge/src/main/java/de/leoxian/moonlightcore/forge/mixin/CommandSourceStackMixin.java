package de.leoxian.moonlightcore.forge.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.leoxian.moonlightcore.event.client.RegisterClientCommandEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements RegisterClientCommandEvent.ClientCommandSourceStack {
    @Override
    public void mlcore$sendSuccess(Supplier<Component> message, boolean broadcastToAdmins) {
        ((CommandSourceStack) (Object) this).sendSuccess(message, broadcastToAdmins);
    }

    @Override
    public void mlcore$sendFailure(Component message) {
        ((CommandSourceStack) (Object) this).sendFailure(message);
    }

    @Override
    public LocalPlayer mlcore$getPlayer() {
        try {
            return (LocalPlayer) ((CommandSourceStack) (Object) this).getEntityOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vec3 mlcore$getPosition() {
        return ((CommandSourceStack) (Object) this).getPosition();
    }

    @Override
    public Vec2 mlcore$getRotation() {
        return ((CommandSourceStack) (Object) this).getRotation();
    }

    @Override
    public ClientLevel mlcore$getLevel() {
        return (ClientLevel) ((CommandSourceStack) (Object) this).getUnsidedLevel();
    }
}
