# Guía de arquitectura del proyecto — Ascenso Backend (Exploradores del Rey)

## 1. Objetivo

Ascenso Backend es el núcleo de **Exploradores del Rey**: un sistema para llevar el
seguimiento del progreso de niños y jóvenes a través de un programa de escultismo
cristiano (grupos etarios, libros bíblicos, destrezas, liderazgo, asistencia,
medallas) organizado en destacamentos locales (típicamente ligados a una iglesia).

La arquitectura es un **Modular Monolith** con **DDD** (Domain-Driven Design) y
**Clean Architecture** por módulo — el mismo patrón portado del proyecto hermano
`market-backend` (ERP retail), adaptado a este dominio distinto:

- Un solo desplegable (un solo proceso Spring Boot), pero con **fronteras de módulo
  estrictas**: cada módulo representa un contexto delimitado (bounded context) del
  negocio y solo expone su capa de aplicación al resto del sistema.
- Cada módulo se organiza en capas Clean Architecture (`domain → application →
  infrastructure/api`) para que las reglas de negocio no dependan de Spring, JPA ni HTTP.
- Esta separación permite, si el negocio crece, extraer un módulo a un servicio
  independiente sin reescribir su lógica de dominio.

## 2. Paquete raíz y módulos

```text
com.ais.ascensobackend
```

Cada módulo de negocio vive directamente bajo este paquete raíz:

| Módulo | Paquete | Responsabilidad de negocio | Fase 1 |
| --- | --- | --- | --- |
| Seguridad | `seguridad` | Usuarios, roles, permisos, autenticación JWT | Java completo |
| Destacamentos | `destacamentos` | Catálogo de destacamentos (grupos locales) | Java completo |
| Catálogo | `catalogo` | Grupos etarios, años de programa, libros bíblicos, destrezas, liderazgo, pasos requeridos, reglas de asistencia | Java completo |
| Niños | (tablas `nino`, `trimestre`, `asistencia`, `progreso_*`, `medalla_otorgada`, `logro_mayor`) | Registro y seguimiento de niños, asistencia y avance | Solo esquema (Liquibase) — sin Java todavía |
| Actividades | (tablas `actividad`, `anuncio`, `nino_padre`) | Actividades por destacamento, anuncios globales, portal de padres | Solo esquema (Liquibase) — sin Java todavía |

Los nombres de los paquetes se escriben siempre en minúsculas, sin guiones ni
guiones bajos.

## 3. Estructura obligatoria de un módulo

Cada módulo debe seguir la misma división de capas. No es necesario crear paquetes
vacíos: se agregan cuando el módulo realmente los utiliza.

```text
src/main/java/com/ais/ascensobackend/
├── AscensoBackendApplication.java
├── destacamentos/
│   ├── domain/
│   │   ├── model/            (agregados, entidades de dominio, value objects)
│   │   ├── repository/       (interfaces de persistencia — puertos)
│   │   └── exception/         (excepciones de negocio del módulo)
│   ├── application/
│   │   ├── services/
│   │   │   ├── interfaces/    (casos de uso expuestos a otros módulos)
│   │   │   └── impl/          (orquesta dominio + repositorios; límite @Transactional)
│   │   └── dtos/               (comandos/resultados internos entre capas)
│   ├── infrastructure/
│   │   └── persistence/
│   │       ├── entities/       (entidades JPA — nunca el modelo de dominio)
│   │       ├── repositories/   (Spring Data JPA)
│   │       ├── adapters/       (implementan los puertos de domain.repository)
│   │       └── mappers/        (MapStruct: entidad JPA ↔ modelo de dominio)
│   └── api/
│       ├── controllers/
│       ├── dtos/
│       │   ├── requests/
│       │   └── responses/
│       └── mappers/            (MapStruct: resultado de aplicación ↔ DTO HTTP)
└── shared/
    ├── domain/
    ├── exceptions/
    ├── responses/
    ├── api/
    └── infrastructure/
        ├── web/                 (CorrelationIdFilter)
        ├── alertas/             (AlertaEmailService)
        └── persistence/         (PaginaMapper)
```

