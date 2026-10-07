package de.leoxian.moonlightcore.neoforge.common.attachment;

import de.leoxian.moonlightcore.common.attachment.DataAttachmentHolder;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jspecify.annotations.Nullable;

public record NeoAttachmentHolderWrapper(IAttachmentHolder neoHolder) implements DataAttachmentHolder {
	@Override
	public @Nullable <T> T get(DataAttachmentType<T> type) {
		return neoHolder.getExistingDataOrNull(((NeoDataAttachmentType<T>) type).neoAttachment());
	}

	@Override
	public <T> T getOrCreate(DataAttachmentType<T> type) {
		return neoHolder.getData(((NeoDataAttachmentType<T>) type).neoAttachment());
	}

	@Override
	public boolean has(DataAttachmentType<?> type) {
		return neoHolder.hasData(((NeoDataAttachmentType<?>) type).neoAttachment());
	}

	@Override
	public <T> void set(DataAttachmentType<T> type, T value) {
		neoHolder.setData(((NeoDataAttachmentType<T>) type).neoAttachment(), value);
	}

	@Override
	public <T> T remove(DataAttachmentType<T> type) {
		return neoHolder.removeData(((NeoDataAttachmentType<T>) type).neoAttachment());
	}
}
