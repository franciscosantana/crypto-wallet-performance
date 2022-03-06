package edu.chicoh.crypto.wallet.performance.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinCapApiConfig {
    private final String url;
    private final String apiKey;
    private final Integer timeoutSeconds;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CoinCapApiConfig(@JsonProperty("url") String url,
                            @JsonProperty("apiKey") String apiKey,
                            @JsonProperty("timeoutSeconds") Integer timeoutSeconds) {
        this.url = url;
        this.apiKey = apiKey;
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }
}
