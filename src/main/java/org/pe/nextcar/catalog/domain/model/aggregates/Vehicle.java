package org.pe.nextcar.catalog.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pe.nextcar.catalog.domain.model.valueobjects.*;
import org.pe.nextcar.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

/**
 * Vehicle — aggregate root del catálogo de vehículos.
 * Almacena toda la información del auto para mostrar en el frontend:
 * imagen, precio, specs técnicos y una cuota estimada precalculada.
 */
@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor
public class Vehicle extends AuditableAbstractAggregateRoot<Vehicle> {

    private String brand;          // Toyota, Hyundai, Kia...
    private String model;          // Corolla, Tucson, Sportage...
    private int    year;
    private double price;          // precio en soles o dólares según currency

    @Enumerated(EnumType.STRING)
    private org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency currency;

    /** URL de la imagen del vehículo. Puede ser CDN externo o path local. */
    @Column(length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private VehicleCategory category;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private Transmission transmission;

    private int    engineCC;       // cilindrada en cc, ej. 1600
    private int    seatingCapacity;

    @Column(length = 800)
    private String description;    // descripción corta para la card

    private boolean available;

    /**
     * Cuota mensual estimada precalculada (TEA 12.5%, 20% inicial, 36 meses, sin gracia).
     * Se recalcula al actualizar el precio. Solo referencial para mostrar en la card.
     */
    private double estimatedMonthlyPayment;

    public Vehicle(String brand, String model, int year, double price,
                   org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency currency,
                   String imageUrl, VehicleCategory category, FuelType fuelType,
                   Transmission transmission, int engineCC, int seatingCapacity,
                   String description) {
        this.brand             = brand;
        this.model             = model;
        this.year              = year;
        this.price             = price;
        this.currency          = currency;
        this.imageUrl          = imageUrl;
        this.category          = category;
        this.fuelType          = fuelType;
        this.transmission      = transmission;
        this.engineCC          = engineCC;
        this.seatingCapacity   = seatingCapacity;
        this.description       = description;
        this.available         = true;
        this.estimatedMonthlyPayment = calculateEstimatedPayment(price);
    }

    /** TEA 12.5%, 20% cuota inicial, 36 meses — solo para mostrar en la card. */
    private double calculateEstimatedPayment(double vehiclePrice) {
        double principal    = vehiclePrice * 0.80;
        double tem          = Math.pow(1.125, 1.0 / 12.0) - 1.0;
        int    n            = 36;
        double factor       = Math.pow(1.0 + tem, n);
        double baseInstall  = principal * tem * factor / (factor - 1.0);
        double desgravamen  = principal * 0.00050;
        return Math.round((baseInstall + desgravamen + 150 + 10) * 100.0) / 100.0;
    }

    public void markUnavailable() { this.available = false; }
    public void markAvailable()   { this.available = true; }

    public void updateDetails(String brand, String model, int year, double price,
                              org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency currency,
                              String imageUrl, VehicleCategory category, FuelType fuelType,
                              Transmission transmission, int engineCC, int seatingCapacity,
                              String description) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.currency = currency;
        this.imageUrl = imageUrl;
        this.category = category;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.engineCC = engineCC;
        this.seatingCapacity = seatingCapacity;
        this.description = description;
        this.estimatedMonthlyPayment = calculateEstimatedPayment(price);
    }
}