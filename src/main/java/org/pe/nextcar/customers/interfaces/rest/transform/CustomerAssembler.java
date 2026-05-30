package org.pe.nextcar.customers.interfaces.rest.transform;

import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.commands.*;
import org.pe.nextcar.customers.interfaces.rest.resources.*;

public class CustomerAssembler {

    public static CreateCustomerProfileCommand toCommand(Long userId,
                                                         CreateCustomerProfileResource r) {
        return new CreateCustomerProfileCommand(
                userId, r.documentNumber(), r.address(), r.district(), r.city(),
                r.employmentType(), r.occupation(), r.employer(), r.monthlyIncome()
        );
    }

    public static UpdateCustomerProfileCommand toCommand(Long customerId,
                                                         UpdateCustomerProfileResource r) {
        return new UpdateCustomerProfileCommand(
                customerId, r.address(), r.district(), r.city(),
                r.employmentType(), r.occupation(), r.employer(), r.monthlyIncome()
        );
    }

    public static CustomerResource toResource(Customer c) {
        return new CustomerResource(
                c.getId(), c.getUserId(), c.getDocumentNumber(),
                c.getAddress(), c.getDistrict(), c.getCity(),
                c.getEmploymentType(), c.getEmploymentType().getDisplayName(),
                c.getOccupation(), c.getEmployer(), c.getMonthlyIncome(),
                c.isProfileComplete()
        );
    }
}