| Paquete | Responsabilidad |
| --- | --- |
| `domain.model` | Agregados y entidades con las reglas de negocio; sin anotaciones de Spring/JPA. |
| `domain.repository` | Contratos de persistencia en lenguaje de dominio (puertos). |
| `application.services.interfaces` | Casos de uso del módulo; único punto de entrada permitido desde otros módulos. |
| `application.services.impl` | Orquesta agregados y repositorios; define transacciones. |
| `infrastructure.persistence.entities` | Entidades JPA (mapeo a tablas PostgreSQL). |
| `infrastructure.persistence.adapters` | Implementan `domain.repository` usando Spring Data JPA + MapStruct. |
| `api.controllers` | Expone endpoints HTTP; valida entrada; delega al servicio de aplicación. |
| `api.dtos.*` | Contrato HTTP de entrada/salida. |

## 4. Regla de dependencia entre capas (Clean Architecture)

```text
api  →  application  →  domain  ←  infrastructure
```

- `domain` no depende de nada del framework: sin `@Entity`, sin `@Service`, sin
  `jakarta.persistence`. Es Java puro con las reglas del negocio.
- `application` depende solo de `domain` (agregados + interfaces de repositorio).
  Define los casos de uso y el límite transaccional (`@Transactional`).
- `infrastructure` depende de `domain` para implementar sus puertos, y de librerías
  externas (Spring Data JPA, Spring Mail, Spring Security).
- `api` depende solo de `application` (nunca de `infrastructure` ni de otro módulo
  directamente).
- Una entidad de persistencia (`infrastructure.persistence.entities`) o un agregado
  de dominio **nunca** se expone directamente en la API: los controllers reciben y
  devuelven DTOs de `api.dtos`.

## 5. Regla de dependencia entre módulos

Un módulo **solo** puede depender de `application.services.interfaces` de otro
módulo, o de `domain.repository`/`domain.model` cuando ese módulo publica un
identificador plano que otro necesita validar (ver `UsuarioServiceImpl` en
`seguridad`, que depende de `destacamentos.domain.repository.DestacamentoRepository`
para validar que un destacamento existe antes de asignar un usuario). Nunca de las
entidades JPA ni del `infrastructure` de otro módulo.

Ejemplo de esta fase: `seguridad.UsuarioServiceImpl.asignarDestacamento` valida la
existencia del destacamento vía `DestacamentoRepository.findById` (puerto de
dominio del módulo `destacamentos`), pero la columna `destacamento_id` en
`usuario_destacamento` es un identificador plano — Seguridad no tiene ni necesita
una FK a nivel de Java, solo a nivel de base de datos (ver §10, "FK diferida").

## 6. Convenciones de nombres

| Tipo | Convención | Ejemplo |
| --- | --- | --- |
| Agregado / entidad de dominio | Nombre singular del negocio | `Destacamento`, `Grupo`, `Destreza` |
| Entidad JPA | Igual al agregado + sufijo `Entity` | `DestacamentoEntity` |
| Repositorio (puerto, dominio) | `{Agregado}Repository` | `DestacamentoRepository` |
| Repositorio Spring Data (infra) | `{Agregado}JpaRepository` | `DestacamentoJpaRepository` |
| Adaptador de repositorio | `{Agregado}RepositoryAdapter` | `DestacamentoRepositoryAdapter` |
| Interfaz de servicio de aplicación | `{Agregado}Service` | `DestacamentoService` |
| Implementación | `{Agregado}ServiceImpl` | `DestacamentoServiceImpl` |
| Controller | `{Agregado}Controller` | `DestacamentoController` |
| DTO de entrada (API) | `{Accion}Request` | `CrearDestacamentoRequest` |
| DTO de salida (API) | `{Agregado}Response` | `DestacamentoResponse` |
| Excepción de negocio | `{Motivo}Exception` | `DestacamentoDuplicadoException` |

