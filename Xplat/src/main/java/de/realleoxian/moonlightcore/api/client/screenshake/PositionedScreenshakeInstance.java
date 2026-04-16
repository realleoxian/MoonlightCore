package de.realleoxian.moonlightcore.api.client.screenshake;

import de.realleoxian.moonlightcore.api.client.misc.Easing;
import net.minecraft.world.phys.Vec3;

public interface PositionedScreenshakeInstance extends ScreenshakeInstance {
    Vec3 position();

    double range();

    interface Builder extends ScreenshakeInstance.Builder {
        Builder fallOffEasing(Easing easing);
    }
}
