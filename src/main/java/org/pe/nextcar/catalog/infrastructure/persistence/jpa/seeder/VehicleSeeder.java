package org.pe.nextcar.catalog.infrastructure.persistence.jpa.seeder;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.valueobjects.*;
import org.pe.nextcar.catalog.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the vehicle catalog on startup.
 * Includes real Peruvian market vehicles with images from Wikimedia Commons.
 * imageUrl = URL pública de la imagen del auto.
 */
@Component
public class VehicleSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleSeeder.class);
    private final VehicleRepository repository;

    public VehicleSeeder(VehicleRepository repository) {
        this.repository = repository;
    }

    public void seed() {
        if (repository.count() > 0) {
            LOGGER.info("Vehicle catalog already seeded — skipping.");
            return;
        }

        var vehicles = List.of(

            // ── SEDANES ─────────────────────────────────────────────────────────
            new Vehicle("Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285254/corollaToyota_anjtk3.jpg",
                VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT, 1800, 5, "El sedán más vendido del Perú."),

            new Vehicle("Toyota", "Yaris", 2024, 69900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285257/yaris_xmzbx1.jpg",
                VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT, 1500, 5, "Compacto, económico y confiable."),

            new Vehicle("Nissan", "Sentra", 2024, 85900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285256/nissan_sentra_lrkfny.jpg",
                VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT, 2000, 5, "Diseño moderno, motor 2.0L."),

            new Vehicle("Hyundai", "Elantra", 2024, 82900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285254/hyundaiElantra_zatdos.jpg",
                VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT, 2000, 5, "Diseño aerodinámico premiado."),

            // ── HATCHBACKS ───────────────────────────────────────────────────────
            new Vehicle("Hyundai", "Grand i10", 2024, 55900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285254/Grandi10_r5euyc.jpg",
                VehicleCategory.HATCHBACK, FuelType.GASOLINE, Transmission.AUTOMATIC, 1200, 5, "El más accesible de nuestra línea."),

            new Vehicle("Kia", "Picanto", 2024, 49900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285255/kiaPicanto_mfh9dn.jpg",
                VehicleCategory.HATCHBACK, FuelType.GASOLINE, Transmission.AUTOMATIC, 1000, 5, "El auto más pequeño con gran carácter."),

            new Vehicle("Renault", "Logan", 2024, 59900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285256/renault_logan_ew0aea.jpg",
                VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.MANUAL, 1600, 5, "Espacioso, económico y resistente."),

            // ── SUVs ─────────────────────────────────────────────────────────────
            new Vehicle("Hyundai", "Tucson", 2024, 125900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285257/tucson_nkxm9w.jpg",
                VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC, 2000, 5, "SUV premium con diseño paramétrico."),

            new Vehicle("Kia", "Sportage", 2024, 118900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285255/kiasportage_ihpbo7.jpg",
                VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC, 2000, 5, "SUV completamente rediseñado."),

            new Vehicle("Chevrolet", "Tracker", 2024, 98900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285256/tracker_dhahp2.jpg",
                VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC, 1200, 5, "SUV compacta turbocargada."),

            new Vehicle("Toyota", "RAV4", 2024, 145900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285256/RAV4_nheegc.jpg",
                VehicleCategory.SUV, FuelType.HYBRID, Transmission.CVT, 2500, 5, "El SUV híbrido líder del mercado."),

            new Vehicle("Renault", "Duster", 2024, 79900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285255/duster_ybm8wz.jpg",
                VehicleCategory.SUV, FuelType.GASOLINE, Transmission.MANUAL, 1600, 5, "SUV aventurera con mayor despeje."),

            new Vehicle("Mazda", "CX-30", 2024, 109900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285255/mazda_g34caw.jpg",
                VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC, 2000, 5, "SUV premium con acabados de lujo."),

            // ── PICKUPS ──────────────────────────────────────────────────────────
            new Vehicle("Toyota", "Hilux", 2024, 159900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285254/hilux_mqwucl.jpg",
                VehicleCategory.PICKUP, FuelType.DIESEL, Transmission.AUTOMATIC, 2800, 5, "La pickup más vendida del Perú."),

            new Vehicle("Ford", "Ranger", 2024, 149900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285256/fordRanger_iyx3y0.jpg",
                VehicleCategory.PICKUP, FuelType.DIESEL, Transmission.AUTOMATIC, 2000, 5, "Pickup potente y versátil."),

            // ── LUXURY ───────────────────────────────────────────────────────────
            new Vehicle("BMW", "X3", 2024, 289900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285254/bmw_z0zcvi.jpg",
                VehicleCategory.LUXURY, FuelType.GASOLINE, Transmission.AUTOMATIC, 2000, 5, "El SAV premium por excelencia."),

            new Vehicle("Mercedes-Benz", "GLC", 2024, 319900.0, LoanCurrency.SOLES,
                "https://res.cloudinary.com/dteztl0mn/image/upload/v1783285256/mercedes_BEnz_aaeq2i.jpg",
                VehicleCategory.LUXURY, FuelType.GASOLINE, Transmission.AUTOMATIC, 2000, 5, "Refinamiento alemán sin igual.")
        );

        repository.saveAll(vehicles);
        LOGGER.info("Vehicle catalog seeded with {} vehicles.", vehicles.size());
    }
}