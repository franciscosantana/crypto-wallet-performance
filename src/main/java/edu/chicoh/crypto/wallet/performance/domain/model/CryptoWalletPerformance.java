package edu.chicoh.crypto.wallet.performance.domain.model;

import java.math.BigDecimal;

public class CryptoWalletPerformance {

    private final BigDecimal total;
    private final CryptoAssetPosition bestPosition;
    private final CryptoAssetPosition worstPosition;

    public CryptoWalletPerformance(BigDecimal total, CryptoAssetPosition bestPosition, CryptoAssetPosition worstPosition) {
        this.total = total;
        this.bestPosition = bestPosition;
        this.worstPosition = worstPosition;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public CryptoAssetPosition getBestPosition() {
        return bestPosition;
    }

    public CryptoAssetPosition getWorstPosition() {
        return worstPosition;
    }
}
