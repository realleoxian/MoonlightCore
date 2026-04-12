package de.leoxian.moonlightcore.api.client.camerashake;

import de.leoxian.moonlightcore.impl.client.camerashake.CameraShakeHandlerImpl;

public interface CameraShakeHandler {
    static void addCameraShake(CameraShakeInstance.Builder builder) {
        CameraShakeHandlerImpl.addCameraShake(builder);
    }
}
