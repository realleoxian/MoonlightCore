package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@FunctionalInterface
public interface OnDatapackSyncEvent {
    Event<OnDatapackSyncEvent> EVENT = Event.create(OnDatapackSyncEvent.class, listeners -> (playerList, player, relevantPlayer) -> {
       for (final var listener : listeners) {
           listener.onDatapackSync(playerList, player, relevantPlayer);
       }
    });

    void onDatapackSync(PlayerList playerList, @Nullable ServerPlayer player, Stream<ServerPlayer> relevantPlayer);
}
