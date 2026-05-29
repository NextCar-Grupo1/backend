package org.pe.nextcar.financial.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pe.nextcar.financial.domain.model.entities.PaymentScheduleEntry;
import org.pe.nextcar.financial.domain.model.valueobjects.*;
import org.pe.nextcar.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root for a vehicle loan simulation.
 * Stores input parameters + all computed financial results.
 */
@Entity
@Getter
@NoArgsConstructor
public class LoanSimulation extends AuditableAbstractAggregateRoot<LoanSimulation> {

    // ── Owner ──────────────────────────────────────────────────────────────────
    private Long userId;

    // ── Vehicle ────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private LoanCurrency currency;
    private double vehiclePrice;
    private double initialFeeRate;     // e.g. 0.20 = 20%
    private double initialFee;         // vehiclePrice * initialFeeRate
    private double principal;          // monto financiado = vehiclePrice - initialFee

    // ── Term ───────────────────────────────────────────────────────────────────
    private int       termMonths;
    private LocalDate startDate;

    // ── Method ─────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // ── Rate ───────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private RateType rateType;
    private double   rateValue;             // decimal, e.g. 0.125

    @Enumerated(EnumType.STRING)
    private CapitalizationFrequency capitalizationFrequency;

    // ── Grace ──────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private GracePeriodType gracePeriodType;
    private int gracePeriodMonths;

    // ── Financial entity & costs ───────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private FinancialEntity financialEntity;
    private double desgravamenRate;
    private double vehicleInsuranceMonthly;
    private double portesMonthly;

    // ── Computed results ───────────────────────────────────────────────────────
    private double monthlyEffectiveRate;     // TEM
    private double baseInstallment;          // cuota sin seguros ni portes
    private double totalMonthlyInstallment;  // cuota incluyendo seguros y portes
    private double npv;                      // VAN (punto de vista del deudor)
    private double monthlyIrr;               // TIR mensual
    private double tcea;                     // Tasa de Costo Efectivo Anual
    private double totalInterestPaid;
    private double totalInsurancePaid;
    private double totalAmortization;
    private double totalPaid;

    // ── Schedule ───────────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "loanSimulation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentScheduleEntry> schedule = new ArrayList<>();

    public LoanSimulation(Long userId, LoanCurrency currency, double vehiclePrice,
                          double initialFeeRate, int termMonths, LocalDate startDate,
                          PaymentMethod paymentMethod, RateType rateType, double rateValue,
                          CapitalizationFrequency capitalizationFrequency,
                          GracePeriodType gracePeriodType, int gracePeriodMonths,
                          FinancialEntity financialEntity, double desgravamenRate,
                          double vehicleInsuranceMonthly, double portesMonthly) {
        this.userId                  = userId;
        this.currency                = currency;
        this.vehiclePrice            = vehiclePrice;
        this.initialFeeRate          = initialFeeRate;
        this.initialFee              = vehiclePrice * initialFeeRate;
        this.principal               = vehiclePrice - this.initialFee;
        this.termMonths              = termMonths;
        this.startDate               = startDate;
        this.paymentMethod           = paymentMethod;
        this.rateType                = rateType;
        this.rateValue               = rateValue;
        this.capitalizationFrequency = capitalizationFrequency;
        this.gracePeriodType         = gracePeriodType;
        this.gracePeriodMonths       = gracePeriodMonths;
        this.financialEntity         = financialEntity;
        this.desgravamenRate         = desgravamenRate;
        this.vehicleInsuranceMonthly = vehicleInsuranceMonthly;
        this.portesMonthly           = portesMonthly;
    }

    public void applyCalculationResult(double monthlyEffectiveRate, double baseInstallment,
                                       double totalMonthlyInstallment, double npv,
                                       double monthlyIrr, double tcea,
                                       double totalInterestPaid, double totalInsurancePaid,
                                       double totalAmortization, double totalPaid,
                                       List<PaymentScheduleEntry> schedule) {
        this.monthlyEffectiveRate    = monthlyEffectiveRate;
        this.baseInstallment         = baseInstallment;
        this.totalMonthlyInstallment = totalMonthlyInstallment;
        this.npv                     = npv;
        this.monthlyIrr              = monthlyIrr;
        this.tcea                    = tcea;
        this.totalInterestPaid       = totalInterestPaid;
        this.totalInsurancePaid      = totalInsurancePaid;
        this.totalAmortization       = totalAmortization;
        this.totalPaid               = totalPaid;
        this.schedule                = schedule;
        schedule.forEach(e -> e.assignSimulation(this));
    }
}