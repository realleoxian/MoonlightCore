package de.realleoxian.moonlightcore.api.config.metadata;

import com.google.common.collect.ImmutableList;
import de.realleoxian.moonlightcore.api.util.ImmutableIterable;

import java.util.Arrays;

public final class CommentsMetadata extends ImmutableIterable<String> {
    public static final ConfigMetadataType<CommentsMetadata, CommentsMetadata.Builder> TYPE = new ConfigMetadataType<>(CommentsMetadata.Builder::new, CommentsMetadata::new, false);

    private CommentsMetadata(CommentsMetadata.Builder builder) {
        super(builder.comments.build());
    }

    public static final class Builder {
        private final ImmutableList.Builder<String> comments = new ImmutableList.Builder<>();

        public void set(String... comments) {
            for (final var comment : comments) {
                final var split = comment.split("\n");
                this.comments.addAll(Arrays.asList(split));
            }
        }

        private Builder() {}
    }
}
