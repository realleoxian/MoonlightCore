package de.leoxian.moonlightcore.common.config.schema;

public enum RestartType {
	/// The config can change without having to do any kind of restart
	NONE,
	/// The game needs to be restarted to apply changes
	GAME,
	/// The world needs to be restarted to apply changes
	WORLD
}
