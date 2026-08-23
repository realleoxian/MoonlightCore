package de.leoxian.moonlightcore.fabric.common.platform;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.common.platform.XplatAbstractionFactory;

public class FabricAbstractionFactoryImpl implements XplatAbstractionFactory {
    @Override
    public XplatAbstraction create() {
        return new FabricAbstractionImpl();
    }
}
