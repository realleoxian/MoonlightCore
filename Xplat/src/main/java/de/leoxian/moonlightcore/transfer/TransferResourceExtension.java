package de.leoxian.moonlightcore.transfer;

public interface TransferResourceExtension<T extends TransferResource<?>> {

    T mlcore_getCachedResource();

}
