package org.pe.nextcar.financial.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.valueobjects.GracePeriodType;
import org.pe.nextcar.shared.domain.model.entities.AuditableModel;

import java.time.LocalDate;

/**
 * One row of the payment schedule (cronograma de pagos).
 *
 * UI column mapping:
 *   Cuota #      -> periodNumber
 *   Fecha        -> paymentDate
 *   Capital Vivo -> initialBalance
 *   Amortización -> amortization
 *   Interés      -> interest
 *   Seguros      -> desgravamenInsurance + vehicleInsurance
 *   Portes       -> portes
 *   Cuota Total  -> totalInstallment
 */
@Entity
@Getter
@NoArgsConstructor
public class PaymentScheduleEntry extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_simulation_id", nullable = false)
    private LoanSimulation loanSimulation;

    private int         periodNumber;
    private LocalDate   paymentDate;
    private double      initialBalance;
    private double      amortization;
    private double      interest;
    private double      desgravamenInsurance;
    private double      vehicleInsurance;
    private double      portes;
    private double      totalInstallment;
    private double      finalBalance;

    @Enumerated(EnumType.STRING)
    private GracePeriodType gracePeriodType;

    private boolean balloonPeriod;

    public PaymentScheduleEntry(int periodNumber, LocalDate paymentDate,
                                double initialBalance, double amortization,
                                double interest, double desgravamenInsurance,
                                double vehicleInsurance, double portes,
                                double totalInstallment, double finalBalance,
                                GracePeriodType gracePeriodType, boolean balloonPeriod) {
        this.periodNumber         = periodNumber;
        this.paymentDate          = paymentDate;
        this.initialBalance       = initialBalance;
        this.amortization         = amortization;
        this.interest             = interest;
        this.desgravamenInsurance = desgravamenInsurance;
        this.vehicleInsurance     = vehicleInsurance;
        this.portes               = portes;
        this.totalInstallment     = totalInstallment;
        this.finalBalance         = finalBalance;
        this.gracePeriodType      = gracePeriodType;
        this.balloonPeriod        = balloonPeriod;
    }

    public void assignSimulation(LoanSimulation simulation) {
        this.loanSimulation = simulation;
    }
}