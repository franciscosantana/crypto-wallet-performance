package edu.chicoh.crypto.wallet.performance.domain.service;

import edu.chicoh.crypto.wallet.performance.domain.model.CryptoWalletPerformance;
import edu.chicoh.crypto.wallet.performance.infra.external.api.CoinCapApi;
import edu.chicoh.crypto.wallet.performance.infra.file.CsvReader;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PerformanceAnalyzerServiceTest {

    private CsvReader csvReader;

    @Mock
    private  CoinCapApi coinCapApi;

    private EasyRandom easyRandom;

    private PerformanceAnalyzerService performanceAnalyzerService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    public void setup() {
        csvReader = new CsvReader();
        performanceAnalyzerService = new PerformanceAnalyzerService(csvReader, coinCapApi);
        easyRandom = new EasyRandom();
    }

    @Test
    public void When_analyze_crypto_wallet_performance_then_should_inform_total_wallet_also_best_and_worst_asset_performance() {
        final String fileName = "wallet-test.csv";

        when(coinCapApi.getAssetIdBySymbol("BTC")).thenReturn(Optional.of("bitcoin"));
        when(coinCapApi.getAssetIdBySymbol("ETH")).thenReturn(Optional.of("ethereum"));
        when(coinCapApi.getLastAssetPrice("bitcoin", "d1" , 1617753600000L, 1617753601000L)).thenReturn(Optional.of(new BigDecimal("56999.9728252053067291")));
        when(coinCapApi.getLastAssetPrice("ethereum", "d1", 1617753600000L, 1617753601000L)).thenReturn(Optional.of(new BigDecimal("2032.1394325557042107")));

        final Optional<CryptoWalletPerformance> cryptoWalletPerformance = performanceAnalyzerService.analyze(fileName);
        assertThat(cryptoWalletPerformance, is(not(empty())));
        cryptoWalletPerformance.ifPresent(performance -> {
            assertThat(performance.getTotal(), is(equalTo(new BigDecimal("16984.62"))));
            assertThat(performance.getBestPosition().getCryptoAsset().getSymbol(), is(equalTo("BTC")));
            assertThat(performance.getBestPosition().getPerformance(), is(equalTo(new BigDecimal("1.51"))));
            assertThat(performance.getWorstPosition().getCryptoAsset().getSymbol(), is(equalTo("ETH")));
            assertThat(performance.getWorstPosition().getPerformance(), is(equalTo(new BigDecimal("1.01"))));

            final String performanceString = "Crypto wallet performance {total=16984.62, best_asset=BTC, best_performance=1.51, worst_asset=ETH, worst_performance=1.01}";
            assertThat(performance.resultToString(), is(equalTo(performanceString)));

        });
    }
}
