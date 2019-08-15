package com.redhat.quotegame.model;
import java.util.Objects;

public class Quote {

    private String symbol;
    private Double price;

    public Quote() {}

    public Quote(String symbol, Double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Quote quote = (Quote) o;
        return symbol == quote.symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, price);
    }

}
