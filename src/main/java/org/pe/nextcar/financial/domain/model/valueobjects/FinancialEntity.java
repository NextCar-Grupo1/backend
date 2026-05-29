package org.pe.nextcar.financial.domain.model.valueobjects;

import lombok.Getter;

/**
 * Authorized Peruvian financial entities for vehicle loans.
 * Each bank defines: residual % for Smart Purchase, default insurance rates,
 * default portes, and maximum grace period months.
 */
@Getter
public enum FinancialEntity {

    BCP(        "Banco de Crédito del Perú (BCP)", 0.40, 0.00050, 150.00, 10.00, 6),
    BBVA(       "BBVA Continental",                0.35, 0.00045, 140.00,  8.00, 6),
    SCOTIABANK( "Scotiabank Perú",                 0.30, 0.00050, 130.00, 12.00, 4),
    INTERBANK(  "Interbank",                       0.35, 0.00048, 145.00,  9.00, 6),
    MIBANCO(    "MiBanco",                         0.25, 0.00055, 120.00,  8.00, 3),
    BANBIF(     "BanBIF",                          0.30, 0.00050, 135.00, 10.00, 4),
    PICHINCHA(  "Banco Pichincha",                 0.30, 0.00050, 130.00,  8.00, 4),
    CREDISCOTIA("CrediScotia Financiera",          0.25, 0.00055, 125.00, 10.00, 3);

    private final String displayName;
    private final double smartPurchaseResidualRate;
    private final double defaultDesgravamenRate;
    private final double defaultVehicleInsurance;
    private final double defaultPortes;
    private final int    maxGracePeriodMonths;

    FinancialEntity(String displayName, double smartPurchaseResidualRate,
                    double defaultDesgravamenRate, double defaultVehicleInsurance,
                    double defaultPortes, int maxGracePeriodMonths) {
        this.displayName               = displayName;
        this.smartPurchaseResidualRate = smartPurchaseResidualRate;
        this.defaultDesgravamenRate    = defaultDesgravamenRate;
        this.defaultVehicleInsurance   = defaultVehicleInsurance;
        this.defaultPortes             = defaultPortes;
        this.maxGracePeriodMonths      = maxGracePeriodMonths;
    }

}