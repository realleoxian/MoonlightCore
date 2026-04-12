package de.leoxian.moonlightcore.api.client.event;

import de.leoxian.moonlightcore.api.event.EventBus;
import de.leoxian.moonlightcore.api.event.EventResult;
import net.minecraft.client.gui.GuiGraphics;

public final class GuiRenderEvent {
    public static final EventBus<Start> GUI_RENDER_START = EventBus.create((listeners) -> (graphics, partialTick) -> {
       for (GuiRenderEvent.Start listener : listeners) {
           EventResult result = listener.onGuiRenderStart(graphics, partialTick);

           if (result.cancelFurtherProcessing) {
               return result;
           }
       }

        return EventResult.TRUE;
    });
    public static final EventBus<End> GUI_RENDER_END = EventBus.create((listeners) -> (graphics, partialTick) -> {
        for (GuiRenderEvent.End listener : listeners) {
            listener.onGuiRenderEnd(graphics, partialTick);
        }
    });

    private GuiRenderEvent() {}

    public interface Start {
        EventResult onGuiRenderStart(GuiGraphics graphics, float partialTick);
    }

    public interface End {
        void onGuiRenderEnd(GuiGraphics graphics, float partialTick);
    }
}
