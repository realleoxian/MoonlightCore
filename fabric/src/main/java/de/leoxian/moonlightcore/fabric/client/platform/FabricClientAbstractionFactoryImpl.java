package de.leoxian.moonlightcore.fabric.client.platform;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstractionFactory;

public class FabricClientAbstractionFactoryImpl implements XplatClientAbstractionFactory {
    @Override
    public XplatClientAbstraction create() {
        return new FabricClientAbstractionImpl();
    }
}
