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
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/2023_Toyota_Corolla_%28ZWE219R%29_hybrid_sedan%2C_front_8.23.22.jpg/1280px-2023_Toyota_Corolla_%28ZWE219R%29_hybrid_sedan%2C_front_8.23.22.jpg",
                        VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT,
                        1800, 5, "El sedán más vendido del Perú. Motor 1.8L, pantalla táctil 8\", Toyota Safety Sense de serie."), // <-- Falta coma y escapar comilla

                new Vehicle("Toyota", "Yaris", 2024, 69900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/2020_Toyota_Yaris_%28MXPH10R%29_hybrid_sedan%2C_front_10.15.20.jpg/1280px-2020_Toyota_Yaris_%28MXPH10R%29_hybrid_sedan%2C_front_10.15.20.jpg",
                        VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT,
                        1500, 5, "Compacto, económico y confiable. Motor 1.5L, ideal para ciudad y carretera."), // <-- Faltaba coma

                new Vehicle("Nissan", "Sentra", 2024, 85900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/2020_Nissan_Sentra_SR%2C_front_9.24.20.jpg/1280px-2020_Nissan_Sentra_SR%2C_front_9.24.20.jpg",
                        VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT,
                        2000, 5, "Diseño moderno, motor 2.0L con 149 HP, pantalla 8\" Apple CarPlay/Android Auto."), // <-- Falta coma y escapar comilla

                new Vehicle("Hyundai", "Elantra", 2024, 82900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/2021_Hyundai_Elantra_SE_%28US%29%2C_front_10.15.20.jpg/1280px-2021_Hyundai_Elantra_SE_%28US%29%2C_front_10.15.20.jpg",
                        VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT,
                        2000, 5, "Diseño aerodinámico premiado. Motor 2.0L IVT, cámara de retroceso, sensores de parqueo."), // <-- Faltaba coma

                // ── HATCHBACKS ───────────────────────────────────────────────────────
                new Vehicle("Hyundai", "Grand i10", 2024, 55900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/2020_Hyundai_Grand_i10_1.0_MPi_Premium_%28facelift%2C_red%29%2C_front_8.1.20.jpg/1280px-2020_Hyundai_Grand_i10_1.0_MPi_Premium_%28facelift%2C_red%29%2C_front_8.1.20.jpg",
                        VehicleCategory.HATCHBACK, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        1200, 5, "El más accesible de nuestra línea. Motor 1.2L, pantalla 8\", ideal para empezar."), // <-- Falta coma y escapar comilla

                new Vehicle("Kia", "Picanto", 2024, 49900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/2017_Kia_Picanto_GT-Line_%281.0_T-GDi%29%2C_front_8.6.17.jpg/1280px-2017_Kia_Picanto_GT-Line_%281.0_T-GDi%29%2C_front_8.6.17.jpg",
                        VehicleCategory.HATCHBACK, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        1000, 5, "El auto más pequeño con gran carácter. Motor 1.0L turbo, diseño llamativo."), // <-- Faltaba coma

                new Vehicle("Renault", "Logan", 2024, 59900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/2021_Renault_Logan_facelift%2C_front_2.23.21.jpg/1280px-2021_Renault_Logan_facelift%2C_front_2.23.21.jpg",
                        VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.MANUAL,
                        1600, 5, "Espacioso, económico y resistente. Motor 1.6L SCe, maletero de 510 litros."), // <-- Faltaba coma

                // ── SUVs ─────────────────────────────────────────────────────────────
                new Vehicle("Hyundai", "Tucson", 2024, 125900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/2022_Hyundai_Tucson_%28NX4.V2%29_2.0_MPi_Elite_2WD_%28facelift%2C_white%29%2C_front_7.13.22.jpg/1280px-2022_Hyundai_Tucson_%28NX4.V2%29_2.0_MPi_Elite_2WD_%28facelift%2C_white%29%2C_front_7.13.22.jpg",
                        VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        2000, 5, "SUV premium con diseño paramétrico. Motor 2.0L, techo panorámico, pantalla 10.25\"."), // <-- Falta coma y escapar comilla

                new Vehicle("Kia", "Sportage", 2024, 118900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/2023_Kia_Sportage_X-Line%2C_front_9.9.22.jpg/1280px-2023_Kia_Sportage_X-Line%2C_front_9.9.22.jpg",
                        VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        2000, 5, "SUV completamente rediseñado. Pantalla 12.3\", sistema Kia Connect, diseño futurista."), // <-- Falta coma y escapar comilla

                new Vehicle("Chevrolet", "Tracker", 2024, 98900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/99/2021_Chevrolet_Tracker_1.2T_Premier_%28facelift%2C_red%29%2C_front_10.17.21.jpg/1280px-2021_Chevrolet_Tracker_1.2T_Premier_%28facelift%2C_red%29%2C_front_10.17.21.jpg",
                        VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        1200, 5, "SUV compacta turbocargada. Motor 1.2T 133 HP, pantalla 8\", WiFi a bordo."), // <-- Falta coma y escapar comilla

                new Vehicle("Toyota", "RAV4", 2024, 145900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/2019_Toyota_RAV4_%28AXAH54R%29_GXL_%28hybrid%29_wagon_%282019-10-17%29_01.jpg/1280px-2019_Toyota_RAV4_%28AXAH54R%29_GXL_%28hybrid%29_wagon_%282019-10-17%29_01.jpg",
                        VehicleCategory.SUV, FuelType.HYBRID, Transmission.CVT,
                        2500, 5, "El SUV híbrido líder del mercado. Sistema AWD, motor 2.5L híbrido, pantalla 10.5\"."), // <-- Falta coma y escapar comilla

                new Vehicle("Renault", "Duster", 2024, 79900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Renault_Duster_2018_%2826042618607%29.jpg/1280px-Renault_Duster_2018_%2826042618607%29.jpg",
                        VehicleCategory.SUV, FuelType.GASOLINE, Transmission.MANUAL,
                        1600, 5, "SUV aventurera con mayor despeje del suelo. Motor 1.6L, tracción 4x2 y 4x4 disponible."), // <-- Faltaba coma

                new Vehicle("Mazda", "CX-30", 2024, 109900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/2019_Mazda_CX-30_2.0_Skyactiv-G_Exclusive_%28facelift%2C_silver%29%2C_front_8.20.20.jpg/1280px-2019_Mazda_CX-30_2.0_Skyactiv-G_Exclusive_%28facelift%2C_silver%29%2C_front_8.20.20.jpg",
                        VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        2000, 5, "SUV premium con acabados de lujo. Motor Skyactiv-G 2.0L, i-Activsense de serie."), // <-- Faltaba coma

                // ── PICKUPS ──────────────────────────────────────────────────────────
                new Vehicle("Toyota", "Hilux", 2024, 159900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/2020_Toyota_Hilux_%28GUN126R%29_SR5_%284x4%29_double_cab_utility%2C_front_8.11.20.jpg/1280px-2020_Toyota_Hilux_%28GUN126R%29_SR5_%284x4%29_double_cab_utility%2C_front_8.11.20.jpg",
                        VehicleCategory.PICKUP, FuelType.DIESEL, Transmission.AUTOMATIC,
                        2800, 5, "La pickup más vendida del Perú. Motor 2.8L Diesel 204 HP, 4x4 permanente, máxima confiabilidad."), // <-- Faltaba coma

                new Vehicle("Ford", "Ranger", 2024, 149900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/2023_Ford_Ranger_XLT_4WD_double_cab_utility%2C_front_9.15.23.jpg/1280px-2023_Ford_Ranger_XLT_4WD_double_cab_utility%2C_front_9.15.23.jpg",
                        VehicleCategory.PICKUP, FuelType.DIESEL, Transmission.AUTOMATIC,
                        2000, 5, "Pickup potente y versátil. Motor 2.0L EcoBlue Diesel, tracción 4x4, tecnología SYNC 4."), // <-- Faltaba coma

                // ── LUXURY ───────────────────────────────────────────────────────────
                new Vehicle("BMW", "X3", 2024, 289900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/2022_BMW_X3_%28G01_LCI%29_sDrive20i_automatic%2C_front_8.27.21.jpg/1280px-2022_BMW_X3_%28G01_LCI%29_sDrive20i_automatic%2C_front_8.27.21.jpg",
                        VehicleCategory.LUXURY, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        2000, 5, "El SAV premium por excelencia. Motor TwinPower Turbo, iDrive 7, carga inalámbrica."), // <-- Faltaba coma

                new Vehicle("Mercedes-Benz", "GLC", 2024, 319900.0, LoanCurrency.SOLES,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/2023_Mercedes-Benz_GLC_300_4Matic%2C_front_9.25.22.jpg/1280px-2023_Mercedes-Benz_GLC_300_4Matic%2C_front_9.25.22.jpg",
                        VehicleCategory.LUXURY, FuelType.GASOLINE, Transmission.AUTOMATIC,
                        2000, 5, "Refinamiento alemán sin igual. Motor 2.0L 258 HP, MBUX con IA, suspensión E-Active Body Control.")
                // <-- El último elemento no lleva coma
        );

        repository.saveAll(vehicles);
        LOGGER.info("Vehicle catalog seeded with {} vehicles.", vehicles.size());
    }
}