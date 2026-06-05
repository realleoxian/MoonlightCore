package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

public final class VanillaGameEvent extends EventBase implements CancellableEvent {
    public static final Event<VanillaGameEvent> EVENT = Event.create(VanillaGameEvent.class);

    public final ServerLevel level;
    public final GameEvent event;
    public final GameEvent.Context context;
    public final Vec3 position;

    @ApiStatus.Internal
    public VanillaGameEvent(ServerLevel level, GameEvent event, GameEvent.Context context, Vec3 position) {
        this.level = level;
        this.event = event;
        this.context = context;
        this.position = position;
    }
}
