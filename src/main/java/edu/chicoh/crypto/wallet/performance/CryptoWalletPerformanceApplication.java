package edu.chicoh.crypto.wallet.performance;

import edu.chicoh.crypto.wallet.performance.domain.model.CryptoWalletPerformance;
import edu.chicoh.crypto.wallet.performance.infra.factory.AppFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoWalletPerformanceApplication {

    private static final Logger logger = LoggerFactory.getLogger(CryptoWalletPerformanceApplication.class);

    public static void main(String[] args) {
        try {
            logger.info("Running Crypto Wallet Performance.");

            final String defaultFileName = "wallet-sample.csv";

            final String result = AppFactory.getPerformanceAnalyzerService()
                    .analyze(defaultFileName)
                    .map(CryptoWalletPerformance::resultToString)
                    .orElse("Crypto wallet is empty.");

            logger.info(result);
            logger.info("Crypto wallet performance analyze completed.");

        } finally {
            AppFactory.getExecutorService().shutdown();
        }
    }
}