Clases/interfaces/enums en `PascalCase`; métodos y atributos en `camelCase`;
constantes en `UPPER_SNAKE_CASE`. Tablas y columnas PostgreSQL en `snake_case`
(ver §10).

## 7. Ejemplo — módulo `destacamentos` (agregado simple, catálogo)

```java
package com.ais.ascensobackend.destacamentos.domain.model;

public class Destacamento {

    private final Long id;
    private final String numeroUnico;
    private String nombre;
    private ModoCorteAnio modoCorteAnio;
    private boolean activo;

    public static Destacamento nuevo(
            String numeroUnico, String nombre, String iglesiaNombre, String direccion,
            ModoCorteAnio modoCorteAnio) {
        return new Destacamento(null, numeroUnico, nombre, iglesiaNombre, direccion, modoCorteAnio, true);
    }

    public void desactivar() {
        this.activo = false;
    }

    // getters, sin setters públicos salvo intención de negocio explícita
}
```

```java
package com.ais.ascensobackend.destacamentos.domain.repository;

public interface DestacamentoRepository {

    Destacamento save(Destacamento destacamento);

    Optional<Destacamento> findById(Long id);

    boolean existsByNumeroUnico(String numeroUnico);

    List<Destacamento> findAll();
}
```

```java
package com.ais.ascensobackend.destacamentos.application.services.impl;

@Service
public class DestacamentoServiceImpl implements DestacamentoService {

    private final DestacamentoRepository destacamentoRepository;

    @Override
    @Transactional
    public DestacamentoResumen crear(
            String numeroUnico, String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio) {
        if (destacamentoRepository.existsByNumeroUnico(numeroUnico)) {
            throw new DestacamentoDuplicadoException(numeroUnico);
        }
        Destacamento destacamento = Destacamento.nuevo(numeroUnico, nombre, iglesiaNombre, direccion, modoCorteAnio);
        return toResumen(destacamentoRepository.save(destacamento));
    }
}
```

La entidad JPA vive aparte, en `infrastructure.persistence.entities`, y un
`DestacamentoRepositoryAdapter` en `infrastructure.persistence.adapters` traduce
entre `DestacamentoEntity` (JPA) y `Destacamento` (dominio) usando un mapper de
MapStruct.

## 8. Ejemplo — catálogo con jerarquía (`catalogo`)

El módulo `catalogo` tiene 7 agregados relacionados entre sí por un identificador
plano (`grupoId`, `anioProgramaId`), nunca por `@ManyToOne` de JPA cruzando
agregados — el mismo criterio que usa `seguridad`/`destacamentos` entre módulos,
aplicado aquí dentro de un mismo módulo por simplicidad (son 7 tablas pequeñas del
mismo bounded context, no 7 módulos separados):

```text
Grupo (1) ──< AnioPrograma (1) ──< PasoRequerido
Grupo (1) ──< LibroBiblico
Grupo (1) ──< Destreza
Grupo (1) ──< Liderazgo
ReglaAsistencia (histórica, sin relación a Grupo)
```

`AnioProgramaServiceImpl.crear` valida que el `grupoId` recibido exista
(`GrupoRepository.findById`) antes de crear el año de programa — mismo patrón de
validación de existencia que en `destacamentos`/`seguridad`.

## 9. DTOs (capa `api`)

Los DTOs constituyen el contrato HTTP. Inmutables cuando sea posible; nunca se
reutiliza una entidad JPA ni un agregado de dominio como DTO. Este proyecto mezcla
`record` (DTOs de una sola capa, ej. `CrearDestacamentoRequest`) y `@Value @Builder`
de Lombok (DTOs de respuesta con más campos, ej. `DestacamentoResponse`) — elige una
convención por tipo de DTO y mantenla en todo el módulo.

