package de.leoxian.moonlightcore.world.biome.layer;

import java.util.Collection;

public abstract class WeightedLayer<E> implements AreaTransformer0 {
    private final AreaWeightedPicker<E> picker = new AreaWeightedPicker<>();

    WeightedLayer(Collection<AreaWeightedPicker.WeightedWrapper<E>> wrappers) {
        wrappers.forEach((wrapper) -> this.picker.add(wrapper.data(), wrapper.weight()));
    }

    public abstract int getEntryIndex(E data);

    @Override
    public int apply(AreaContext context, int x, int z) {
        E element = this.picker.search(context, x, z);
        return this.getEntryIndex(element);
    }
}
