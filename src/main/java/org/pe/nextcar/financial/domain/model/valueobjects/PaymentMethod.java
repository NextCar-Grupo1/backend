package org.pe.nextcar.financial.domain.model.valueobjects;

public enum PaymentMethod {
    FRENCH("Método Francés"), SMART_PURCHASE("Compra Inteligente");
    private final String displayName;
    PaymentMethod(String d) { this.displayName=d; }
    public String getDisplayName() { return displayName; }
}