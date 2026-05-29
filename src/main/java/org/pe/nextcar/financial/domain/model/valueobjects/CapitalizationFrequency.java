package org.pe.nextcar.financial.domain.model.valueobjects;

/** Used to convert TNA to TEM: TEM = (1 + TNA/m)^(m/12) - 1 */
public enum CapitalizationFrequency {
    DAILY(360,"Diaria"), MONTHLY(12,"Mensual"), BIMONTHLY(6,"Bimestral"),
    QUARTERLY(4,"Trimestral"), SEMI_ANNUAL(2,"Semestral"), ANNUAL(1,"Anual");
    private final int periodsPerYear;
    private final String displayName;
    CapitalizationFrequency(int p, String d) { this.periodsPerYear=p; this.displayName=d; }
    public int getPeriodsPerYear() { return periodsPerYear; }
    public String getDisplayName() { return displayName; }
}