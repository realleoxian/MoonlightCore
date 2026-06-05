package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

public class PlayerTickEvents extends EventBase {
    public static final Event<PlayerTickEvents> START = Event.create(PlayerTickEvents.class);
    public static final Event<PlayerTickEvents> END = Event.create(PlayerTickEvents.class);

    public final Player player;

    @ApiStatus.Internal
    public PlayerTickEvents(Player player) {
        this.player = player;
    }
}
