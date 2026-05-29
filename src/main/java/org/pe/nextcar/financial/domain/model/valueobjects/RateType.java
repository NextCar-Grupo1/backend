package org.pe.nextcar.financial.domain.model.valueobjects;

public enum RateType {
    TEA("Tasa Efectiva Anual"), TNA("Tasa Nominal Anual");
    private final String displayName;
    RateType(String d) { this.displayName = d; }
    public String getDisplayName() { return displayName; }
}