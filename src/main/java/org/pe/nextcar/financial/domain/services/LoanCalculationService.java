package org.pe.nextcar.financial.domain.services;



import org.pe.nextcar.financial.domain.model.entities.PaymentScheduleEntry;
import org.pe.nextcar.financial.domain.model.valueobjects.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  LOAN CALCULATION SERVICE — Algoritmo financiero completo
 * ═══════════════════════════════════════════════════════════════════
 *
 *  Rate conversions:
 *    TEA  → TEM : TEM = (1 + TEA)^(1/12) − 1
 *    TNA  → TEM : TEM = (1 + TNA/m)^(m/12) − 1   where m = capitalization periods/year
 *
 *  French method (cuota fija):
 *    C = P · i · (1+i)^n / ((1+i)^n − 1)
 *
 *  Smart Purchase (cuota reducida + balón final):
 *    C = (P − VR·(1+i)^−n) · i / (1 − (1+i)^−n)
 *    At last period: client pays C + VR (balloon)
 *
 *  Grace periods:
 *    PARTIAL : pay only interest; balance constant; recalculate C after grace
 *    TOTAL   : capitalize interest to balance; pay nothing; recalculate C on new balance
 *
 *  Insurance per period:
 *    Desgravamen = initialBalance * desgravamenRate
 *    Vehicular   = flat monthly amount
 *    Portes      = flat monthly amount
 *
 *  VAN (debtor perspective):
 *    VAN = P − Σ [ Cuota_k / (1+i)^k ]
 *
 *  TIR (monthly IRR via Newton-Raphson):
 *    Find r such that: 0 = P − Σ [ Cuota_k / (1+r)^k ]
 *
 *  TCEA:
 *    TCEA = (1 + TIR_mensual)^12 − 1
 */
@Service
public class LoanCalculationService {

    // ── Input / Output ─────────────────────────────────────────────────────────

    public record CalculationInput(
            double          vehiclePrice,
            double          principal,
            int             termMonths,
            LocalDate       startDate,
            PaymentMethod   paymentMethod,
            RateType        rateType,
            double          rateValue,
            CapitalizationFrequency capitalizationFrequency,
            GracePeriodType gracePeriodType,
            int             gracePeriodMonths,
            FinancialEntity financialEntity,
            double          desgravamenRate,
            double          vehicleInsuranceMonthly,
            double          portesMonthly
    ) {}

    public record CalculationResult(
            double                     monthlyEffectiveRate,
            double                     baseInstallment,
            double                     totalMonthlyInstallment,
            double                     npv,
            double                     monthlyIrr,
            double                     tcea,
            double                     totalInterestPaid,
            double                     totalInsurancePaid,
            double                     totalAmortization,
            double                     totalPaid,
            List<PaymentScheduleEntry> schedule
    ) {}

    // ── Main entry point ───────────────────────────────────────────────────────