## 10. Persistencia PostgreSQL y Liquibase

- Motor: **PostgreSQL**. Tablas y columnas en `snake_case` (`usuario_destacamento`,
  `anio_programa`, `progreso_libro`).
- Migraciones con **Liquibase**, un changelog maestro que incluye un changelog por
  módulo:

```text
src/main/resources/db/changelog/
├── db.changelog-master.xml
└── modules/
    ├── seguridad/001-usuario.xml …
    ├── destacamentos/001-destacamento.xml
    ├── catalogo/001-grupo.xml …
    ├── ninos/001-nino.xml …          (solo esquema, sin código Java todavía)
    └── actividades/001-actividad.xml … (solo esquema, sin código Java todavía)
```

- Un changeset por cambio de esquema, nunca editar un changeset ya aplicado en un
  ambiente compartido — se agrega uno nuevo.
- `GenerationType.IDENTITY` (soportado nativamente por PostgreSQL) para claves
  primarias `Long`, salvo asociaciones puente sin identidad propia (ej.
  `nino_padre`, con clave primaria compuesta).
- Restricciones de negocio (unicidad, `CHECK`) se declaran en el changelog, no solo
  en Bean Validation — la base es la última línea de defensa (ej.
  `ck_destacamento_modo_corte_anio`, `ck_regla_asistencia_unidad`).
- **FK diferida entre módulos**: cuando un módulo A necesita referenciar una tabla
  de un módulo B que su changelog master incluye *después*, la columna nace como
  `BIGINT` plano sin FK en el changelog de A, y la FK real se agrega en el
  changelog de B una vez que B publica su tabla. Ejemplo real en este proyecto:
  `seguridad/003-usuario-destacamento.xml` crea `usuario_destacamento.destacamento_id`
  sin FK; `destacamentos/001-destacamento.xml` la agrega
  (`fk_usuario_destacamento_destacamento`) una vez que la tabla `destacamento`
  existe. Mismo patrón que usa `market-backend` entre `tiendas` y `seguridad`.

## 11. Repositories

Puerto en `domain.repository` (lenguaje de dominio, sin `JpaRepository`), adaptador
en `infrastructure.persistence.adapters` que sí usa Spring Data JPA — ver el
ejemplo completo de `DestacamentoRepository`/`DestacamentoRepositoryAdapter` en §7.

## 12. MapStruct

MapStruct traduce entre las tres representaciones de un mismo concepto (entidad
JPA, agregado de dominio, DTO HTTP) para que ninguna capa dependa de otra hacia
arriba:

```java
package com.ais.ascensobackend.destacamentos.infrastructure.persistence.mappers;

@Mapper(componentModel = "spring")
public interface DestacamentoEntityMapper {

    Destacamento toDomain(DestacamentoEntity entity);

    @Mapping(target = "creadoEn", ignore = true)
    DestacamentoEntity toEntity(Destacamento domain);
}
```

Un segundo mapper en `api.mappers` traduce el resultado de aplicación (`*Resumen`)
↔ DTO HTTP (`*Response`). No se combinan ambos mappers en uno solo: cada capa mapea
solo hacia su vecino inmediato.

## 13. Manejo de errores

Las excepciones de negocio son específicas (`DestacamentoDuplicadoException`, …) y
se traducen a una respuesta HTTP consistente desde `@RestControllerAdvice` en
`shared.exceptions.GlobalExceptionHandler` (ver `seguridad-desarrolladores.md` §7
para los códigos usados por el módulo de seguridad):

```text
shared/
├── exceptions/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BusinessException.java
└── responses/
    └── ApiErrorResponse.java
```

El error de la API incluye, como mínimo: marca de tiempo, código HTTP, mensaje
legible, ruta e identificador de error (`correlationId`). Nunca se devuelven trazas
de ejecución ni mensajes de PostgreSQL al cliente.

