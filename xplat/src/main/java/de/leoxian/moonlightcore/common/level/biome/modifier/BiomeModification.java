package de.leoxian.moonlightcore.common.level.biome.modifier;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.resources.Identifier;

public interface BiomeModification {
    static BiomeModification get() {
        return XplatAbstraction.INSTANCE.biomeModification();
    }

    void modify(Identifier id, BiomeModificationPhase phase, BiomeSelector selector, BiomeModifier modifier);
}
