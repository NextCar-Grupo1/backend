package org.pe.nextcar.financial.interfaces.rest.transform;

import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.commands.CreateLoanSimulationCommand;
import org.pe.nextcar.financial.interfaces.rest.resources.*;

public class LoanSimulationAssembler {

    /** Resource + userId → Command */
    public static CreateLoanSimulationCommand toCommand(Long userId,
                                                        CreateLoanSimulationResource r) {
        return new CreateLoanSimulationCommand(
                userId,
                r.currency(), r.vehiclePrice(), r.initialFeeRate(),
                r.termMonths(), r.startDate(), r.paymentMethod(),
                r.rateType(), r.rateValue(), r.capitalizationFrequency(),
                r.gracePeriodType(), r.gracePeriodMonths(),
                r.financialEntity(), r.desgravamenRate(),
                r.vehicleInsuranceMonthly(), r.portesMonthly()
        );
    }

    /** Aggregate → Full response resource */
    public static LoanSimulationResource toResource(LoanSimulation s) {
        var scheduleResources = s.getSchedule().stream()
                .map(e -> new PaymentScheduleEntryResource(
                        e.getPeriodNumber(), e.getPaymentDate(),
                        e.getInitialBalance(), e.getAmortization(), e.getInterest(),
                        e.getDesgravamenInsurance(), e.getVehicleInsurance(), e.getPortes(),
                        e.getTotalInstallment(), e.getFinalBalance(),
                        e.getGracePeriodType(), e.isBalloonPeriod()
                )).toList();

        return new LoanSimulationResource(
                s.getId(),
                s.getCurrency(), s.getVehiclePrice(), s.getInitialFee(), s.getPrincipal(),
                s.getTermMonths(), s.getStartDate(), s.getPaymentMethod(),
                s.getRateType(), s.getRateValue(), s.getCapitalizationFrequency(),
                s.getGracePeriodType(), s.getGracePeriodMonths(),
                s.getFinancialEntity().getDisplayName(),
                s.getMonthlyEffectiveRate(), s.getBaseInstallment(),
                s.getTotalMonthlyInstallment(), s.getNpv(), s.getMonthlyIrr(), s.getTcea(),
                s.getTotalInterestPaid(), s.getTotalInsurancePaid(),
                s.getTotalAmortization(), s.getTotalPaid(),
                scheduleResources
        );
    }
}