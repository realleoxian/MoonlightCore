package de.realleoxian.moonlightcore.fabric.mixin.client;

import de.realleoxian.moonlightcore.api.client.command.ClientCommandSourceStack;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FabricClientCommandSource.class)
public interface FabricClientCommandSourceMixin extends ClientCommandSourceStack {
    @Shadow
    Entity getEntity();

    @Override
    default void sendSuccess(Component message) {
        ((FabricClientCommandSource) this).sendFeedback(message);
    }

    @Override
    default void sendFailure(Component message) {
        ((FabricClientCommandSource) this).sendFeedback(message);
    }

    @Override
    default LocalPlayer getClientPlayer() {
        return (LocalPlayer) getEntity();
    }

    @Override
    default ClientLevel getClientLevel() {
        return ((FabricClientCommandSource) this).getWorld();
    }
}
