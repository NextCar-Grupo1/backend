package org.pe.nextcar.customers.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
import org.pe.nextcar.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

/**
 * Customer — perfil financiero del cliente.
 * Complementa al User de IAM con datos necesarios para la evaluación crediticia.
 * userId es la referencia al User; nunca importamos User directamente (ACL).
 */
@Entity
@Getter
@NoArgsConstructor
public class Customer extends AuditableAbstractAggregateRoot<Customer> {

    @Column(unique = true, nullable = false)
    private Long   userId;

    private String documentNumber;    // DNI (8 dígitos)
    private String address;
    private String district;
    private String city;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private String occupation;        // cargo / profesión
    private String employer;          // nombre de la empresa (si es dependiente)
    private double monthlyIncome;     // ingreso mensual en soles

    private boolean profileComplete;

    public Customer(Long userId, String documentNumber, String address,
                    String district, String city, EmploymentType employmentType,
                    String occupation, String employer, double monthlyIncome) {
        this.userId           = userId;
        this.documentNumber   = documentNumber;
        this.address          = address;
        this.district         = district;
        this.city             = city;
        this.employmentType   = employmentType;
        this.occupation       = occupation;
        this.employer         = employer;
        this.monthlyIncome    = monthlyIncome;
        this.profileComplete  = true;
    }

    public void updateProfile(String address, String district, String city,
                              EmploymentType employmentType, String occupation,
                              String employer, double monthlyIncome) {
        this.address         = address;
        this.district        = district;
        this.city            = city;
        this.employmentType  = employmentType;
        this.occupation      = occupation;
        this.employer        = employer;
        this.monthlyIncome   = monthlyIncome;
    }
}