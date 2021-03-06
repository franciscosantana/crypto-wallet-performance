package edu.chicoh.crypto.wallet.performance.infra.external.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chicoh.crypto.wallet.performance.config.CoinCapApiConfig;
import edu.chicoh.crypto.wallet.performance.infra.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static edu.chicoh.crypto.wallet.performance.infra.external.api.constants.CoinCapApiConstants.Fields.ID;
import static edu.chicoh.crypto.wallet.performance.infra.external.api.constants.CoinCapApiConstants.Fields.PRICE_USD;
import static edu.chicoh.crypto.wallet.performance.infra.external.api.constants.CoinCapApiConstants.Fields.SYMBOL;
import static edu.chicoh.crypto.wallet.performance.infra.external.api.constants.CoinCapApiConstants.Fields.TIME;

public class CoinCapApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    private final CoinCapApiConfig coinCapApiConfig;

    public CoinCapApi(ObjectMapper objectMapper, CoinCapApiConfig coinCapApiConfig) {
        this.objectMapper = objectMapper;
        this.coinCapApiConfig = coinCapApiConfig;
    }

    public Optional<String> getAssetIdBySymbol(String symbol) {
        logger.info("Get ID for {} asset.", symbol);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(coinCapApiConfig.getTimeoutSeconds(), ChronoUnit.SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(coinCapApiConfig.getUrl() + "?search=" + symbol))
                .header("Authorization", "Bearer " + coinCapApiConfig.getApiKey())
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.error("Error getting asset ID.", e);
            throw new SystemException(e);
        }

        if (response.statusCode() == 200) {
            return extractAssetIdFromResponse(symbol, response);
        } else if (response.statusCode() >= 400) {
            logger.error("Fail to get asset by symbol {}: {} - {}", symbol, response.statusCode(), response.body());
        } else {
            logger.warn("Status not recognized to get asset by symbol {}: {} - {}", symbol, response.statusCode(), response.body());
        }

        return Optional.empty();
    }

    private Optional<String> extractAssetIdFromResponse(String symbol, HttpResponse<String> response) {
        Hashtable jsonMap;
        try {
            jsonMap = objectMapper.readValue(response.body(), Hashtable.class);
        } catch (JsonProcessingException e) {
            logger.error("Error mapping response from asset id request. ", e);
            throw new SystemException(e);
        }

        final List<Map<String, Object>> data = (List<Map<String, Object>>) jsonMap.get("data");
        return data
                .stream()
                .filter(o -> symbol.equals(o.get(SYMBOL)))
                .map(o -> (String) o.get(ID))
                .findAny();
    }

    public Optional<BigDecimal> getLastAssetPrice(String assetId, String interval, long start, long end) {
        logger.info("Get last price for asset {} in period.", assetId);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(coinCapApiConfig.getTimeoutSeconds(), ChronoUnit.SECONDS))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(coinCapApiConfig.getUrl() + "/" + assetId + "/history?interval=" + interval +
                        "&start=" + start + "&end=" + end))
                .header("Authorization", "Bearer " + coinCapApiConfig.getApiKey())
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.error("Error getting asset price by id " + assetId, e);
            throw new SystemException(e);
        }

        if (response.statusCode() == 200) {
            return extractLastAssetPriceFromResponse(response);
        } else if (response.statusCode() >= 400) {
            logger.error(
                    "Error getting asset price by id {}: {} - {}",
                    assetId,
                    response.statusCode(),
                    response.body()
            );
        } else {
            logger.warn(
                    "Status not recognized to get last asset price by id {}: {} - {}",
                    assetId,
                    response.statusCode(),
                    response.body()
            );
        }

        return Optional.empty();
    }

    private Optional<BigDecimal> extractLastAssetPriceFromResponse(HttpResponse<String> response) {
        Hashtable jsonMap;
        try {
            jsonMap = objectMapper.readValue(response.body(), Hashtable.class);
        } catch (JsonProcessingException e) {
            logger.error("Error mapping response from asset price request. ", e);
            throw new SystemException(e);
        }
        final List<Map<String, Object>> data = (List<Map<String, Object>>) jsonMap.get("data");
        return data
                .stream()
                .sorted((o1, o2) -> ((Long) o2.get(TIME)).compareTo((Long) o1.get(TIME)))
                .map(o -> new BigDecimal((String) o.get(PRICE_USD)))
                .findFirst();
    }
}
