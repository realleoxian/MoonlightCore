package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.CancellableEvent;
import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

public sealed class GuiRenderEvents extends EventBase {
    public static final Event<Pre> PRE = Event.create(GuiRenderEvents.Pre.class);
    public static final Event<Post> POST = Event.create(GuiRenderEvents.Post.class);

    public final GuiGraphics graphics;
    public final DeltaTracker tracker;

    protected GuiRenderEvents(GuiGraphics graphics, DeltaTracker tracker) {
        this.graphics = graphics;
        this.tracker = tracker;
    }

    public static final class Pre extends GuiRenderEvents implements CancellableEvent {
        @ApiStatus.Internal
        public Pre(GuiGraphics graphics, DeltaTracker tracker) {
            super(graphics, tracker);
        }
    }

    public static final class Post extends GuiRenderEvents {
        @ApiStatus.Internal
        public Post(GuiGraphics graphics, DeltaTracker tracker) {
            super(graphics, tracker);
        }
    }
}
