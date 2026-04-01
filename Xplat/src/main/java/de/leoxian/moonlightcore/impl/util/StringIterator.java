package de.leoxian.moonlightcore.impl.util;

import java.util.List;

public class StringIterator extends ImmutableIterable<String> {

    public StringIterator(List<String> backingIterable) {
        super(backingIterable);
    }

    public StringIterator(Iterable<String> backingIterable) {
        super(backingIterable);
    }

}
