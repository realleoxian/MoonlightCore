package de.leoxian.moonlightcore.core.data;

import de.leoxian.moonlightcore.common.attachment.DataAttachmentSyncPredicate;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import de.leoxian.moonlightcore.common.playerskin.MlPlayerSkin;
import de.leoxian.moonlightcore.common.playerskin.PlayerSkinHandler;
import de.leoxian.moonlightcore.core.MoonlightCore;
import net.minecraft.resources.Identifier;

public final class MoonlightDataAttachments {
    public static final DataAttachmentType<MlPlayerSkin> PLAYER_SKIN = DataAttachmentType.builder(() -> MlPlayerSkin.EMPTY)
            .persistent(Identifier.CODEC.xmap(PlayerSkinHandler::getSkinOrThrow, MlPlayerSkin::id))
            .synced(Identifier.STREAM_CODEC.map(PlayerSkinHandler::getSkinOrThrow, MlPlayerSkin::id), DataAttachmentSyncPredicate.all())
            .copyOnDeath(true)
            .build(MoonlightCore.id("player_skin"));

    public static void init() {
        // no-op
    }

    private MoonlightDataAttachments() {
    }
}