    public CalculationResult calculate(CalculationInput in) {

        // Step 1: convert rate to TEM
        double i = toMonthlyEffectiveRate(in.rateValue(), in.rateType(),
                in.capitalizationFrequency());
        int n = in.termMonths();
        int g = in.gracePeriodMonths();
        int payingPeriods = n - g;

        // Step 2: compute post-grace balance (TOTAL grace inflates balance)
        double postGraceBalance = in.principal();
        if (in.gracePeriodType() == GracePeriodType.TOTAL && g > 0) {
            postGraceBalance = round(in.principal() * Math.pow(1.0 + i, g));
        }

        // Step 3: compute base installment (without insurance/portes)
        double residualValue = 0;
        double baseInstallment;
        if (in.paymentMethod() == PaymentMethod.SMART_PURCHASE) {
            residualValue   = round(in.vehiclePrice() * in.financialEntity().getSmartPurchaseResidualRate());
            baseInstallment = calculateSmartPurchaseInstallment(postGraceBalance, residualValue, i, payingPeriods);
        } else {
            baseInstallment = calculateFrenchInstallment(postGraceBalance, i, payingPeriods);
        }

        // Step 4: build full schedule
        List<PaymentScheduleEntry> schedule = buildSchedule(in, i, g, n, baseInstallment, residualValue);

        // Step 5: totals
        double totalInterest     = schedule.stream().mapToDouble(PaymentScheduleEntry::getInterest).sum();
        double totalInsurance    = schedule.stream()
                .mapToDouble(e -> e.getDesgravamenInsurance() + e.getVehicleInsurance()).sum();
        double totalAmortization = schedule.stream().mapToDouble(PaymentScheduleEntry::getAmortization).sum();
        double totalPaid         = schedule.stream().mapToDouble(PaymentScheduleEntry::getTotalInstallment).sum();

        double totalMonthlyInstallment = schedule.stream()
                .filter(e -> e.getGracePeriodType() == GracePeriodType.NONE && !e.isBalloonPeriod())
                .mapToDouble(PaymentScheduleEntry::getTotalInstallment)
                .findFirst()
                .orElse(0);

        // Step 6: VAN, TIR, TCEA
        double npv        = calculateNPV(in.principal(), schedule, i);
        double monthlyIrr = calculateIRR(in.principal(), schedule);
        double tcea       = Math.pow(1.0 + monthlyIrr, 12) - 1.0;

        return new CalculationResult(
                round(i), round(baseInstallment), round(totalMonthlyInstallment),
                round(npv), round(monthlyIrr), round(tcea),
                round(totalInterest), round(totalInsurance),
                round(totalAmortization), round(totalPaid),
                schedule
        );
    }

    // ── Rate conversions ───────────────────────────────────────────────────────

    public double toMonthlyEffectiveRate(double rateValue, RateType rateType,
                                         CapitalizationFrequency freq) {
        if (rateType == RateType.TEA) {
            return Math.pow(1.0 + rateValue, 1.0 / 12.0) - 1.0;
        }
        double ratePerPeriod   = rateValue / freq.getPeriodsPerYear();
        double periodsPerMonth = (double) freq.getPeriodsPerYear() / 12.0;
        return Math.pow(1.0 + ratePerPeriod, periodsPerMonth) - 1.0;
    }

    // ── Installment formulas ───────────────────────────────────────────────────

    /**
     * French: C = P·i·(1+i)^n / ((1+i)^n − 1)
     */
    public double calculateFrenchInstallment(double principal, double i, int n) {
        if (i == 0) return principal / n;
        double factor = Math.pow(1.0 + i, n);
        return principal * i * factor / (factor - 1.0);
    }

    /**
     * Smart Purchase: C = (P − VR·(1+i)^−n) · i / (1 − (1+i)^−n)
     * After n periods of paying C, the outstanding balance = VR exactly.
     * The client pays VR as balloon on the last period.
     */
    public double calculateSmartPurchaseInstallment(double principal, double residualValue,
                                                    double i, int n) {
        double discountFactor    = Math.pow(1.0 + i, -n);
        double adjustedPrincipal = principal - residualValue * discountFactor;
        return calculateFrenchInstallment(adjustedPrincipal, i, n);
    }

    // ── Schedule builder ───────────────────────────────────────────────────────

