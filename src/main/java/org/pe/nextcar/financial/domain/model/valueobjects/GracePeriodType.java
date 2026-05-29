package org.pe.nextcar.financial.domain.model.valueobjects;

public enum GracePeriodType {
    NONE("Sin Gracia"), PARTIAL("Gracia Parcial"), TOTAL("Gracia Total");
    private final String displayName;
    GracePeriodType(String d) { this.displayName=d; }
    public String getDisplayName() { return displayName; }
}