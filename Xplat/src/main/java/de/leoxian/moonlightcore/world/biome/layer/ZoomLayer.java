package de.leoxian.moonlightcore.world.biome.layer;

public enum ZoomLayer implements AreaTransformer1 {
    NORMAL,
    FUZZY {
        @Override
        protected int modeOrRandom(AreaContext context, int a, int b, int c, int d) {
            return context.random(a, b, c, d);
        }
    };

    @Override
    public int apply(AreaContext context, Area area, int x, int z) {
        int parentX = x >> 1;
        int parentZ = z >> 1;
        int i = area.get(parentX, parentZ);

        int xOffset = x & 1;
        int zOffset = z & 1;

        if (xOffset == 0 && zOffset == 0) {
            return i;
        }

        context.initializeRandom(x & ~1, z & ~1);

        if (xOffset == 0) {
            return context.random(i, area.get(parentX, parentZ + 1));
        }

        int l = area.get(parentX + 1, parentZ);
        if (zOffset == 0) {
            return context.random(i, l);
        }

        int j = area.get(parentX, parentZ + 1);
        int n = area.get(parentX + 1, parentZ + 1);
        return this.modeOrRandom(context, i, l, j, n);
    }

    protected int modeOrRandom(AreaContext context, int a, int b, int c, int d) {
        if (b == c && c == d) return b;
        if (a == b && a == c) return a;
        if (a == b && a == d) return a;
        if (a == c && a == d) return a;

        if (a == b && c != d) return a;
        if (a == c && b != d) return a;
        if (a == d && b != c) return a;
        if (b == c && a != d) return b;
        if (b == d && a != c) return b;
        if (c == d && a != b) return c;

        return context.random(a, b, c, d);
    }
}
