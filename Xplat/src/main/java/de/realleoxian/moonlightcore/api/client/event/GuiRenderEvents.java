package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import de.realleoxian.moonlightcore.api.event.EventResult;
import net.minecraft.client.gui.GuiGraphics;

public final class GuiRenderEvents {
    public static final EventBus<Start> GUI_RENDER_START = EventBus.create(Start.class, (listeners) -> (graphics, partialTick) -> {
       for (GuiRenderEvents.Start listener : listeners) {
           EventResult result = listener.onGuiRenderStart(graphics, partialTick);

           if (result.cancelFurtherProcessing) {
               return result;
           }
       }

        return EventResult.TRUE;
    });
    public static final EventBus<End> GUI_RENDER_END = EventBus.create(End.class, (listeners) -> (graphics, partialTick) -> {
        for (GuiRenderEvents.End listener : listeners) {
            listener.onGuiRenderEnd(graphics, partialTick);
        }
    });

    private GuiRenderEvents() {}

    public interface Start {
        EventResult onGuiRenderStart(GuiGraphics graphics, float partialTick);
    }

    public interface End {
        void onGuiRenderEnd(GuiGraphics graphics, float partialTick);
    }
}
