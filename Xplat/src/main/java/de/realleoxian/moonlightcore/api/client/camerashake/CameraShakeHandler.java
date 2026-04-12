package de.realleoxian.moonlightcore.api.client.camerashake;

import de.realleoxian.moonlightcore.impl.client.camerashake.CameraShakeHandlerImpl;

public interface CameraShakeHandler {
    static void addCameraShake(CameraShakeInstance.Builder builder) {
        CameraShakeHandlerImpl.addCameraShake(builder);
    }
}
