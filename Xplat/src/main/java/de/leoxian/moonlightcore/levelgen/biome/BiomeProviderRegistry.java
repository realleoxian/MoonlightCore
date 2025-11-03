package de.leoxian.moonlightcore.levelgen.biome;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BiomeProviderRegistry {
    private static final Map<Dimension, BiomeProviderRegistry> REGISTRIES = new HashMap<>();
    private static final int VANILLA_PROVIDER = 0;

    static {
        get(Dimension.OVERWORLD).register(new ResourceLocation("overworld"), VanillaBiomeProviders.OVERWORLD, 1000);
        get(Dimension.NETHER).register(new ResourceLocation("nether"), VanillaBiomeProviders.NETHER, 1000);
    }

    public static BiomeProviderRegistry get(Dimension dimension) {
        return REGISTRIES.computeIfAbsent(dimension, $ -> new BiomeProviderRegistry());
    }

    private final Map<ResourceLocation, BiomeProvider> byName = new HashMap<>();
    private final Map<BiomeProvider, ResourceLocation> toName = new IdentityHashMap<>();
    private final ObjectList<BiomeProvider> byId = new ObjectArrayList<>();
    private final Object2IntMap<BiomeProvider> toId = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());
    private final Object2IntMap<BiomeProvider> toWeight = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());

    private int nextId = 0;

    private BiomeProviderRegistry() {}

    public void register(ResourceLocation name, BiomeProvider provider, int weight) {
        Objects.requireNonNull(name, "Biome provider id cannot be null");
        Objects.requireNonNull(provider, "Biome provider cannot be null");
        Preconditions.checkArgument(weight >= 0, "Biome provider weight may not be negative");

        if(this.byName.containsKey(name)) {
            throw new IllegalArgumentException("Duplicated biome provider id registration '" + name + "'");
        }
        if(this.toName.containsKey(provider)) {
            throw new IllegalArgumentException("Duplicated provider registration ('" + name + "')");
        }
        int id = this.nextId++;

        this.byName.put(name, provider);
        this.toName.put(provider, name);
        this.byId.size(id + 1);
        this.byId.add(id, provider);
        this.toId.put(provider, id);
        this.toWeight.put(provider, weight);
    }

    @Nullable
    public BiomeProvider get(ResourceLocation name) {
        return this.byName.get(name);
    }

    @Nullable
    public ResourceLocation getName(BiomeProvider provider) {
        return this.toName.get(provider);
    }

    @Nullable
    public BiomeProvider byId(int id) {
        return this.byId.get(id);
    }

    public int getId(BiomeProvider provider) {
        return this.toId.getOrDefault(provider, 0);
    }

    public int getId(ResourceLocation name) {
        return this.getId(this.get(name));
    }

    public int getWeight(BiomeProvider provider) {
        return this.toWeight.getOrDefault(provider, 0);
    }

    public int getWeight(ResourceLocation name) {
        return this.getWeight(this.get(name));
    }

    public Set<ResourceLocation> keys() {
        return ImmutableSet.copyOf(this.byName.keySet());
    }

    public enum Dimension {
        OVERWORLD,
        NETHER
    }
}
