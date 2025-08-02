package de.leoxian.moonlightcore.forge.handler;

import de.leoxian.moonlightcore.api.event.EventDispatcher;
import de.leoxian.moonlightcore.api.event.server.CommandRegistrationEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegisterCommandsEventHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        EventDispatcher.INSTANCE.fire(CommandRegistrationEvent.COMMAND_REGISTRATION, (listener) -> listener.bootstrap(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext()));
    }

}
