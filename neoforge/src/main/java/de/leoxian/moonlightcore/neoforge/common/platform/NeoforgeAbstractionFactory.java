package de.leoxian.moonlightcore.neoforge.common.platform;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.common.platform.XplatAbstractionFactory;

public class NeoforgeAbstractionFactory implements XplatAbstractionFactory {
    @Override
    public XplatAbstraction create() {
        return new NeoforgeAbstractionImpl();
    }
}
