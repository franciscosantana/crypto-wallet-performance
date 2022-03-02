package edu.chicoh.crypto.wallet.performance.domain.mapper;

import edu.chicoh.crypto.wallet.performance.domain.model.CryptoAsset;

import java.math.BigDecimal;

public final class CryptoAssetMapper {

    public static CryptoAsset fromMetadata(final String[] metadata) {
        final String symbol = metadata[0];
        final BigDecimal quantity = new BigDecimal(metadata[1]);
        final BigDecimal price = new BigDecimal(metadata[2]);

        return new CryptoAsset(symbol, quantity, price);
    }
}
