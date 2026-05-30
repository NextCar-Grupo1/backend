package org.pe.nextcar.customers.domain.model.valueobjects;

public enum EmploymentType {
    DEPENDENT("Dependiente"),
    INDEPENDENT("Independiente"),
    BUSINESS_OWNER("Empresario"),
    RETIRED("Jubilado");
    private final String displayName;
    EmploymentType(String d) { this.displayName = d; }
    public String getDisplayName() { return displayName; }
}