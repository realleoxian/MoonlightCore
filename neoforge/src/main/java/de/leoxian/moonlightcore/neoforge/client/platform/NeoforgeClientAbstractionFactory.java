package de.leoxian.moonlightcore.neoforge.client.platform;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstractionFactory;

public class NeoforgeClientAbstractionFactory implements XplatClientAbstractionFactory {
    @Override
    public XplatClientAbstraction create() {
        return new NeoforgeClientAbstraction();
    }
}
