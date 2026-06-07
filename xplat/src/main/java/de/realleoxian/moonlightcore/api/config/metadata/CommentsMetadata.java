package de.realleoxian.moonlightcore.api.config.metadata;

import com.google.common.collect.ImmutableList;

public record CommentsMetadata(Iterable<String> comments) {
    public static final ConfigMetadataType<CommentsMetadata, CommentsMetadata.Builder> TYPE = new ConfigMetadataType<>(Builder::new, CommentsMetadata::new);

    public CommentsMetadata(Builder builder) {
        this (builder.comments.build());
    }

    public static final class Builder {
        private final ImmutableList.Builder<String> comments = ImmutableList.builder();

        public void set(String... comments) {
            for (final var comment : comments) {
                final var split = comment.split("\n");
                this.comments.add(split);
            }
        }
    }
}
