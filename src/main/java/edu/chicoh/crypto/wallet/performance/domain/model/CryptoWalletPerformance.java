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

    public String resultToString() {
        final StringBuilder description = new StringBuilder("Crypto wallet performance {");

        description.append("total=").append(getTotal());

        if (getBestPosition() != null) {
            final CryptoAssetPosition bestPosition = getBestPosition();
            description
                    .append(", best_asset=")
                    .append(bestPosition.getCryptoAsset().getSymbol())
                    .append(", best_performance=")
                    .append(bestPosition.getPerformance());
        }

        if (getWorstPosition() != null) {
            final CryptoAssetPosition worstPosition = getWorstPosition();
            description
                    .append(", worst_asset=")
                    .append(worstPosition.getCryptoAsset().getSymbol())
                    .append(", worst_performance=")
                    .append(worstPosition.getPerformance());
        }

        description.append("}");
        return description.toString();
    }
}
