package de.leoxian.moonlightcore.levelgen.noise;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.List;
import java.util.Optional;

public class WeightedRandomList<T extends WeightedEntry> {

    public static <T extends WeightedEntry> WeightedRandomList<T> create(List<T> entries) {
        return new WeightedRandomList<>(entries);
    }

    public static <T extends WeightedEntry> WeightedRandomList<T> create(T... entries) {
        return create(ImmutableList.copyOf(entries));
    }

    public static <T extends WeightedEntry> WeightedRandomList<T> create() {
        return create();
    }

    private final ImmutableList<T> entries;
    private final int totalWeight;

    WeightedRandomList(List<? extends T> entries) {
        this.entries = ImmutableList.copyOf(entries);
        this.totalWeight = WeightedRandom.getTotalWeight(entries);
    }

    public Optional<T> get(AreaContext context) {
        if(this.totalWeight == 0) {
            return Optional.empty();
        }

        int i = context.nextRandom(this.totalWeight);
        return WeightedRandom.getWeightedItem(this.entries, i);
    }
}
