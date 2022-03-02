package edu.chicoh.crypto.wallet.performance.domain.model;

import java.math.BigDecimal;

public class CryptoAsset {

    private final String symbol;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final BigDecimal originalPosition;

    public CryptoAsset(String symbol, BigDecimal quantity, BigDecimal price) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.originalPosition = calculateOriginalPosition(quantity, price);
    }

    private BigDecimal calculateOriginalPosition(BigDecimal quantity, BigDecimal price) {
        return quantity.multiply(price);
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getOriginalPosition() {
        return originalPosition;
    }
}
