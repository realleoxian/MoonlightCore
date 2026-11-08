package de.leoxian.moonlightcore.core.client.render.state;

import de.leoxian.moonlightcore.common.attachment.DataAttachmentHolder;
import de.leoxian.moonlightcore.common.playerskin.MlPlayerSkin;
import de.leoxian.moonlightcore.core.data.MoonlightDataAttachments;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

public class PlayerSkinRenderState {
    public static PlayerSkinRenderState from(AvatarRenderState renderState) {
        return ((AvatarRenderStateExtension) renderState).moonlightcore$playerSkinState();
    }

    public MlPlayerSkin mlPlayerSkin = MlPlayerSkin.EMPTY;

    public <AvatarlikeEntity extends Avatar & ClientAvatarEntity> void extract(AvatarlikeEntity player) {
        this.mlPlayerSkin = DataAttachmentHolder.of(player).get(MoonlightDataAttachments.PLAYER_SKIN);
    }
}
