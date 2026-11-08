package de.leoxian.moonlightcore.mixin.client;

import de.leoxian.moonlightcore.core.client.render.state.AvatarRenderStateExtension;
import de.leoxian.moonlightcore.core.client.render.state.PlayerSkinRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateExtension{
    @Unique
    public final PlayerSkinRenderState moonlightcore$playerSkin = new PlayerSkinRenderState();

    @Override
    public PlayerSkinRenderState moonlightcore$playerSkinState() {
        return this.moonlightcore$playerSkin;
    }
}
