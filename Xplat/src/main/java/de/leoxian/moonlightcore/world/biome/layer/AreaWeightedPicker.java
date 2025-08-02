package de.leoxian.moonlightcore.world.biome.layer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public final class AreaWeightedPicker<T> {
    public static <T> AreaWeightedPicker<T> fromEntries (List<Pair<T, Integer>> entries) {
        AreaWeightedPicker<T> picker = new AreaWeightedPicker<>();

        for (var entry : entries) {
            picker.add(entry.getFirst(), entry.getSecond());
        }

        return picker;
    }

    private final List<Entry<T>> entries = new ArrayList<>();
    private int totalWeight = 0;

    public AreaWeightedPicker() {}

    public void add(T data, int weight) {
        Preconditions.checkArgument(weight > 0 && weight <= 1000, "Weight must be on a range from 0 to 1000: [0,1000]");
        totalWeight += weight;
        entries.add(new Entry<>(data, weight, totalWeight));
    }

    public T search(AreaContext context, int x, int z) {
        Preconditions.checkArgument(totalWeight > 0, "Picker is empty");
        Preconditions.checkNotNull(context, "Context cannot be null");

        context.initializeRandom(x, z);
        int target = context.nextRandom(totalWeight);

        int low = 0;
        int high = entries.size() - 1;

        while (low < high) {
            int mid = (low + high) >>> 1;

            if (target >= entries.get(mid).upperWeight()) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return entries.get(low).data();
    }

    public List<Entry<T>> entries() {
        return ImmutableList.copyOf(this.entries);
    }

    public record Entry<T>(T data, int weight, int upperWeight) {
        public Entry {
            Preconditions.checkNotNull(data);
            Preconditions.checkArgument(weight > 0, "Weight must be positive");
        }
    }

    public record WeightedWrapper<T>(T data, int weight) {}
}
