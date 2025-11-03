package de.leoxian.moonlightcore.levelgen.noise;

import net.minecraft.util.random.WeightedEntry;

import java.util.List;

public abstract class WeightedRandomLayer<T extends WeightedEntry> implements AreaTransformer0 {
    private final WeightedRandomList<T> entries;

    WeightedRandomLayer(List<T> entries) {
        this.entries = WeightedRandomList.create(entries);
    }

    protected abstract int getEntryIndex(T entry);

    @Override
    public int apply(AreaContext context, int x, int z) {
        return this.entries.get(context).map(this::getEntryIndex).orElse(getDefaultIndex());
    }

    protected int getDefaultIndex() {
        return 0;
    }
}
