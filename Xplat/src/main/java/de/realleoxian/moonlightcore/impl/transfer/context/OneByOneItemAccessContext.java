/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package de.realleoxian.moonlightcore.impl.transfer.context;

import de.realleoxian.moonlightcore.api.transfer.context.ItemAccessContext;
import de.realleoxian.moonlightcore.api.transfer.item.ItemResource;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;

public record OneByOneItemAccessContext(ItemAccessContext delegate) implements ItemAccessContext {
    @Override
    public int insert(TransactionContext tx, ItemResource resource, int maxAmount) {
        return delegate.insert(tx, resource, Math.min(maxAmount, 1));
    }

    @Override
    public int extract(TransactionContext tx, ItemResource resource, int maxAmount) {
        return delegate.extract(tx, resource, Math.min(maxAmount, 1));
    }

    @Override
    public ItemResource getResource() {
        return delegate.getResource();
    }

    @Override
    public int getAmount() {
        return Math.min(1, delegate.getAmount());
    }
}
