package org.pe.nextcar.financial.domain.model.valueobjects;

public enum LoanCurrency {
    SOLES("S/"), DOLLARS("$");
    private final String symbol;
    LoanCurrency(String s) { this.symbol = s; }
    public String getSymbol() { return symbol; }
}