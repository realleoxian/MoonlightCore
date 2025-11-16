package de.leoxian.moonlightcore.mixin.accessor;

import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundCustomPayloadPacket.class)
public interface ServerboundCustomPayloadPacketAccessor {

    @Accessor
    static int getMAX_PAYLOAD_SIZE() {
        throw new AssertionError();
    }

}
