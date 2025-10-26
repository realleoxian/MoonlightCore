package de.leoxian.moonlightcore.mixin.accessor;

import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FriendlyByteBuf.class)
public interface FriendlyByteBufAccessor {

    @Accessor
    static int getMAX_VARINT_SIZE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static int getMAX_VARLONG_SIZE() {
        throw new UnsupportedOperationException();
    }

}
