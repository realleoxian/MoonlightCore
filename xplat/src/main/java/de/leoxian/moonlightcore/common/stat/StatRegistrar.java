package de.leoxian.moonlightcore.common.stat;

import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;

public interface StatRegistrar {
    Identifier register(Identifier id, StatFormatter formatter);

    default Identifier register(Identifier id) {
        return register(id, StatFormatter.DEFAULT);
    }
}