    private List<PaymentScheduleEntry> buildSchedule(CalculationInput in, double i,
                                                     int g, int n,
                                                     double baseInstallment,
                                                     double residualValue) {
        List<PaymentScheduleEntry> entries = new ArrayList<>();
        double    balance = in.principal();
        LocalDate date    = in.startDate().plusMonths(1);

        for (int k = 1; k <= n; k++) {
            double          initialBalance = balance;
            double          interest       = round(initialBalance * i);
            double          amortization;
            double          totalInstallment;
            GracePeriodType entryGrace;
            boolean         isBalloon      = false;

            // ── Grace period ──────────────────────────────────────────────────
            if (k <= g) {
                entryGrace = in.gracePeriodType();
                if (in.gracePeriodType() == GracePeriodType.PARTIAL) {
                    amortization = 0;
                    // balance unchanged
                } else {
                    // TOTAL: capitalize interest
                    amortization = 0;
                    balance      = round(balance + interest);
                }
                // ── Regular period ────────────────────────────────────────────────
            } else {
                entryGrace = GracePeriodType.NONE;

                if (in.paymentMethod() == PaymentMethod.SMART_PURCHASE && k == n) {
                    // Balloon period: amortize everything (balance should ≈ VR)
                    // C already reduces balance to VR; client also pays VR as balloon
                    amortization = round(initialBalance);
                    isBalloon    = true;
                } else {
                    amortization = round(baseInstallment - interest);
                }
                balance = round(initialBalance - amortization);
            }

            // ── Costs ─────────────────────────────────────────────────────────
            boolean isTotalGracePeriod = (k <= g) && in.gracePeriodType() == GracePeriodType.TOTAL;
            double desgravamen = isTotalGracePeriod ? 0 : round(initialBalance * in.desgravamenRate());
            double vehicleIns  = isTotalGracePeriod ? 0 : round(in.vehicleInsuranceMonthly());
            double portes      = isTotalGracePeriod ? 0 : round(in.portesMonthly());

            // ── Total installment ─────────────────────────────────────────────
            if (entryGrace == GracePeriodType.TOTAL) {
                totalInstallment = 0;   // nothing paid; interest capitalized above
            } else if (entryGrace == GracePeriodType.PARTIAL) {
                totalInstallment = round(interest + desgravamen + vehicleIns + portes);
            } else if (isBalloon) {
                // Last period: regular installment + balloon (VR) + insurance
                totalInstallment = round(baseInstallment + initialBalance + desgravamen + vehicleIns + portes);
            } else {
                totalInstallment = round(baseInstallment + desgravamen + vehicleIns + portes);
            }

            entries.add(new PaymentScheduleEntry(
                    k, date,
                    round(initialBalance), amortization, interest,
                    desgravamen, vehicleIns, portes,
                    totalInstallment, round(balance),
                    entryGrace, isBalloon
            ));
            date = date.plusMonths(1);
        }
        return entries;
    }

    // ── VAN ────────────────────────────────────────────────────────────────────

    /**
     * Debtor receives principal at t=0, pays installments at t=1..n.
     * VAN = P − Σ [ Cuota_k / (1+i)^k ]
     */
    public double calculateNPV(double principal, List<PaymentScheduleEntry> schedule,
                               double discountRate) {
        double npv = principal;
        for (int k = 0; k < schedule.size(); k++) {
            npv -= schedule.get(k).getTotalInstallment() / Math.pow(1.0 + discountRate, k + 1);
        }
        return npv;
    }

    // ── TIR via Newton-Raphson ─────────────────────────────────────────────────

    /**
     * Finds monthly r such that: 0 = P − Σ [ Cuota_k / (1+r)^k ]
     * Annualize: TCEA = (1 + r)^12 − 1
     */
    public double calculateIRR(double principal, List<PaymentScheduleEntry> schedule) {
        double[] cf = new double[schedule.size() + 1];
        cf[0] = principal;
        for (int k = 0; k < schedule.size(); k++) {
            cf[k + 1] = -schedule.get(k).getTotalInstallment();
        }
        return newtonRaphsonIRR(cf);
    }

    private double newtonRaphsonIRR(double[] cashFlows) {
        double r         = 0.01;
        double tolerance = 1e-8;
        int    maxIter   = 1_000;

        for (int iter = 0; iter < maxIter; iter++) {
            double f = 0, df = 0;
            for (int t = 0; t < cashFlows.length; t++) {
                double d = Math.pow(1.0 + r, t);
                f  += cashFlows[t] / d;
                if (t > 0) df -= t * cashFlows[t] / (d * (1.0 + r));
            }
            if (Math.abs(df) < 1e-12) break;
            double rNew = r - f / df;
            if (Math.abs(rNew - r) < tolerance) return rNew;
            r = rNew;
        }
        return r;
    }

    // ── Utility ────────────────────────────────────────────────────────────────

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}