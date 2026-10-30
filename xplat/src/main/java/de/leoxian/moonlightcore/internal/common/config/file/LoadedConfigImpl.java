package de.leoxian.moonlightcore.internal.common.config.file;

import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.RestartType;
import de.leoxian.moonlightcore.internal.common.config.ConfigValueImpl;

import java.util.*;

public final class LoadedConfigImpl implements LoadedConfig {
	private final Map<ConfigKey, Object> data;

	public LoadedConfigImpl(Map<ConfigKey, Object> data) {
		this.data = data;
	}

	public LoadedConfigImpl() {
		this.data = new HashMap<>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getRaw(ConfigValue<T> configValue) {
		var ret = this.data.get(configValue.key());
		return ret == null ? configValue.defaultValue() : (T) ret;
	}

	@Override
    public <T> void setRaw(ConfigValue<T> configValue, T newValue) {
		this.data.put(configValue.key(), newValue);
		if (configValue.requiredRestartType() == RestartType.NONE) {
			((ConfigValueImpl<T>) configValue).invalidate();
		}
	}
}