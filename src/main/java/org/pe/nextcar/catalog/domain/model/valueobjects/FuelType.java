package org.pe.nextcar.catalog.domain.model.valueobjects;

public enum FuelType {
    GASOLINE("Gasolina"), DIESEL("Diésel"), HYBRID("Híbrido"), ELECTRIC("Eléctrico");
    private final String displayName;
    FuelType(String d) { this.displayName = d; }
    public String getDisplayName() { return displayName; }
}