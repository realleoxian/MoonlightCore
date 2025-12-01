package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.client.RegisterClientCommandEvent;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(FabricClientCommandSource.class)
public interface FabricCommandSourceMixin extends RegisterClientCommandEvent.ClientCommandSourceStack {
    @Override
    default void mlcore$sendSuccess(Supplier<Component> message, boolean broadcastToAdmins) {
        ((FabricClientCommandSource) this).sendFeedback(message.get());
    }

    @Override
    default void mlcore$sendFailure(Component message) {
        ((FabricClientCommandSource) this).sendError(message);
    }

    @Override
    default ClientLevel mlcore$getLevel() {
        return ((FabricClientCommandSource) this).getWorld();
    }

    @Override
    default LocalPlayer mlcore$getPlayer() {
        return ((FabricClientCommandSource) this).getPlayer();
    }

    @Override
    default Vec3 mlcore$getPosition() {
        return ((FabricClientCommandSource) this).getPosition();
    }

    @Override
    default Vec2 mlcore$getRotation() {
        return ((FabricClientCommandSource) this).getRotation();
    }
}
