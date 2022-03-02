package edu.chicoh.crypto.wallet.performance;

import edu.chicoh.crypto.wallet.performance.domain.model.CryptoAssetPosition;
import edu.chicoh.crypto.wallet.performance.domain.model.CryptoWalletPerformance;
import edu.chicoh.crypto.wallet.performance.infra.factory.AppFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoWalletPerformanceApplication {

    private static final Logger logger = LoggerFactory.getLogger(CryptoWalletPerformanceApplication.class);

    public static void main(String[] args) {
        logger.info("Running Crypto Wallet Performance.");

        final String defaultFileName = "wallet-sample.csv";

        final String result = AppFactory.getPerformanceAnalyzerService()
                .analyze(defaultFileName)
                .map(CryptoWalletPerformanceApplication::resultToString)
                .orElse("Crypto wallet is empty.");

        logger.info(result);
        logger.info("Crypto wallet performance analyze completed.");
    }

    private static String resultToString(CryptoWalletPerformance cryptoWalletPerformance) {
        final StringBuilder description = new StringBuilder("Crypto wallet performance {");

        description.append("total=").append(cryptoWalletPerformance.getTotal());

        if (cryptoWalletPerformance.getBestPosition() != null) {
            final CryptoAssetPosition bestPosition = cryptoWalletPerformance.getBestPosition();
            description
                    .append(", best_asset=")
                    .append(bestPosition.getCryptoAsset().getSymbol())
                    .append(", best_performance=")
                    .append(bestPosition.getPerformance());
        }

        if (cryptoWalletPerformance.getWorstPosition() != null) {
            final CryptoAssetPosition worstPosition = cryptoWalletPerformance.getWorstPosition();
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
