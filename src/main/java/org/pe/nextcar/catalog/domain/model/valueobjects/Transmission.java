package org.pe.nextcar.catalog.domain.model.valueobjects;

public enum Transmission {
    MANUAL("Manual"), AUTOMATIC("Automático"), CVT("CVT");
    private final String displayName;
    Transmission(String d) { this.displayName = d; }
    public String getDisplayName() { return displayName; }
}