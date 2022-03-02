package edu.chicoh.crypto.wallet.performance.infra.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chicoh.crypto.wallet.performance.domain.service.PerformanceAnalyzerService;
import edu.chicoh.crypto.wallet.performance.infra.external.api.CoinCapApi;
import edu.chicoh.crypto.wallet.performance.infra.file.CsvReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppFactory {

    private static final int DEFAULT_THREAD_POOL_SIZE = 3;

    private static PerformanceAnalyzerService performanceAnalyzerService;
    private static ExecutorService executorService;

    public static PerformanceAnalyzerService getPerformanceAnalyzerService() {
        if (performanceAnalyzerService == null) {
            final CsvReader csvReader = new CsvReader();
            final ObjectMapper objectMapper = new ObjectMapper();
            final CoinCapApi coinCapApi = new CoinCapApi(objectMapper);
            performanceAnalyzerService = new PerformanceAnalyzerService(csvReader, coinCapApi);
        }
        return performanceAnalyzerService;
    }

    public static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        }
        return executorService;
    }
}
