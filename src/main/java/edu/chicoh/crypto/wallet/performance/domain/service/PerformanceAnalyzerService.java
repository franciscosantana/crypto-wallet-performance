package edu.chicoh.crypto.wallet.performance.domain.service;

import edu.chicoh.crypto.wallet.performance.domain.mapper.CryptoAssetMapper;
import edu.chicoh.crypto.wallet.performance.domain.model.CryptoAsset;
import edu.chicoh.crypto.wallet.performance.domain.model.CryptoAssetPosition;
import edu.chicoh.crypto.wallet.performance.domain.model.CryptoWalletPerformance;
import edu.chicoh.crypto.wallet.performance.infra.exception.SystemException;
import edu.chicoh.crypto.wallet.performance.infra.external.api.CoinCapApi;
import edu.chicoh.crypto.wallet.performance.infra.factory.AppFactory;
import edu.chicoh.crypto.wallet.performance.infra.file.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PerformanceAnalyzerService {

    private static final String DEFAULT_INTERVAL = "d1";
    private static final long DEFAULT_START = 1617753600000L;
    private static final long DEFAULT_END = 1617753601000L;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CsvReader csvReader;
    private final CoinCapApi coinCapApi;

    public PerformanceAnalyzerService(CsvReader csvReader, CoinCapApi coinCapApi) {
        this.csvReader = csvReader;
        this.coinCapApi = coinCapApi;
    }

    public Optional<CryptoWalletPerformance> analyze(final String fileName) {

        logger.info("Analyzing crypto wallet from {}.", fileName);

        final List<CompletableFuture<Optional<CryptoAssetPosition>>> futurePositions = csvReader
                .read(fileName, true)
                .map(CryptoAssetMapper::fromMetadata)
                .map(this::getAssetPosition)
                .collect(Collectors.toList());

        final List<CryptoAssetPosition> positions = completeFuturePositions(futurePositions);

        final Optional<BigDecimal> total = getTotal(positions);
        final CryptoAssetPosition bestPosition = getBestPosition(positions);
        final CryptoAssetPosition worstPosition = getWorstPosition(positions);

        return total.map(value -> new CryptoWalletPerformance(value, bestPosition, worstPosition));
    }

    private CompletableFuture<Optional<CryptoAssetPosition>> getAssetPosition(CryptoAsset asset) {
        return CompletableFuture.supplyAsync(() ->
                coinCapApi.getAssetIdBySymbol(asset.getSymbol())
                .flatMap(assetId ->
                        coinCapApi.getLastAssetPrice(assetId, DEFAULT_INTERVAL, DEFAULT_START, DEFAULT_END)
                                .flatMap(price -> {
                                    logger.info("Get asset position for {}.", asset.getSymbol());
                                    return Optional.of(new CryptoAssetPosition(asset, price));
                                })
                ), AppFactory.getExecutorService());
    }

    private List<CryptoAssetPosition> completeFuturePositions(
            List<CompletableFuture<Optional<CryptoAssetPosition>>> completableFutures
    ) {
        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));

        CompletableFuture<List<Optional<CryptoAssetPosition>>> allCompletableFuture = allFutures
                .thenApply(future -> completableFutures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        CompletableFuture<List<CryptoAssetPosition>> completableFuture = allCompletableFuture
                .thenApply(cryptoAssetPositions ->
                        cryptoAssetPositions.stream()
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .sorted(Comparator.comparing(CryptoAssetPosition::getPerformance))
                                .collect(Collectors.toUnmodifiableList())
                );

        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error completing execution of asset positions.", e);
            throw new SystemException(e);
        }
    }

    private Optional<BigDecimal> getTotal(List<CryptoAssetPosition> positions) {
        return positions.stream()
                .map(CryptoAssetPosition::getCurrentPosition)
                .reduce(BigDecimal::add)
                .map(value -> value.setScale(2, RoundingMode.HALF_UP));
    }

    private CryptoAssetPosition getBestPosition(List<CryptoAssetPosition> positions) {
        return positions.size() > 1 ? positions.get(positions.size() - 1) : null;
    }

    private CryptoAssetPosition getWorstPosition(List<CryptoAssetPosition> positions) {
        return positions.size() >= 1 ? positions.get(0) : null;
    }
}
