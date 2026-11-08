package de.leoxian.moonlightcore.common.playerskin;

import de.leoxian.moonlightcore.common.attachment.DataAttachmentHolder;
import de.leoxian.moonlightcore.core.data.MoonlightDataAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PlayerSkinHandler {
    private static final Map<Identifier, MlPlayerSkin> SKINS = new HashMap<>();

    public static void register(MlPlayerSkin skin) {
        SKINS.put(skin.id(), skin);
    }

    public static void tick(Player player) {
        MlPlayerSkin skin = getSkin(player);
        if (!isEmptySkin(skin)) {
            skin.tick(player);
        }
    }

    public static void setSkin(Player player, MlPlayerSkin skin) {
        DataAttachmentHolder holder = DataAttachmentHolder.of(player);
        holder.set(MoonlightDataAttachments.PLAYER_SKIN, skin);
        holder.syncData(MoonlightDataAttachments.PLAYER_SKIN);
    }

    public static @Nullable MlPlayerSkin getSkin(Player player) {
        DataAttachmentHolder holder = DataAttachmentHolder.of(player);
        return holder.get(MoonlightDataAttachments.PLAYER_SKIN);
    }

    public static @Nullable MlPlayerSkin getSkin(Identifier id) {
        return SKINS.get(id);
    }

    public static MlPlayerSkin getSkinOrThrow(Identifier id) {
        return Objects.requireNonNull(getSkin(id), "Skin '" + id + "' doesn't seem to exists");
    }

    public static boolean isEmptySkin(@Nullable MlPlayerSkin skin) {
        return skin == null || skin == MlPlayerSkin.EMPTY;
    }

    private PlayerSkinHandler() {
    }
}