## 14. Uso recomendado de Lombok

| Anotación | Uso recomendado |
| --- | --- |
| `@RequiredArgsConstructor` | Inyección por constructor en controllers y adaptadores simples. |
| `@Value` | DTOs inmutables (capa `api`). |
| `@Builder` | Construcción legible de DTOs con varios atributos. |
| `@Getter` / `@Setter` | Entidades JPA. |
| `@NoArgsConstructor` | Solo en entidades JPA, con el acceso más restrictivo posible. |

Evitar: `@Data` en entidades JPA o en agregados de dominio, `@Setter` a nivel de
clase en dominio, `@SneakyThrows`, `@EqualsAndHashCode` incluyendo asociaciones JPA.
Los agregados de dominio (`domain.model`) no llevan anotaciones Lombok orientadas a
persistencia — son objetos Java simples con métodos de negocio (constructor +
factory estático `nuevo(...)` + métodos de mutación con nombre de negocio, nunca
setters genéricos).

## 15. Pruebas

Las pruebas reflejan la misma estructura de capas del módulo:

```text
src/test/java/com/ais/ascensobackend/
└── destacamentos/
    ├── domain/
    │   └── model/DestacamentoTest.java
    ├── application/
    │   └── services/DestacamentoServiceImplTest.java
    ├── infrastructure/
    │   └── persistence/DestacamentoRepositoryAdapterTest.java
    └── api/
        └── controllers/DestacamentoControllerTest.java
```

- `domain`: pruebas unitarias puras (sin `@SpringBootTest`) de las reglas de negocio.
- `application`: pruebas de orquestación con repositorios y dependencias mockeadas.
- `infrastructure`: pruebas de integración de las consultas (Testcontainers + PostgreSQL).
- `api`: pruebas del contrato HTTP con `MockMvc`.

Nombres de prueba describen el comportamiento esperado, ej.:
`shouldRejectDestacamentoDuplicadoAlCrear`.

Esta fase 1 no incluye todavía la suite de pruebas — es la siguiente prioridad
antes de sumar lógica de negocio en `ninos`/`actividades`.

## 16. Dependencias necesarias

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-liquibase</artifactId>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

`mapstruct-processor` se declara en `maven-compiler-plugin` junto con
`lombok-mapstruct-binding` para que Lombok y MapStruct convivan en la misma clase
cuando sea necesario. No fijar versiones individuales cuando estén administradas
por el BOM de Spring Boot.

## 17. Lista de verificación para un nuevo módulo

Antes de considerar completo un módulo, verificar que:

- Está ubicado bajo `com.ais.ascensobackend.<modulo>` con las cuatro capas
  (`domain`, `application`, `infrastructure`, `api`).
- El `domain.model` no importa nada de `jakarta.persistence`, Spring ni HTTP.
- El controller solo gestiona HTTP y delega al servicio de aplicación.
- Otro módulo, si lo necesita, solo importa `application.services.interfaces` (o,
  cuando hace falta validar existencia de un identificador plano,
  `domain.repository`/`domain.model`) de este módulo — nunca su `infrastructure` ni
  entidades JPA.
- Las entidades JPA y los agregados de dominio no se exponen en la API.
- Cada endpoint que muta o expone datos de negocio está anotado con
  `@RequiresPermission("CODIGO_PERMISO")` (ver `seguridad-desarrolladores.md` §2) y
  ese código existe en el catálogo `permiso` sembrado por Liquibase — de lo
  contrario `RequiresPermissionStartupValidator` falla el arranque.
- Si el endpoint opera sobre un destacamento concreto, el path declara una
  variable `destacamentoId` para que `PermissionInterceptor` también valide el
  alcance de destacamento del usuario (ver §3, "regla de permiso").
- Existen migraciones Liquibase para el esquema nuevo, con restricciones a nivel
  de base de datos para las reglas críticas.
- No se registran contraseñas, tokens ni información sensible en logs.
