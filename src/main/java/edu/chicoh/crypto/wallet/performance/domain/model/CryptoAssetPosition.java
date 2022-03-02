package edu.chicoh.crypto.wallet.performance.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CryptoAssetPosition {

    private final CryptoAsset cryptoAsset;
    private final BigDecimal currentPrice;
    private final BigDecimal currentPosition;
    private final BigDecimal performance;

    public CryptoAssetPosition(CryptoAsset cryptoAsset, BigDecimal currentPrice) {
        this.cryptoAsset = cryptoAsset;
        this.currentPrice = currentPrice;
        this.currentPosition = calculateCurrentPosition(cryptoAsset, currentPrice);
        this.performance = calculatePerformance(cryptoAsset);
    }

    private BigDecimal calculateCurrentPosition(CryptoAsset cryptoAsset, BigDecimal currentPrice) {
        return cryptoAsset.getQuantity().multiply(currentPrice);
    }

    private BigDecimal calculatePerformance(CryptoAsset cryptoAsset) {
        return (currentPosition.subtract(cryptoAsset.getOriginalPosition()))
                .divide(cryptoAsset.getOriginalPosition(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100L));
    }

    public CryptoAsset getCryptoAsset() { return cryptoAsset; }

    public BigDecimal getCurrentPrice() { return currentPrice; }

    public BigDecimal getCurrentPosition() { return currentPosition; }

    public BigDecimal getPerformance() {
        return performance;
    }
}
