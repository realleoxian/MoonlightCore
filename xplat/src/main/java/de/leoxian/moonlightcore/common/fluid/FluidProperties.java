package de.leoxian.moonlightcore.common.fluid;

public record FluidProperties(int slopeFindDistance, int levelDecreasePerBlock, int tickRate, float explosionResistance) {
	public static final FluidProperties DEFAULT = new FluidProperties(4, 1, 5, 5);

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private int slopeFindDistance = 4;
		private int levelDecreasePerBlock = 1;
		private int tickRate = 5;
		private float explosionResistance = 1;

		public Builder slopeFindDistance(int slopeFindDistance) {
			this.slopeFindDistance = slopeFindDistance;
			return this;
		}

		public Builder levelDecreasePerBlock(int levelDecreasePerBlock) {
			this.levelDecreasePerBlock = levelDecreasePerBlock;
			return this;
		}

		public Builder explosionResistance(float explosionResistance) {
			this.explosionResistance = explosionResistance;
			return this;
		}

		public Builder tickRate(int tickRate) {
			this.tickRate = tickRate;
			return this;
		}

		public FluidProperties build() {
			return new FluidProperties(slopeFindDistance, levelDecreasePerBlock, tickRate, explosionResistance);
		}
	}
}
