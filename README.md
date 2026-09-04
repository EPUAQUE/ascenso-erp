# Ascenso ERP — Exploradores del Rey

Sistema para llevar el control de ascenso de niños y jóvenes (4 a 17 años)
en los destacamentos de Exploradores del Rey: grupos por edad
(Pre-Navegantes, Navegantes, Pioneros, Seguidores, Exploradores), premios
bíblico / destreza / liderazgo / requerido, medallas bronce-plata-oro por
año de programa, y aislamiento de datos entre destacamentos (una iglesia
nunca ve los datos de otra — solo el supervisor general ve todos).

Tres subproyectos, misma base técnica que `market-erp` (stack, seguridad y
tema visual reutilizados; el dominio de negocio es completamente distinto):

| Carpeta | Qué es | Stack |
| --- | --- | --- |
| `ascenso-backend` | API y lógica de negocio | Spring Boot 4 / Java 25, Postgres, Liquibase |
| `ascenso-backoffice` | Panel de administración web | Vue 3 + TypeScript, Tailwind |
| `ascenso-flutter` | App para líderes de grupo | Flutter |

## Estado actual

Fundación técnica ya construida y verificada: seguridad completa (JWT +
refresh token en cookie, RBAC con roles `SUPERVISOR_GENERAL` /
`LIDER_PRINCIPAL` / `LIDER_GRUPO` / `PADRE`, aislamiento por
`destacamento`), las 21 tablas del modelo de datos aplicadas vía Liquibase,
y login funcionando de punta a punta en los tres clientes.

**Lo que NO existe todavía** (siguiente fase): pantallas y lógica de
negocio para niños, asistencia, progreso de premios, medallas, actividades
y anuncios. Los módulos `catalogo` (grupos, libros, destrezas, liderazgo,
pasos requeridos, regla de asistencia) y `destacamentos` sí tienen CRUD
completo; el resto de las 21 tablas existe solo como esquema.

Ver `ascenso-backend/ARCHITECTURE.md` y `ascenso-backend/seguridad-desarrolladores.md`
para el detalle de la arquitectura y el modelo de seguridad.

## Requisitos

- Java 25, Maven
- Node 20+, pnpm
- Flutter SDK
- Docker (para Postgres local)

## Arranque local

**1. Base de datos** (una sola vez, o cada vez que reinicies el equipo):

```bash
docker compose up -d db
```

**2. Backend**:

```bash
cd ascenso-backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Liquibase aplica el esquema completo automáticamente. Queda sembrado un
usuario administrador:

- Usuario: `admin`
- Contraseña: `Admin1234!Seguro`

**3. Backoffice**:

```bash
cd ascenso-backoffice
pnpm install
pnpm run dev
```

Abre en `http://localhost:5173`, ya apuntando a `http://localhost:8080`
(ver `.env.development`).

> Si el puerto 8080 ya está ocupado (ej. `market-backend` corriendo al
> mismo tiempo), seteá `SERVER_PORT=8081` antes de levantar el backend
> (`SERVER_PORT=8081 mvn spring-boot:run -Dspring-boot.run.profiles=local`)
> y actualizá `VITE_API_BASE_URL` en `ascenso-backoffice/.env.development`
> a juego.

**4. App de líderes (Flutter)**:

```bash
cd ascenso-flutter
flutter pub get
flutter run -d chrome --dart-define=API_BASE_URL=http://localhost:8080
```

## Por qué no hay contenedor para backend/backoffice en desarrollo

Corren nativos (`mvn spring-boot:run` / `pnpm run dev`) porque es más
rápido iterar así que reconstruir una imagen Docker en cada cambio — mismo
flujo que usa `market-erp` día a día. `docker-compose.yml` en esta carpeta
solo levanta Postgres. Un compose de despliegue real (con Caddy, TLS y
backups, como el de `market-backend`) es trabajo de una fase posterior,
cuando haya un dominio y un servidor real.
