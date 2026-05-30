package org.pe.nextcar.catalog.domain.model.valueobjects;

public enum VehicleCategory {
    SEDAN("Sedán"), SUV("SUV / Camioneta"), HATCHBACK("Hatchback"),
    PICKUP("Pickup"), VAN("Van / Minivan"), LUXURY("Lujo");
    private final String displayName;
    VehicleCategory(String d) { this.displayName = d; }
    public String getDisplayName() { return displayName; }
}