# NextCar Backend — Documentación Técnica

> API REST para el sistema de crédito vehicular **Compra Inteligente**, desarrollado con Spring Boot siguiendo arquitectura de monolito modular (DDD).

---

## Tabla de Contenidos

1. [Stack Tecnológico](#stack-tecnológico)
2. [Requisitos Previos](#requisitos-previos)
3. [Configuración del Entorno](#configuración-del-entorno)
4. [Ejecución Local](#ejecución-local)
5. [Arquitectura General](#arquitectura-general)
6. [Módulo IAM — Identity & Access Management](#módulo-iam)
   - [Modelo de Dominio](#modelo-de-dominio)
   - [Roles disponibles](#roles-disponibles)
   - [Endpoints de Autenticación](#endpoints-de-autenticación)
   - [Endpoints de Usuarios](#endpoints-de-usuarios)
   - [Endpoints de Roles](#endpoints-de-roles)
   - [Flujo de Autenticación JWT](#flujo-de-autenticación-jwt)
7. [Seguridad](#seguridad)
8. [Convenciones del Proyecto](#convenciones-del-proyecto)
9. [Variables de Entorno](#variables-de-entorno)
10. [Swagger / OpenAPI](#swagger--openapi)

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
| Maven | 3.9.x | Gestión de dependencias |

---

## Requisitos Previos

- **JDK 26** instalado y `JAVA_HOME` configurado
- **PostgreSQL 14+** corriendo localmente o en Docker
- **Maven 3.9+** (o usar el wrapper incluido `./mvnw`)

Para verificar tu instalación:

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
CREATE USER postgres WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE nextcar_db TO postgres;
```

### 2. Configurar `application.properties`

El archivo se encuentra en `src/main/resources/application.properties`. Los valores por defecto son:

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/nextcar_db
spring.datasource.username=postgres
spring.datasource.password=1234

# JWT — cambiar en producción
authorization.jwt.secret=WriteHereYourSecretStringForTokenSigningCredentials
authorization.jwt.expiration.days=7

# Puerto del servidor
server.port=8091
```

> Para sobreescribir sin tocar el archivo, usa variables de entorno (ver sección [Variables de Entorno](#variables-de-entorno)).

### 3. Esquema de base de datos

El proyecto usa `spring.jpa.hibernate.ddl-auto=update`, así que Hibernate crea y actualiza las tablas automáticamente al iniciar. No se necesita ejecutar scripts SQL manualmente.

Las tablas generadas siguen la convención **snake_case pluralizado** gracias a `SnakeCaseWithPluralizedTablePhysicalNamingStrategy`:

| Entidad Java | Tabla PostgreSQL |
|---|---|
| `User` | `users` |
| `Role` | `roles` |
| (join table) | `user_roles` |

---

## Ejecución Local

```bash
# Clonar el repositorio
git clone <url-del-repo>
cd nextcar

# Compilar sin tests
./mvnw clean package -DskipTests

# Ejecutar
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8091`.

Al iniciar, el sistema ejecuta automáticamente el **seeding de roles** (ver `ApplicationReadyEventHandler`), creando en la base de datos los roles `ROLE_ADMIN` y `ROLE_USER` si no existen.

---

## Arquitectura General

El proyecto sigue una arquitectura de **monolito modular** con principios DDD, organizando el código en bounded contexts independientes:

```
src/main/java/org/pe/nextcar/
│
├── iam/                          ← Bounded context: autenticación y usuarios
│   ├── application/
│   │   └── internal/
│   │       ├── commandservices/  ← Lógica de escritura (SignUp, SignIn)
│   │       ├── queryservices/    ← Lógica de lectura (GetUser, GetRoles)
│   │       ├── eventhandlers/    ← Eventos de aplicación (seed roles)
│   │       └── outboundservices/ ← Contratos hacia infraestructura
│   ├── domain/
│   │   └── model/
│   │       ├── aggregates/       ← User (raíz de agregado)
│   │       ├── commands/         ← SignUpCommand, SignInCommand
│   │       ├── entities/         ← Role
│   │       ├── queries/          ← GetUserByIdQuery, etc.
│   │       ├── valueobjects/     ← Roles (enum)
│   │       └── services/         ← Contratos de dominio
│   ├── infrastructure/
│   │   ├── authorization/sfs/    ← Filtros y pipeline de Spring Security
│   │   ├── hashing/bcrypt/       ← Implementación BCrypt
│   │   ├── persistence/jpa/      ← Repositorios JPA
│   │   └── tokens/jwt/           ← Implementación JWT
│   └── interfaces/
│       ├── acl/                  ← IamContextFacade (Anti-Corruption Layer)
│       └── rest/                 ← Controllers, Resources, Assemblers
│
├── shared/                       ← Shared Kernel
│   ├── domain/model/
│   │   ├── aggregates/           ← AuditableAbstractAggregateRoot
│   │   └── entities/             ← AuditableModel
│   ├── infrastructure/
│   │   ├── documentation/        ← OpenApiConfiguration
│   │   └── persistence/jpa/      ← SnakeCaseNamingStrategy
│   └── interfaces/rest/
│       └── resources/            ← MessageResource
│
└── NextcarApplication.java
```

### Principio clave: separación por capas dentro de cada bounded context

```
interfaces → application → domain ← infrastructure
```

- **`domain`** no depende de nada externo.
- **`application`** coordina dominio e infraestructura a través de contratos (interfaces).
- **`infrastructure`** implementa los contratos definidos en `application`.
- **`interfaces`** expone el contexto hacia afuera (REST, ACL hacia otros contextos).

---

## Módulo IAM

### Modelo de Dominio

#### `User` (aggregate root)

Campos principales:

| Campo | Tipo | Restricciones |
|---|---|---|
| `id` | `Long` | PK, auto-generado |
| `email` | `String` | Único, max 254 chars |
| `password` | `String` | BCrypt hash, max 256 |
| `firstName` | `String` | Requerido, max 60 |
| `lastName` | `String` | Requerido, max 60 |
| `phone` | `String` | Opcional, max 20 |
| `isActive` | `boolean` | Default `true` |
| `emailVerified` | `boolean` | Default `false` |
| `roles` | `Set<Role>` | Muchos a muchos |
| `createdAt` | `Date` | Auto-auditado |
| `updatedAt` | `Date` | Auto-auditado |

#### `Role` (entity)

| Campo | Tipo | Valores |
|---|---|---|
| `id` | `Long` | PK, auto-generado |
| `name` | `Roles` (enum) | `ROLE_ADMIN`, `ROLE_USER` |

### Roles Disponibles

| Rol | Descripción |
|---|---|
| `ROLE_USER` | Rol por defecto asignado en registro. Acceso a funciones de cliente. |
| `ROLE_ADMIN` | Acceso administrativo completo. |

Si el campo `roles` viene vacío en el registro, se asigna `ROLE_USER` automáticamente.

---

### Endpoints de Autenticación

Base path: `/api/v1/authentication`

Estos endpoints son **públicos** (no requieren token).

---

#### `POST /api/v1/authentication/sign-up`

Registra un nuevo usuario en el sistema.

**Request body:**

```json
{
  "email": "juan.perez@example.com",
  "password": "MiPassword123",
  "firstName": "Juan",
  "lastName": "Pérez García",
  "phone": "999888777",
  "roles": []
}
```

> Dejar `roles` como array vacío para asignar `ROLE_USER` por defecto.  
> Para crear un admin durante desarrollo: `"roles": ["ROLE_ADMIN"]`

**Response `201 Created`:**

```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "firstName": "Juan",
  "lastName": "Pérez García",
  "phone": "999888777",
  "roles": ["ROLE_USER"]
}
```

**Errores posibles:**

| Código | Causa |
|---|---|
| `400 Bad Request` | Email ya registrado o datos inválidos |

---

#### `POST /api/v1/authentication/sign-in`

Autentica un usuario y retorna el token JWT.

**Request body:**

```json
{
  "email": "juan.perez@example.com",
  "password": "MiPassword123"
}
```

**Response `200 OK`:**

```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_USER"]
}
```

**Errores posibles:**

| Código | Causa |
|---|---|
| `404 Not Found` | Usuario no encontrado |
| `400 Bad Request` | Contraseña incorrecta |

---

### Endpoints de Usuarios

Base path: `/api/v1/users`

Todos estos endpoints **requieren token JWT** en el header.

**Header requerido:**
```
Authorization: Bearer <token>
```

---

#### `GET /api/v1/users`

Retorna la lista de todos los usuarios registrados.

**Response `200 OK`:**

```json
[
  {
    "id": 1,
    "email": "juan.perez@example.com",
    "firstName": "Juan",
    "lastName": "Pérez García",
    "phone": "999888777",
    "roles": ["ROLE_USER"]
  }
]
```

---

#### `GET /api/v1/users/{userId}`

Retorna un usuario por su ID.

**Parámetro de ruta:** `userId` — Long

**Response `200 OK`:**

```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "firstName": "Juan",
  "lastName": "Pérez García",
  "phone": "999888777",
  "roles": ["ROLE_USER"]
}
```

**Errores posibles:**

| Código | Causa |
|---|---|
| `404 Not Found` | Usuario no encontrado |
| `401 Unauthorized` | Token ausente o inválido |

---

### Endpoints de Roles

Base path: `/api/v1/roles`

Requiere token JWT.

---

#### `GET /api/v1/roles`

Retorna todos los roles disponibles en el sistema.

**Response `200 OK`:**

```json
[
  { "id": 1, "name": "ROLE_ADMIN" },
  { "id": 2, "name": "ROLE_USER" }
]
```

---

### Flujo de Autenticación JWT

```
Cliente                          Backend
  │                                 │
  │  POST /sign-up (datos)          │
  │────────────────────────────────►│
  │                                 │  1. Valida datos
  │                                 │  2. Hashea password con BCrypt
  │                                 │  3. Guarda User en BD
  │  201 Created (UserResource)     │
  │◄────────────────────────────────│
  │                                 │
  │  POST /sign-in (email+password) │
  │────────────────────────────────►│
  │                                 │  1. Busca User por email
  │                                 │  2. Valida password vs hash BCrypt
  │                                 │  3. Genera JWT firmado (HS256, 7 días)
  │  200 OK (token + datos)         │
  │◄────────────────────────────────│
  │                                 │
  │  GET /api/v1/users              │
  │  Authorization: Bearer <token>  │
  │────────────────────────────────►│
  │                                 │  1. BearerAuthorizationRequestFilter
  │                                 │  2. Extrae y valida JWT
  │                                 │  3. Carga UserDetails por email
  │                                 │  4. Setea SecurityContext
  │  200 OK                         │
  │◄────────────────────────────────│
```

**Estructura del JWT:**

```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "email@ejemplo.com", "iat": <timestamp>, "exp": <timestamp+7días> }
Signature: HMACSHA256(base64(header) + "." + base64(payload), secret)
```

El token expira a los **7 días** (configurable con `authorization.jwt.expiration.days`).

---

## Seguridad

### Rutas públicas (sin token)

```
POST /api/v1/authentication/sign-in
POST /api/v1/authentication/sign-up
GET  /v3/api-docs/**
GET  /swagger-ui/**
GET  /swagger-ui.html
```

### Rutas protegidas (requieren `Authorization: Bearer <token>`)

Todas las demás rutas bajo `/api/v1/**`.

### Pipeline de seguridad

```
Request
  └─► BearerAuthorizationRequestFilter   ← extrae y valida el JWT
        └─► SecurityContextHolder          ← setea el usuario autenticado
              └─► Controller               ← procesa la request
```

Si el token es inválido o está ausente, `UnauthorizedRequestHandlerEntryPoint` responde con `401 Unauthorized`.

### Hashing de contraseñas

Las contraseñas nunca se almacenan en texto plano. Se utiliza **BCrypt** con su factor de costo por defecto (10 rounds). La verificación se hace con `BCryptPasswordEncoder.matches()`.

### CORS

Configurado para aceptar cualquier origen (`*`) en métodos `GET`, `POST`, `PUT`, `DELETE`. Ajustar a dominios específicos para producción en `WebSecurityConfiguration`.

---

## Convenciones del Proyecto

### Naming de base de datos

La clase `SnakeCaseWithPluralizedTablePhysicalNamingStrategy` transforma automáticamente:

- Nombres de clase `CamelCase` → tablas `snake_case` pluralizadas (`User` → `users`)
- Nombres de campo `camelCase` → columnas `snake_case` (`firstName` → `first_name`)

### Patrón Command / Query (CQRS lite)

Cada operación se modela como un objeto de comando o query:

```
SignUpCommand     → UserCommandService.handle(SignUpCommand)
SignInCommand     → UserCommandService.handle(SignInCommand)
GetUserByIdQuery  → UserQueryService.handle(GetUserByIdQuery)
```

### Assemblers

La transformación entre entidades de dominio y recursos REST se hace siempre a través de assemblers, nunca directamente en el controller:

```
User (entity) ──► UserResourceFromEntityAssembler ──► UserResource (record)
```

### Anti-Corruption Layer (ACL)

Si otro bounded context (por ejemplo `financial`) necesita datos de un usuario, lo hace a través de `IamContextFacade`, nunca importando repositorios o entidades de `iam` directamente.

---

## Variables de Entorno

Para sobreescribir la configuración sin modificar `application.properties`:

| Variable | Descripción | Default |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/nextcar_db` |
| `DB_USERNAME` | Usuario de BD | `postgres` |
| `DB_PASSWORD` | Contraseña de BD | `1234` |

Ejemplo de ejecución con variables de entorno:

```bash
DB_URL=jdbc:postgresql://prod-server:5432/nextcar_db \
DB_USERNAME=nextcar_user \
DB_PASSWORD=SecurePassword \
./mvnw spring-boot:run
```

---

## Swagger / OpenAPI

La documentación interactiva de la API está disponible una vez que el servidor está corriendo:

```
http://localhost:8091/swagger-ui/index.html
```

Para endpoints protegidos, hacer click en **Authorize** (ícono de candado) e ingresar:

```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

El JSON de la especificación OpenAPI está en:

```
http://localhost:8091/v3/api-docs
```

---

## Próximos Módulos

| Módulo | Estado | Descripción |
|---|---|---|
| `iam` | ✅ Implementado | Autenticación, usuarios, roles, JWT |
| `verification` | 🔄 Pendiente | Verificación de DNI (RENIEC) y mayoría de edad |
| `financial` | 🔄 Pendiente | Plan de pagos método francés, Compra Inteligente, VAN, TIR |
| `customers` | 🔄 Pendiente | Perfil del cliente, vehículo, historial de préstamos |

---

*NextCar Backend — Finanzas e Ingeniería Económica · UPC*
