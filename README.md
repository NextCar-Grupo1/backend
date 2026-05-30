# NextCar Backend — Documentación Técnica

> API REST para el sistema de crédito vehicular **Compra Inteligente**, desarrollado con Spring Boot siguiendo arquitectura de monolito modular (DDD).

---

## Tabla de Contenidos

1. [Stack Tecnológico](#stack-tecnológico)
2. [Requisitos Previos](#requisitos-previos)
3. [Configuración del Entorno](#configuración-del-entorno)
4. [Ejecución Local](#ejecución-local)
5. [Arquitectura General](#arquitectura-general)
6. [Flujo Completo del Sistema](#flujo-completo-del-sistema)
7. [Módulo IAM](#módulo-iam)
8. [Módulo Verification — DNI](#módulo-verification--dni)
9. [Módulo Catalog — Vehículos](#módulo-catalog--vehículos)
10. [Módulo Customers — Perfil del Cliente](#módulo-customers--perfil-del-cliente)
11. [Módulo Financial — Simulador de Crédito](#módulo-financial--simulador-de-crédito)
12. [Seguridad](#seguridad)
13. [Convenciones del Proyecto](#convenciones-del-proyecto)
14. [Variables de Entorno](#variables-de-entorno)
15. [Swagger / OpenAPI](#swagger--openapi)
16. [Estado del Proyecto y Mejoras](#estado-del-proyecto-y-mejoras)

---

## Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 26 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework base |
| Spring Security | (incluido) | Autenticación y autorización |
| Spring Data JPA | (incluido) | Persistencia |
| PostgreSQL | 14+ | Base de datos |
| JJWT | 0.12.6 | Generación y validación de tokens JWT |
| BCrypt | (via Spring) | Hash de contraseñas |
| Lombok | (incluido) | Reducción de boilerplate |
| springdoc-openapi | 2.8.8 | Documentación Swagger |
| java-dotenv | 5.2.2 | Carga de variables `.env` |
| Maven | 3.9.x | Gestión de dependencias |

---

## Requisitos Previos

- **JDK 26** instalado y `JAVA_HOME` configurado
- **PostgreSQL 14+** corriendo localmente
- Archivo **`.env`** en la raíz del proyecto (ver `.env.example`)

```bash
java -version
psql --version
./mvnw --version
```

---

## Configuración del Entorno

### 1. Crear la base de datos en PostgreSQL

```sql
CREATE DATABASE nextcar_db;
```

### 2. Crear el archivo `.env`

Copia `.env.example` y rellena:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nextcar_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_password

APIPERU_DNI_KEY=tu_token_de_apiperu_dev
GOOGLE_CAPTCHA_KEY=tu_secret_key_de_recaptcha
```

> Obtén tu token en [apiperu.dev](https://apiperu.dev) y tu clave en [Google reCAPTCHA v3](https://www.google.com/recaptcha/admin).

**Para desarrollo sin APIs externas**, agrega al `.env`:
```env
apiperu.enabled=false
recaptcha.secret-key=
```

### 3. Tablas generadas automáticamente

Hibernate crea y actualiza todas las tablas al iniciar (`ddl-auto=update`):

| Entidad Java | Tabla PostgreSQL |
|---|---|
| `User` | `users` |
| `Role` | `roles` |
| (join table) | `user_roles` |
| `Vehicle` | `vehicles` |
| `Customer` | `customers` |
| `LoanSimulation` | `loan_simulations` |
| `PaymentScheduleEntry` | `payment_schedule_entries` |

---

## Ejecución Local

```bash
git clone <url-del-repo>
cd nextcar
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

La API queda en `http://localhost:8091`.

Al iniciar, `ApplicationReadyEventHandler` ejecuta automáticamente:

```
1. Seed de roles      →  ROLE_ADMIN, ROLE_USER
2. Seed de admin      →  admin@nextcar.pe / Admin123!
3. Seed de catálogo   →  16 vehículos con imagen y precio real
```

---

## Arquitectura General

El proyecto sigue una arquitectura de **monolito modular** con principios DDD, organizado en 4 bounded contexts + shared kernel:

```
src/main/java/org/pe/nextcar/
│
├── iam/                              ← Autenticación, usuarios, roles, JWT
│   ├── application/internal/
│   │   ├── commandservices/          ← SignUp, SignIn, SeedAdmin
│   │   ├── queryservices/            ← GetUser, GetRoles
│   │   ├── eventhandlers/            ← ApplicationReadyEventHandler (orquesta seeding)
│   │   └── outboundservices/         ← Contratos: HashingService, TokenService,
│   │                                              CaptchaVerifierService, DniVerificationService
│   ├── domain/model/
│   │   ├── aggregates/               ← User
│   │   ├── commands/                 ← SignUpCommand, SignInCommand, SeedAdminUserCommand
│   │   ├── entities/                 ← Role
│   │   ├── queries/                  ← GetUserByIdQuery, GetUserByEmailQuery
│   │   └── valueobjects/             ← Roles, DniResult, DniVerificationStatus, AuthenticatedUser
│   ├── infrastructure/
│   │   ├── authorization/sfs/        ← BearerAuthorizationRequestFilter, WebSecurityConfiguration
│   │   ├── captcha/google/           ← GoogleRecaptchaVerifierServiceImpl
│   │   ├── dni/apiperu/              ← ApiPeruDniVerificationServiceImpl
│   │   ├── hashing/bcrypt/           ← HashingServiceImpl (BCrypt)
│   │   ├── persistence/jpa/          ← UserRepository, RoleRepository
│   │   └── tokens/jwt/               ← TokenServiceImpl (JJWT)
│   └── interfaces/
│       ├── acl/                      ← IamContextFacade (ACL para otros contextos)
│       └── rest/                     ← AuthenticationController, UsersController,
│                                        RolesController, DniController
│
├── catalog/                          ← Catálogo de vehículos con imagen y cuota estimada
│   ├── application/internal/
│   │   └── queryservices/            ← VehicleQueryServiceImpl
│   ├── domain/model/
│   │   ├── aggregates/               ← Vehicle
│   │   ├── queries/                  ← GetAllVehiclesQuery, GetVehicleByIdQuery
│   │   └── valueobjects/             ← VehicleCategory, FuelType, Transmission
│   ├── domain/services/              ← VehicleQueryService
│   ├── infrastructure/
│   │   └── persistence/jpa/
│   │       ├── repositories/         ← VehicleRepository
│   │       └── seeder/               ← VehicleSeeder (16 autos pre-cargados)
│   └── interfaces/rest/              ← VehicleController, VehicleResource, VehicleAssembler
│
├── customers/                        ← Perfil financiero del cliente
│   ├── application/internal/
│   │   ├── commandservices/          ← CustomerCommandServiceImpl
│   │   └── queryservices/            ← CustomerQueryServiceImpl
│   ├── domain/model/
│   │   ├── aggregates/               ← Customer
│   │   ├── commands/                 ← CreateCustomerProfileCommand, UpdateCustomerProfileCommand
│   │   ├── queries/                  ← GetCustomerByUserIdQuery, GetCustomerByIdQuery
│   │   └── valueobjects/             ← EmploymentType
│   ├── domain/services/              ← CustomerCommandService, CustomerQueryService
│   ├── infrastructure/
│   │   └── persistence/jpa/          ← CustomerRepository
│   └── interfaces/rest/              ← CustomerController, CustomerResource, CustomerAssembler
│
├── financial/                        ← Simulador de crédito vehicular
│   ├── application/internal/
│   │   ├── commandservices/          ← LoanSimulationCommandServiceImpl
│   │   └── queryservices/            ← LoanSimulationQueryServiceImpl
│   ├── domain/model/
│   │   ├── aggregates/               ← LoanSimulation
│   │   ├── commands/                 ← CreateLoanSimulationCommand
│   │   ├── entities/                 ← PaymentScheduleEntry
│   │   ├── queries/                  ← GetLoanSimulationByIdQuery, GetLoanSimulationsByUserIdQuery
│   │   └── valueobjects/             ← LoanCurrency, RateType, CapitalizationFrequency,
│   │                                    GracePeriodType, PaymentMethod, FinancialEntity
│   ├── domain/services/              ← LoanCalculationService, contratos
│   ├── infrastructure/
│   │   └── persistence/jpa/          ← LoanSimulationRepository
│   └── interfaces/rest/              ← LoanSimulationController
│
├── shared/                           ← Shared Kernel
│   ├── domain/model/
│   │   ├── aggregates/               ← AuditableAbstractAggregateRoot
│   │   └── entities/                 ← AuditableModel
│   ├── infrastructure/
│   │   ├── documentation/            ← OpenApiConfiguration
│   │   ├── environment/dotenv/       ← DotEnvConfig
│   │   └── persistence/jpa/          ← SnakeCaseWithPluralizedTablePhysicalNamingStrategy
│   └── interfaces/rest/              ← GlobalExceptionHandler, MessageResource
│
└── NextcarApplication.java
```

### Principio clave: separación por capas

```
interfaces → application → domain ← infrastructure
```

Los bounded contexts se comunican **únicamente** a través de fachadas ACL. Ejemplo: `financial` y `customers` acceden a datos de `iam` solo vía `IamContextFacade`, nunca importando repositorios directamente.

---

## Flujo Completo del Sistema

Este es el flujo que sigue un cliente desde que entra a la plataforma hasta obtener su plan de pagos:

```
CLIENTE                                    BACKEND
  │                                           │
  │ 1. VER CATÁLOGO (sin cuenta)              │
  │ GET /api/v1/vehicles                      │
  │──────────────────────────────────────────►│  catalog: devuelve autos con imagen
  │◄──────────────────────────────────────────│  + cuota estimada precalculada
  │                                           │
  │ 2. REGISTRO                               │
  │ POST /api/v1/authentication/sign-up       │
  │──────────────────────────────────────────►│  iam: valida CAPTCHA + DNI
  │◄──────────────────────────────────────────│  crea User con ROLE_USER → 201
  │                                           │
  │ 3. INICIO DE SESIÓN                       │
  │ POST /api/v1/authentication/sign-in       │
  │──────────────────────────────────────────►│  iam: valida credenciales
  │◄──────────────────────────────────────────│  retorna JWT (7 días) → 200
  │                                           │
  │ 4. COMPLETAR PERFIL FINANCIERO            │
  │ POST /api/v1/customers                    │
  │ Authorization: Bearer <token>             │
  │──────────────────────────────────────────►│  customers: guarda dirección,
  │◄──────────────────────────────────────────│  empleo, ingresos → 201
  │                                           │
  │ 5. ELEGIR AUTO DEL CATÁLOGO               │
  │ GET /api/v1/vehicles/{id}                 │
  │──────────────────────────────────────────►│  catalog: devuelve precio
  │◄──────────────────────────────────────────│  e imageUrl del auto seleccionado
  │                                           │
  │ 6. SIMULAR CRÉDITO                        │
  │ POST /api/v1/loan-simulations             │
  │ { vehiclePrice, método, banco, tasa... }  │
  │──────────────────────────────────────────►│  financial: calcula TEM, cuotas,
  │                                           │  seguros, VAN, TIR, TCEA
  │◄──────────────────────────────────────────│  retorna cronograma completo → 201
  │                                           │
  │ 7. VER HISTORIAL DE SIMULACIONES          │
  │ GET /api/v1/loan-simulations/my           │
  │──────────────────────────────────────────►│  financial: lista todas las
  │◄──────────────────────────────────────────│  simulaciones del usuario
```

---

## Módulo IAM

### Modelo de Dominio

#### `User` — aggregate root

| Campo | Tipo | Restricciones |
|---|---|---|
| `id` | `Long` | PK, auto-generado |
| `email` | `String` | Único, max 254 |
| `password` | `String` | BCrypt hash, max 256 |
| `firstName` | `String` | Requerido, max 60 |
| `lastName` | `String` | Requerido, max 60 |
| `phone` | `String` | Opcional, max 20 |
| `isActive` | `boolean` | Default `true` |
| `emailVerified` | `boolean` | Default `false` |
| `roles` | `Set<Role>` | Many-to-many |
| `createdAt` | `Date` | Auto-auditado |
| `updatedAt` | `Date` | Auto-auditado |

#### `Role` — entity

| Campo | Tipo | Valores |
|---|---|---|
| `id` | `Long` | PK, auto-generado |
| `name` | `Roles` (enum) | `ROLE_ADMIN`, `ROLE_USER` |

### Roles disponibles

| Rol | Descripción |
|---|---|
| `ROLE_USER` | Asignado por defecto en el registro |
| `ROLE_ADMIN` | Seeded automáticamente: `admin@nextcar.pe` / `Admin123!` |

---

### `POST /api/v1/authentication/sign-up` — público

```json
{
  "email": "juan@gmail.com",
  "password": "MiPassword123",
  "firstName": "Juan",
  "lastName": "Pérez García",
  "phone": "999888777",
  "documentNumber": "12345678",
  "captchaToken": "03AGdBq25..."
}
```

> `documentNumber` y `captchaToken` son opcionales. Si se omiten, no se validan.

**Response `201 Created`:**

```json
{
  "id": 1,
  "email": "juan@gmail.com",
  "firstName": "Juan",
  "lastName": "Pérez García",
  "phone": "999888777",
  "roles": ["ROLE_USER"]
}
```

| Código | Causa |
|---|---|
| `201` | Usuario creado |
| `400` | Email duplicado, CAPTCHA inválido o DNI no encontrado |

---

### `POST /api/v1/authentication/sign-in` — público

```json
{
  "email": "juan@gmail.com",
  "password": "MiPassword123"
}
```

**Response `200 OK`:**

```json
{
  "id": 1,
  "email": "juan@gmail.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_USER"]
}
```

> ⚠️ Guarda el `token`. Se usa en el header `Authorization: Bearer <token>`.

| Código | Causa |
|---|---|
| `200` | Autenticado |
| `400` | Contraseña incorrecta |
| `404` | Usuario no encontrado |

---

### Endpoints de Usuarios — requieren JWT

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/users` | Lista todos los usuarios |
| `GET` | `/api/v1/users/{id}` | Obtiene usuario por ID |
| `GET` | `/api/v1/roles` | Lista roles disponibles |

---

### Flujo de Autenticación JWT

```
POST /sign-up
  └─► Valida CAPTCHA → Verifica DNI → Verifica email único
      → BCrypt hash → Guarda User → 201

POST /sign-in
  └─► Busca User → Compara BCrypt → Genera JWT (HS256, 7 días) → 200

Requests protegidas:
  └─► BearerAuthorizationRequestFilter
        → Valida firma JWT → Extrae email → Carga UserDetails
        → Setea SecurityContextHolder → Controller
```

**Estructura del JWT:**
```
Header  : { "alg": "HS256", "typ": "JWT" }
Payload : { "sub": "juan@gmail.com", "iat": 1700000000, "exp": 1700604800 }
```

---

## Módulo Verification — DNI

### `GET /api/v1/dni/{dni}` — público

Consulta RENIEC vía apiperu.dev.

```
GET /api/v1/dni/12345678
```

**Response `200 OK`:**

```json
{
  "dni": "12345678",
  "nombres": "JUAN CARLOS",
  "apellidoPaterno": "PÉREZ",
  "apellidoMaterno": "GARCÍA"
}
```

| Código | Causa |
|---|---|
| `200` | DNI encontrado |
| `404` | DNI no encontrado o servicio no disponible |

---

## Módulo Catalog — Vehículos

El catálogo es **público** — los usuarios pueden ver los autos antes de registrarse. Al iniciar, el `VehicleSeeder` carga **16 vehículos** con imágenes reales (Wikimedia Commons), precios en soles y una cuota estimada precalculada para mostrar en la card del frontend.

### Vehículos pre-cargados

| Marca | Modelo | Año | Precio (S/) | Categoría |
|---|---|---|---|---|
| Toyota | Corolla | 2024 | 89,900 | SEDAN |
| Toyota | Yaris | 2024 | 69,900 | SEDAN |
| Toyota | RAV4 | 2024 | 145,900 | SUV |
| Toyota | Hilux | 2024 | 159,900 | PICKUP |
| Hyundai | Tucson | 2024 | 125,900 | SUV |
| Hyundai | Elantra | 2024 | 82,900 | SEDAN |
| Hyundai | Grand i10 | 2024 | 55,900 | HATCHBACK |
| Kia | Sportage | 2024 | 118,900 | SUV |
| Kia | Picanto | 2024 | 49,900 | HATCHBACK |
| Chevrolet | Tracker | 2024 | 98,900 | SUV |
| Renault | Duster | 2024 | 79,900 | SUV |
| Renault | Logan | 2024 | 59,900 | SEDAN |
| Nissan | Sentra | 2024 | 85,900 | SEDAN |
| Mazda | CX-30 | 2024 | 109,900 | SUV |
| Ford | Ranger | 2024 | 149,900 | PICKUP |
| BMW | X3 | 2024 | 289,900 | LUXURY |
| Mercedes-Benz | GLC | 2024 | 319,900 | LUXURY |

> La cuota estimada `estimatedMonthlyPayment` se calcula automáticamente con TEA 12.5%, 20% cuota inicial, 36 meses. Es referencial para mostrar en la card — el usuario puede personalizar todos los parámetros al simular.

---

### `GET /api/v1/vehicles` — público

Lista todos los vehículos disponibles. Filtro opcional por categoría.

```
GET /api/v1/vehicles
GET /api/v1/vehicles?category=SUV
GET /api/v1/vehicles?category=SEDAN
```

**Response `200 OK`:**

```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2024,
    "price": 89900.0,
    "currency": "SOLES",
    "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/...",
    "category": "SEDAN",
    "categoryDisplayName": "Sedán",
    "fuelType": "GASOLINE",
    "fuelTypeDisplayName": "Gasolina",
    "transmission": "CVT",
    "transmissionDisplayName": "CVT",
    "engineCC": 1800,
    "seatingCapacity": 5,
    "description": "El sedán más vendido del Perú...",
    "estimatedMonthlyPayment": 2447.26
  }
]
```

> **Cómo usar `estimatedMonthlyPayment` en el frontend:**
> Muestra este valor en la card del auto como "Desde S/ 2,447/mes*" con un asterisco que aclare que es referencial. Al hacer clic en el auto, pre-llena el simulador con el `price` del vehículo.

---

### `GET /api/v1/vehicles/{id}` — público

Obtiene el detalle completo de un vehículo. Usa el campo `price` y `currency` para pre-llenar el formulario de simulación.

```
GET /api/v1/vehicles/1
```

### Valores del campo `category`

| Valor | Descripción |
|---|---|
| `SEDAN` | Sedán |
| `SUV` | SUV / Camioneta |
| `HATCHBACK` | Hatchback |
| `PICKUP` | Pickup |
| `VAN` | Van / Minivan |
| `LUXURY` | Lujo |

### Todos los endpoints del módulo Catalog

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/vehicles` | ❌ Público | Catálogo completo, filtrable por categoría |
| `GET` | `/api/v1/vehicles?category={cat}` | ❌ Público | Filtrado por categoría |
| `GET` | `/api/v1/vehicles/{id}` | ❌ Público | Detalle de un vehículo |

---

## Módulo Customers — Perfil del Cliente

Complementa al `User` de IAM con los datos financieros necesarios para la evaluación crediticia: dirección, tipo de empleo e ingresos. Se crea una sola vez después del registro y puede actualizarse.

### `Customer` — aggregate root

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK, auto-generado |
| `userId` | `Long` | Referencia al User de IAM (ACL) |
| `documentNumber` | `String` | DNI (8 dígitos) |
| `address` | `String` | Dirección completa |
| `district` | `String` | Distrito |
| `city` | `String` | Ciudad |
| `employmentType` | `EmploymentType` | Tipo de empleo |
| `occupation` | `String` | Cargo / Profesión |
| `employer` | `String` | Nombre de la empresa |
| `monthlyIncome` | `double` | Ingreso mensual en soles |
| `profileComplete` | `boolean` | `true` cuando el perfil está completo |

---

### `GET /api/v1/customers/me` — requiere JWT

Obtiene el perfil del usuario autenticado.

**Response `200 OK`:**

```json
{
  "id": 1,
  "userId": 1,
  "documentNumber": "12345678",
  "address": "Av. Javier Prado 1234",
  "district": "San Isidro",
  "city": "Lima",
  "employmentType": "DEPENDENT",
  "employmentTypeDisplayName": "Dependiente",
  "occupation": "Ingeniero de Software",
  "employer": "Tech Corp SAC",
  "monthlyIncome": 5500.0,
  "profileComplete": true
}
```

| Código | Causa |
|---|---|
| `200` | Perfil encontrado |
| `404` | El usuario no tiene perfil aún |

---

### `POST /api/v1/customers` — requiere JWT

Crea el perfil financiero del cliente. Se ejecuta **una sola vez** después del registro.

```json
{
  "documentNumber": "12345678",
  "address": "Av. Javier Prado 1234",
  "district": "San Isidro",
  "city": "Lima",
  "employmentType": "DEPENDENT",
  "occupation": "Ingeniero de Software",
  "employer": "Tech Corp SAC",
  "monthlyIncome": 5500.00
}
```

**Response `201 Created`:**

```json
{
  "id": 1,
  "userId": 1,
  "documentNumber": "12345678",
  "address": "Av. Javier Prado 1234",
  "district": "San Isidro",
  "city": "Lima",
  "employmentType": "DEPENDENT",
  "employmentTypeDisplayName": "Dependiente",
  "occupation": "Ingeniero de Software",
  "employer": "Tech Corp SAC",
  "monthlyIncome": 5500.0,
  "profileComplete": true
}
```

| Código | Causa |
|---|---|
| `201` | Perfil creado |
| `400` | Usuario ya tiene perfil o datos inválidos |

---

### `PUT /api/v1/customers/{id}` — requiere JWT

Actualiza el perfil existente.

```json
{
  "address": "Calle Los Pinos 567",
  "district": "Miraflores",
  "city": "Lima",
  "employmentType": "INDEPENDENT",
  "occupation": "Consultor",
  "employer": "",
  "monthlyIncome": 7200.00
}
```

### Valores de `employmentType`

| Valor | Descripción |
|---|---|
| `DEPENDENT` | Dependiente (relación de trabajo con empresa) |
| `INDEPENDENT` | Independiente / Freelance |
| `BUSINESS_OWNER` | Empresario / Dueño de negocio |
| `RETIRED` | Jubilado |

### Todos los endpoints del módulo Customers

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/customers/me` | ✅ JWT | Perfil del usuario autenticado |
| `POST` | `/api/v1/customers` | ✅ JWT | Crear perfil (una sola vez) |
| `PUT` | `/api/v1/customers/{id}` | ✅ JWT | Actualizar perfil |

---

## Módulo Financial — Simulador de Crédito

### Paso a paso: cómo simular un crédito

```
1. GET  /api/v1/vehicles                         (sin token)
        → Elige el auto y toma nota de su precio

2. POST /api/v1/authentication/sign-in           (sin token)
        → Obtén el token JWT

3. GET  /api/v1/loan-simulations/financial-entities  (con token)
        → Consulta los bancos y sus parámetros por defecto

4. POST /api/v1/loan-simulations                 (con token)
        → Envía vehiclePrice + parámetros del crédito

5. GET  /api/v1/loan-simulations/my              (con token)
        → Historial de todas tus simulaciones
```

---

### Ejemplo 1: Método Francés (caso base)

**Escenario:** Toyota Corolla S/ 89,900 | Cuota inicial 20% | 36 meses | TEA 12.5% | BCP

```
POST /api/v1/loan-simulations
Authorization: Bearer <token>
```

```json
{
  "currency": "SOLES",
  "vehiclePrice": 89900,
  "initialFeeRate": 0.20,
  "termMonths": 36,
  "startDate": "2024-01-15",
  "paymentMethod": "FRENCH",
  "rateType": "TEA",
  "rateValue": 0.125,
  "capitalizationFrequency": "MONTHLY",
  "gracePeriodType": "NONE",
  "gracePeriodMonths": 0,
  "financialEntity": "BCP",
  "desgravamenRate": 0.00050,
  "vehicleInsuranceMonthly": 150.00,
  "portesMonthly": 10.00
}
```

**Response `201 Created`:**

```json
{
  "id": 1,
  "currency": "SOLES",
  "vehiclePrice": 89900.0,
  "initialFee": 17980.0,
  "principal": 71920.0,
  "termMonths": 36,
  "paymentMethod": "FRENCH",
  "financialEntityName": "Banco de Crédito del Perú (BCP)",
  "monthlyEffectiveRate": 0.009864,
  "baseInstallment": 2383.66,
  "totalMonthlyInstallment": 2583.66,
  "monthlyIrr": 0.010412,
  "tcea": 0.1323,
  "npv": 0.0,
  "totalInterestPaid": 13871.76,
  "totalInsurancePaid": 7008.0,
  "totalAmortization": 71920.0,
  "totalPaid": 93071.76,
  "schedule": [
    {
      "periodNumber": 1,
      "paymentDate": "2024-02-15",
      "initialBalance": 71920.0,
      "amortization": 1674.74,
      "interest": 708.92,
      "desgravamenInsurance": 35.96,
      "vehicleInsurance": 150.0,
      "portes": 10.0,
      "totalInstallment": 2579.62,
      "finalBalance": 70245.26,
      "gracePeriodType": "NONE",
      "balloonPeriod": false
    }
  ]
}
```

> - `baseInstallment` = cuota pura (capital + interés), sin seguros ni portes.
> - `totalMonthlyInstallment` = cuota real mensual del cliente.
> - `tcea` = Tasa de Costo Efectivo Anual exigida por SBS.
> - `npv` ≈ 0 porque la tasa de descuento usada es la misma TEM del préstamo.

---

### Ejemplo 2: Compra Inteligente

BCP retiene el 40% del precio como valor residual (cuota balón en el periodo 36).

```json
{
  "currency": "SOLES",
  "vehiclePrice": 89900,
  "initialFeeRate": 0.20,
  "termMonths": 36,
  "startDate": "2024-01-15",
  "paymentMethod": "SMART_PURCHASE",
  "rateType": "TEA",
  "rateValue": 0.125,
  "capitalizationFrequency": "MONTHLY",
  "gracePeriodType": "NONE",
  "gracePeriodMonths": 0,
  "financialEntity": "BCP",
  "desgravamenRate": 0.00050,
  "vehicleInsuranceMonthly": 150.00,
  "portesMonthly": 10.00
}
```

> Cuotas 1–35 son menores que en Método Francés. La cuota 36 incluye el balón (`balloonPeriod: true`).
> Fórmula: `C = (P − VR·(1+i)^−n) · i / (1 − (1+i)^−n)` | VR = 89,900 × 0.40 = 35,960

---

### Ejemplo 3: Gracia Parcial

El cliente paga solo intereses los primeros 3 meses.

```json
{
  "currency": "SOLES",
  "vehiclePrice": 89900,
  "initialFeeRate": 0.20,
  "termMonths": 36,
  "startDate": "2024-01-15",
  "paymentMethod": "FRENCH",
  "rateType": "TEA",
  "rateValue": 0.125,
  "capitalizationFrequency": "MONTHLY",
  "gracePeriodType": "PARTIAL",
  "gracePeriodMonths": 3,
  "financialEntity": "BCP",
  "desgravamenRate": 0.00050,
  "vehicleInsuranceMonthly": 150.00,
  "portesMonthly": 10.00
}
```

| Período | `amortization` | `gracePeriodType` |
|---|---|---|
| 1–3 | `0.0` | `"PARTIAL"` |
| 4–36 | `> 0` | `"NONE"` |

---

### Ejemplo 4: Gracia Total

El cliente no paga nada los primeros 2 meses. El interés capitaliza al saldo.

```json
{
  "gracePeriodType": "TOTAL",
  "gracePeriodMonths": 2
}
```

| Período | `totalInstallment` | `gracePeriodType` |
|---|---|---|
| 1–2 | `0.0` | `"TOTAL"` |
| 3–36 | `> 0` | `"NONE"` |

---

### Ejemplo 5: TNA en Dólares

Vehículo $ 22,000 | BBVA | TNA 12% capitalización diaria | 48 meses

```json
{
  "currency": "DOLLARS",
  "vehiclePrice": 22000,
  "initialFeeRate": 0.25,
  "termMonths": 48,
  "startDate": "2024-01-15",
  "paymentMethod": "FRENCH",
  "rateType": "TNA",
  "rateValue": 0.12,
  "capitalizationFrequency": "DAILY",
  "gracePeriodType": "NONE",
  "gracePeriodMonths": 0,
  "financialEntity": "BBVA",
  "desgravamenRate": 0.00045,
  "vehicleInsuranceMonthly": 45.00,
  "portesMonthly": 8.00
}
```

> Conversión automática: `TEM = (1 + 0.12/360)^(360/12) − 1 ≈ 1.0052%`

---

### Entidades Financieras

`GET /api/v1/loan-simulations/financial-entities` — requiere token.

```json
[
  { "name": "BCP",         "displayName": "Banco de Crédito del Perú (BCP)", "smartPurchaseResidualRate": 0.40, "defaultDesgravamenRate": 0.0005,  "defaultVehicleInsurance": 150.0, "defaultPortes": 10.0, "maxGracePeriodMonths": 6 },
  { "name": "BBVA",        "displayName": "BBVA Continental",                "smartPurchaseResidualRate": 0.35, "defaultDesgravamenRate": 0.00045, "defaultVehicleInsurance": 140.0, "defaultPortes": 8.0,  "maxGracePeriodMonths": 6 },
  { "name": "SCOTIABANK",  "displayName": "Scotiabank Perú",                 "smartPurchaseResidualRate": 0.30, "defaultDesgravamenRate": 0.0005,  "defaultVehicleInsurance": 130.0, "defaultPortes": 12.0, "maxGracePeriodMonths": 4 },
  { "name": "INTERBANK",   "displayName": "Interbank",                       "smartPurchaseResidualRate": 0.35, "defaultDesgravamenRate": 0.00048, "defaultVehicleInsurance": 145.0, "defaultPortes": 9.0,  "maxGracePeriodMonths": 6 },
  { "name": "MIBANCO",     "displayName": "MiBanco",                         "smartPurchaseResidualRate": 0.25, "defaultDesgravamenRate": 0.00055, "defaultVehicleInsurance": 120.0, "defaultPortes": 8.0,  "maxGracePeriodMonths": 3 },
  { "name": "BANBIF",      "displayName": "BanBIF",                          "smartPurchaseResidualRate": 0.30, "defaultDesgravamenRate": 0.0005,  "defaultVehicleInsurance": 135.0, "defaultPortes": 10.0, "maxGracePeriodMonths": 4 },
  { "name": "PICHINCHA",   "displayName": "Banco Pichincha",                 "smartPurchaseResidualRate": 0.30, "defaultDesgravamenRate": 0.0005,  "defaultVehicleInsurance": 130.0, "defaultPortes": 8.0,  "maxGracePeriodMonths": 4 },
  { "name": "CREDISCOTIA", "displayName": "CrediScotia Financiera",          "smartPurchaseResidualRate": 0.25, "defaultDesgravamenRate": 0.00055, "defaultVehicleInsurance": 125.0, "defaultPortes": 10.0, "maxGracePeriodMonths": 3 }
]
```

---

### Referencia de campos

| Campo | Valores válidos | Restricción |
|---|---|---|
| `currency` | `SOLES`, `DOLLARS` | — |
| `paymentMethod` | `FRENCH`, `SMART_PURCHASE` | — |
| `rateType` | `TEA`, `TNA` | — |
| `capitalizationFrequency` | `DAILY`, `MONTHLY`, `BIMONTHLY`, `QUARTERLY`, `SEMI_ANNUAL`, `ANNUAL` | Solo aplica con TNA |
| `gracePeriodType` | `NONE`, `PARTIAL`, `TOTAL` | — |
| `initialFeeRate` | Decimal | Entre `0.20` y `0.80` |
| `termMonths` | Entero | Entre `6` y `84` |
| `gracePeriodMonths` | Entero | ≥ `0`, < `termMonths`, ≤ `maxGracePeriodMonths` del banco |
| `rateValue` | Decimal | Positivo (ej. `0.125` = 12.5%) |
| `desgravamenRate` | Decimal | Positivo (ej. `0.00050` = 0.05% mensual) |

### Todos los endpoints del módulo Financial

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/v1/loan-simulations` | ✅ JWT | Calcular y guardar simulación |
| `GET` | `/api/v1/loan-simulations/{id}` | ✅ JWT | Obtener por ID |
| `GET` | `/api/v1/loan-simulations/my` | ✅ JWT | Historial del usuario |
| `GET` | `/api/v1/loan-simulations/financial-entities` | ✅ JWT | Lista de bancos |

---

## Seguridad

### Rutas públicas (sin token)

```
POST /api/v1/authentication/sign-in
POST /api/v1/authentication/sign-up
GET  /api/v1/dni/{dni}
GET  /api/v1/vehicles/**
GET  /v3/api-docs/**
GET  /swagger-ui/**
GET  /swagger-ui.html
```

### Rutas protegidas — requieren `Authorization: Bearer <token>`

```
/api/v1/users/**
/api/v1/roles/**
/api/v1/customers/**
/api/v1/loan-simulations/**
```

### Pipeline de seguridad

```
Request
  └─► BearerAuthorizationRequestFilter  ← valida firma JWT
        └─► SecurityContextHolder         ← setea usuario autenticado
              └─► Controller              ← procesa la request
```

Si el token es inválido: `UnauthorizedRequestHandlerEntryPoint` → `401 Unauthorized`.

### Hashing de contraseñas

BCrypt 10 rounds. Las contraseñas nunca se almacenan en texto plano.

---

## Convenciones del Proyecto

### Naming de base de datos

`SnakeCaseWithPluralizedTablePhysicalNamingStrategy` transforma automáticamente:
- `LoanSimulation` → `loan_simulations`
- `initialFeeRate` → `initial_fee_rate`
- `PaymentScheduleEntry` → `payment_schedule_entries`

### Patrón CQRS lite

```
CreateLoanSimulationCommand  →  LoanSimulationCommandService.handle(...)
GetLoanSimulationByIdQuery   →  LoanSimulationQueryService.handle(...)
CreateCustomerProfileCommand →  CustomerCommandService.handle(...)
```

### Assemblers

```
Vehicle  ──► VehicleAssembler  ──► VehicleResource
Customer ──► CustomerAssembler ──► CustomerResource
```

### Anti-Corruption Layer

```java
// financial y customers acceden a iam así (correcto):
Long userId = iamFacade.fetchUserIdByEmail(currentUser.getEmail()).orElseThrow(...);

// Nunca así (incorrecto — rompe el aislamiento):
userRepository.findByEmail(email);
```

---

## Variables de Entorno

| Variable | Descripción |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de base de datos |
| `APIPERU_DNI_KEY` | Token de apiperu.dev para RENIEC |
| `GOOGLE_CAPTCHA_KEY` | Secret key de Google reCAPTCHA v3 |

---

## Swagger / OpenAPI

```
http://localhost:8091/swagger-ui/index.html
```

Click en **Authorize** e ingresa:
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

JSON de la especificación:
```
http://localhost:8091/v3/api-docs
```

---

## Estado del Proyecto y Mejoras

### Estado actual

| Módulo | Estado | Descripción |
|---|---|---|
| `iam` | ✅ Completo | Auth, usuarios, roles, JWT, BCrypt, CAPTCHA, seeding |
| `verification` | ✅ Completo | Consulta DNI vía RENIEC (apiperu.dev) |
| `catalog` | ✅ Completo | 16 vehículos pre-cargados con imagen, precio y cuota estimada |
| `customers` | ✅ Completo | Perfil financiero: dirección, empleo, ingresos |
| `financial` | ✅ Completo | Método Francés, Compra Inteligente, gracia parcial/total, VAN, TIR, TCEA |

### Mejoras propuestas

Las siguientes funcionalidades elevarían el sistema a un nivel de producción real y darían ventaja en la presentación:

#### Funcionales (alto impacto)

| Mejora | Descripción |
|---|---|
| **Exportar PDF del cronograma** | `GET /api/v1/loan-simulations/{id}/pdf` — genera el plan de pagos en PDF usando iText o JasperReports |
| **Comparador de bancos** | `POST /api/v1/loan-simulations/compare` — recibe un vehículo y parámetros base, devuelve la simulación para todos los bancos en paralelo |
| **Búsqueda y filtros en catálogo** | `GET /api/v1/vehicles?brand=Toyota&minPrice=50000&maxPrice=100000&fuelType=HYBRID` |
| **Paginación del catálogo** | `GET /api/v1/vehicles?page=0&size=6` — necesario para catálogos grandes |
| **Favoritos del usuario** | `POST /api/v1/vehicles/{id}/favorite` — guardar autos para comparar después |

#### Técnicas (buenas prácticas)

| Mejora | Descripción |
|---|---|
| **Pruebas unitarias** | `JUnit 5` + `Mockito` para `LoanCalculationService` — verifica fórmulas con datos conocidos |
| **Rate limiting** | Limitar requests por IP en sign-up y sign-in para evitar ataques de fuerza bruta |
| **Soft delete** | Agregar `deletedAt` en lugar de borrar físicamente simulaciones y clientes |
| **Refresh token** | Emitir un refresh token junto con el JWT para renovar sesiones sin pedir credenciales |
| **Manejo global de 404** | Agregar `@ExceptionHandler(NoSuchElementException.class)` en `GlobalExceptionHandler` |
| **Docker Compose** | `docker-compose.yml` con PostgreSQL + backend para facilitar el despliegue del equipo |
| **Imágenes propias** | Subir las imágenes de los autos a Cloudinary o S3 para no depender de Wikimedia |

---

*NextCar Backend — Finanzas e Ingeniería Económica · UPC*
