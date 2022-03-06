package edu.chicoh.crypto.wallet.performance.infra.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import edu.chicoh.crypto.wallet.performance.CryptoWalletPerformanceApplication;
import edu.chicoh.crypto.wallet.performance.config.CoinCapApiConfig;
import edu.chicoh.crypto.wallet.performance.domain.service.PerformanceAnalyzerService;
import edu.chicoh.crypto.wallet.performance.infra.exception.SystemException;
import edu.chicoh.crypto.wallet.performance.infra.external.api.CoinCapApi;
import edu.chicoh.crypto.wallet.performance.infra.file.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppFactory {

    private static final Logger logger = LoggerFactory.getLogger(CryptoWalletPerformanceApplication.class);
    private static final int DEFAULT_THREAD_POOL_SIZE = 3;

    private static PerformanceAnalyzerService performanceAnalyzerService;
    private static ExecutorService executorService;
    private static CoinCapApiConfig coinCapApiConfig;
    private static CoinCapApi coinCapApi;

    public static CoinCapApiConfig getCoinCapApiConfig() {
        if (coinCapApiConfig == null) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            try {
                coinCapApiConfig = mapper.readValue(
                        new File("src/main/resources/coin-cap-api.yml"),
                        CoinCapApiConfig.class
                );
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new SystemException(e);
            }
        }
        return coinCapApiConfig;
    }

    public static PerformanceAnalyzerService getPerformanceAnalyzerService() {
        if (performanceAnalyzerService == null) {
            final CsvReader csvReader = new CsvReader();
            performanceAnalyzerService = new PerformanceAnalyzerService(csvReader, getCoinCapApi());
        }
        return performanceAnalyzerService;
    }

    public static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        }
        return executorService;
    }

    public static CoinCapApi getCoinCapApi() {
        if (coinCapApi == null) {
            coinCapApi = new CoinCapApi(new ObjectMapper(), getCoinCapApiConfig());
        }
        return coinCapApi;
    }
}
