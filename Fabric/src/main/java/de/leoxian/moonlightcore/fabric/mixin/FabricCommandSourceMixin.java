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
public abstract class FabricCommandSourceMixin implements RegisterClientCommandEvent.ClientCommandSourceStack {
    @Override
    public void mlcore$sendSuccess(Supplier<Component> message, boolean broadcastToAdmins) {
        ((FabricClientCommandSource) this).sendFeedback(message.get());
    }

    @Override
    public void mlcore$sendFailure(Component message) {
        ((FabricClientCommandSource) this).sendError(message);
    }

    @Override
    public ClientLevel mlcore$getLevel() {
        return ((FabricClientCommandSource) this).getWorld();
    }

    @Override
    public LocalPlayer mlcore$getPlayer() {
        return ((FabricClientCommandSource) this).getPlayer();
    }

    @Override
    public Vec3 mlcore$getPosition() {
        return ((FabricClientCommandSource) this).getPosition();
    }

    @Override
    public Vec2 mlcore$getRotation() {
        return ((FabricClientCommandSource) this).getRotation();
    }
}
