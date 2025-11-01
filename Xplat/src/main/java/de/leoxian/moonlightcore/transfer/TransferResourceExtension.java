package de.leoxian.moonlightcore.transfer;

public interface TransferResourceExtension<V, T extends TransferResource<V>> {

    T mlcore_getCachedResource();

}
