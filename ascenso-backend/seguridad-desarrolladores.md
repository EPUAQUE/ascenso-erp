# Seguridad para desarrolladores

Guía operativa del módulo de autenticación y autorización de `ascenso-backend`
(`com.ais.ascensobackend.seguridad`). Este módulo es un port casi sin cambios del
mismo módulo en el proyecto hermano `market-backend` — la única diferencia de
fondo es de vocabulario de negocio: donde aquel habla de **tienda**, este habla de
**destacamento** (grupo local del programa, típicamente ligado a una iglesia), y
esta fase no soporta todavía asignación de "grupos de destacamentos" (solo
asignación individual, un usuario a la vez a uno o varios destacamentos).

Stack: Java 25, Spring Boot 4, Spring Security, JWT (RS256) + refresh token opaco,
Argon2id, PostgreSQL + Liquibase.

> Este es un **login propietario** que emite access tokens JWT de corta duración más un
> refresh token opaco para renovarlos. **No** es un flujo OAuth 2.0 / OpenID Connect
> completo. Si se necesita interoperar con un IdP externo, usar un proveedor de identidad
> aparte de este módulo.

---

## 1. Modelo de autorización

RBAC plano con alcance por destacamento:

1. **Usuario** — identidad y estado de la cuenta.
2. **Rol** (`SUPERVISOR_GENERAL`, `LIDER_PRINCIPAL`, `LIDER_GRUPO`, `PADRE`) —
   catálogo de roles de negocio. Solo `SUPERVISOR_GENERAL` es de alcance global.
3. **Permiso** — código plano `{MODULO}_{ACCION}` (ej. `CATALOGO_VER`,
   `DESTACAMENTOS_CREAR`, `USUARIOS_ASIGNAR_DESTACAMENTO`).
4. **RolPermiso** — asignación many-to-many; el conjunto efectivo de permisos de un
   usuario es la **unión** de los permisos de todos sus roles.
5. **UsuarioDestacamento** — asignación de un usuario a uno o varios destacamentos,
   cada una con un rol. Determina el **alcance** sobre el que puede operar, no solo
   qué puede hacer. A diferencia de `market-backend` (que además soporta asignar un
   usuario a un "grupo de tiendas" completo vía `UsuarioGrupoTienda`), esta fase
   solo soporta asignación individual — no existe agrupación de destacamentos
   todavía.

Regla de permiso:

```text
permitido = el usuario tiene, en alguno de sus roles (global o del destacamento
            solicitado), el permiso requerido por el endpoint,
            Y el destacamento solicitado está entre sus destacamentos asignados
            (o su rol es explícitamente de alcance global, ej. SUPERVISOR_GENERAL).
```

- **Fail-closed**: cualquier ausencia, duplicado, corrupción o ambigüedad de datos de
  permisos produce **denegación**.
- Un permiso concedido **no** implica acceso a cualquier destacamento: siempre se
  valida el alcance de `UsuarioDestacamento` para operaciones sobre recursos
  destacamento-específicos.

---

## 2. Cómo proteger un nuevo `RestController`

1. Definir (o reutilizar) un código de permiso plano en el catálogo `Permiso`
   (`MODULO_ACCION`, MAYÚSCULAS_SNAKE), sembrado por Liquibase.
2. Anotar el método con `@RequiresPermission("CODIGO_PERMISO")`.

```java
@RestController
@RequestMapping("/api/v1/destacamentos")
public class DestacamentoController {

    @GetMapping("/{id}")
    @RequiresPermission("DESTACAMENTOS_VER")
    public ResponseEntity<DestacamentoResponse> obtener(@PathVariable Long id) { ... }

    @PostMapping
    @RequiresPermission("DESTACAMENTOS_CREAR")
    public ResponseEntity<DestacamentoResponse> crear(@RequestBody @Valid CrearDestacamentoRequest req) { ... }
}
```

- Al arrancar, `RequiresPermissionStartupValidator` verifica que **todos** los
  códigos referenciados existan en la tabla `permiso`. Si falta alguno, **la
  aplicación no arranca**.
- Endpoints sin `@RequiresPermission` quedan igualmente **autenticados** por defecto
  (fail-closed en la capa HTTP), pero **no** tienen autorización de negocio; todo
  endpoint que mute o exponga datos de negocio debe anotarse.

## 3. Alcance por destacamento (equivalente a BOLA/IDOR)

`@RequiresPermission` autoriza la **acción**, no el **alcance**. Para recursos
destacamento-específicos, `PermissionInterceptor` valida automáticamente que la
variable de ruta `destacamentoId` (si el endpoint la declara) esté entre los
destacamentos asignados al usuario (o que su rol sea de alcance global):

```java
@PostMapping("/{destacamentoId}/actividades")
@RequiresPermission("ACTIVIDADES_CREAR")
public ResponseEntity<ActividadResponse> crear(
        @PathVariable Long destacamentoId, @RequestBody @Valid CrearActividadRequest req) { ... }
```

Para casos donde la validación debe hacerse dentro de un servicio de aplicación
(no solo en el controller), usar `AutorizacionDestacamentoService.exigirAcceso(id)`
— puerto de aplicación equivalente a `AutorizacionTiendaService` en market-backend.

Devolver `403` genérico sin revelar si el recurso existe en un destacamento ajeno.

---

## 4. Autenticación y contraseñas (Argon2id)

- Endpoint único de login: `POST /api/v1/auth/login` con `{ "username", "password" }`.
- **No existe** `/auth/salt` ni hashing en cliente. Argon2id genera y gestiona su propio
  sal interno.
- La contraseña se verifica con `PasswordEncoder.matches()`. **Nunca** se registra,
  retorna ni propaga en claro.
- `matches()` se ejecuta **siempre**, incluso si el usuario no existe (credencial
  ficticia), para no filtrar por tiempo.
- Toda falla (usuario inexistente, contraseña incorrecta, cuenta inactiva) produce la
  **misma** respuesta `401 AUTHENTICATION_FAILED`.

### Parámetros Argon2id

Definidos en `PasswordEncoderConfig`: `m=19456 KiB`, `t=2`, `p=1`, sal 16 bytes, hash 32
bytes (mínimo de referencia OWASP vigente). **Acción requerida por entorno**: realizar una
prueba de rendimiento en el hardware de producción y **subir el costo** hasta que una
verificación tarde ~0.25–0.5 s sin causar DoS. Documentar los valores finales.

### Política de contraseña

`PasswordPolicy`: longitud mínima configurable (≥12), máxima (≥64), medida en **code
points**. Se permiten todos los caracteres Unicode y espacios; **sin** reglas de
composición; **sin** truncado ni normalización.

### Cambio de contraseña y restablecimiento

- **Autoservicio**: `POST /api/v1/auth/password` (`{ "passwordActual", "passwordNueva" }`,
  usuario autenticado, sin permiso adicional). Verifica la actual con `matches()`, valida
  la nueva contra `PasswordPolicy`, y **revoca todos los refresh tokens del usuario**.
  `Usuario.cambiarPassword` sube `version_seguridad`.
- **Restablecimiento administrativo**: `POST /api/v1/usuarios/{usuarioId}/password/restablecer`
  (requiere `USUARIOS_RESTABLECER_PASSWORD`). El backend genera una contraseña
  temporal aleatoria (`TemporaryPasswordGenerator`, 20 caracteres, charset sin
  ambiguos) y la devuelve **una sola vez** en la respuesta — nunca se persiste en
  claro. Marca `usuario.debe_cambiar_password = true` y revoca todos sus refresh
  tokens.
- **Olvidé mi contraseña (autoservicio por correo)**: a diferencia de
  market-backend (que decidió NO tener este flujo — solo restablecimiento mediado
  por administrador), este proyecto SÍ lo porta, con el mismo criterio de no
  enumeración: `POST /api/v1/auth/forgot-password` siempre responde 200 con el
  mismo mensaje genérico exista o no el usuario, tenga o no correo, esté o no
  activo (`AuthServiceImpl.solicitarRestablecimiento` nunca lanza excepción para
  distinguir esos casos). El token de un solo uso se canjea en
  `POST /api/v1/auth/reset-password`.
- **`debe_cambiar_password`**: el login (`/auth/login`, `/auth/refresh`) incluye
  `debeCambiarPassword` en la respuesta cuando está marcado. El propio access token
  lleva el claim `debeCambiarPassword`, y `DebeCambiarPasswordFilter` bloquea con
  `403 DEBE_CAMBIAR_PASSWORD` cualquier ruta que no sea `/api/v1/auth/password`,
  `/logout` o `/me` mientras esté activo.

---

## 5. Access token y refresh token

- Firma **asimétrica RS256** para el access token. Componentes estándar: `JwtEncoder`
  (emisión), `JwtDecoder` (validación), sin filtro criptográfico propio.
- El algoritmo se **fija** en la configuración. **Nunca** se confía en el encabezado
  `alg`. Se rechaza `alg=none`, algoritmos inesperados, `kid` desconocido y tokens
  malformados.
- Claims del access token: `sub` (username canónico), `iss`, `aud`, `iat`, `nbf`,
  `exp`, `jti`, `destacamentos` (destacamentos activos del usuario, cuando
  aplique), `sver` (versión de seguridad del usuario al momento de emitir el
  token), `alcanceGlobal` y `debeCambiarPassword`. Tolerancia de reloj configurable
  (`clock-skew`, por defecto 30s). TTL corto (por defecto 10 min).
- El JWT **no cifra** su payload: **nunca** incluir contraseñas, hashes, sales, correos,
  nombres ni IP.

### Refresh token

- **Opaco** (no JWT), aleatorio, **rotatorio** y de **un solo uso**.
- Se almacena en servidor **hasheado**, nunca en claro, junto a `usuario_id`,
  `expira_en`, `revocado` y `token_padre_id`.
- `POST /api/v1/auth/refresh`: valida hash + vigencia + no revocado, emite access token
  nuevo y refresh token nuevo, y revoca el usado.
- **Reutilización de un refresh ya usado/revocado** ⇒ se revoca toda la cadena del
  usuario — posible robo de token.
- `POST /api/v1/auth/logout` revoca el refresh activo. El access token no se invalida
  (stateless): expira solo por TTL corto.

### Rotación de llaves (access token)

1. Generar el nuevo par fuera del código (ver §9) con un `kid` nuevo.
2. Añadir su llave **pública** como `app.security.jwt.keys[n]` (coexistencia): los
   tokens antiguos siguen validando.
3. Cambiar `app.security.jwt.active-kid` al nuevo `kid` y montar su llave privada.
4. Tras `exp` del último token firmado con la llave anterior, retirar la vieja.

### Invalidación temprana (antes de `exp`) y revocar sesiones

`Usuario.versionSeguridad` (incrementada por `cambiarPassword`/
`restablecerConPasswordTemporal`/`desactivar`/`bloquear`/`activar`/`revocarSesiones`)
viaja en el access token como claim `sver` y se revalida en **cada petición
autenticada** vía `SecurityVersionValidator` (`OAuth2TokenValidator<Jwt>`, registrado
en `JwtConfig.defaultValidators`) — no solo en los endpoints anotados con
`@RequiresPermission`. Si la versión no coincide, el usuario no existe o no está
activo, el token se rechaza con `401 AUTHENTICATION_FAILED` aunque siga vigente su
`exp`. `POST /api/v1/usuarios/{usuarioId}/sesiones/revocar`
(`USUARIOS_REVOCAR_SESIONES`) expone esto para revocar la sesión de otro usuario
sin tocar su contraseña ni su estado.

---

## 6. Rate limiting y anti-automatización

- Límites por **IP de origen validada** y por **HMAC del username canónico**.
  Configurables en `app.security.rate-limit.*`.
- Respuesta `429` con `Retry-After`, sin revelar si el usuario existe.
- El token bucket **se recarga** de forma continua: no hay bloqueos permanentes por
  username.
- `X-Forwarded-For` **solo** se respeta si la conexión viene de un proxy en
  `app.security.network.trusted-proxies`.
- La implementación por defecto es **en memoria (una instancia)**. En multi-instancia
  debe registrarse otro bean `LoginRateLimiter` con almacén compartido (p. ej. Redis).
- Los buckets en memoria se purgan periódicamente
  (`app.security.rate-limit.login.cleanup-interval`, por defecto 10 minutos) una
  vez que se recargan por completo.

---

## 7. Errores y logging

- Formato JSON uniforme (`ApiErrorResponse`): `timestamp`, `status`, `error`, `message`
  genérico, `path`, `correlationId`. **Nunca** stack traces, SQL, nombres de tabla ni
  claims internos.
- Códigos: `400` validación/JSON, `401` no autenticado/token inválido, `403` permiso o
  destacamento insuficiente, `429` rate limit.
- Eventos de seguridad (`SecurityAuditPublisher`): login OK/fallido, rate limit,
  reutilización de refresh, cambio de contraseña/estado de cuenta, asignación de
  destacamento, etc. — se escriben al logger `SECURITY_AUDIT` y, para los dos
  eventos de mayor severidad (`REFRESH_REUTILIZADO`, `RATE_LIMIT_ALCANZADO`),
  disparan una alerta por correo (`AlertaEmailService`). **A diferencia de
  market-backend, esta fase NO tiene todavía un módulo de auditoría persistente**
  (tabla `audit_event`) — es un punto de extensión documentado, no una omisión
  silenciosa: si se necesita consultar el historial de eventos desde la API más
  adelante, agregar ese módulo e implementar `SecurityAuditPublisher` contra él sin
  tocar los ~14 call sites que ya lo invocan.
- **Prohibido registrar**: contraseñas, hashes, bearer tokens, refresh tokens (ni su
  hash), claves privadas o datos personales completos. Las entradas se **sanitizan**.

---

## 8. TLS, cookies y frontend

- **TLS/HTTPS es obligatorio en producción**. **HSTS** debe habilitarse en el
  borde/proxy correspondiente.
- El access token viaja en `Authorization: Bearer`; no se guarda en cookies.
- El refresh token se entrega en cookie `HttpOnly`, `Secure`, `SameSite=Strict`,
  con `SameSite=Strict` como defensa CSRF primaria para /refresh y /logout.
- Cualquier frontend (backoffice web, app móvil) debe mantener el access token
  **solo en memoria** (no `localStorage`/`sessionStorage`) y dejar el refresh token
  exclusivamente en la cookie que el navegador gestiona — o, en apps móviles nativas
  sin cookie de navegador, en almacenamiento seguro del SO.

---

## 9. Gestión de llaves (sin secretos en el repo)

Generar el par RSA fuera del código y montarlo de forma segura (gestor de secretos,
keystore o archivos montados). Ejemplo local (no usar estas llaves en producción):

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private-key-2026-01.pem
openssl rsa -in private-key-2026-01.pem -pubout -out public-key-2026-01.pem
```

Luego apuntar `JWT_PRIVATE_KEY_LOCATION` / `JWT_PUBLIC_KEY_LOCATION` a esas rutas
(`file:...`). Las llaves y el `.env` están en `.gitignore`. **Nunca** versionar llaves
privadas, `.env` ni credenciales reales. Las llaves de desarrollo local
(`local-dev/certs/dev-*.pem`) se generan una vez con
`local-dev/certs/generar-llaves.sh` y nunca se commitean.

---

## 10. Responsabilidades de infraestructura

Fuera del código de la aplicación, deben resolverse en la plataforma:

- **TLS/HTTPS** y terminación segura; **HSTS** en el borde.
- **Proxy confiable** y `trusted-proxies` correctos para `X-Forwarded-For`.
- **Gestor de secretos** para llaves JWT y credenciales de BD.
- **Rate limiting distribuido** (almacén compartido) en multi-instancia.
- **Límites de tamaño de headers/cuerpo** en el proxy.
- **Sincronización de reloj** (NTP) coherente con el `clock-skew` configurado.

---

## 11. Migraciones (Liquibase, PostgreSQL)

- `seguridad/001-usuario.xml` — tabla `usuario` con `password_hash`, `estado`,
  `version_seguridad`, unicidad de `username`, más los datos de perfil
  (`nombre`/`telefono`/`correo`) y `debe_cambiar_password` ya incluidos desde el
  primer changeset (a diferencia de market-backend, que los agregó en fases
  sucesivas — acá no hay historial previo que preservar).
- `seguridad/002-rol-permiso.xml` — tablas `rol`, `permiso`, `rol_permiso`.
- `seguridad/003-usuario-destacamento.xml` — tabla `usuario_destacamento`
  (usuario, destacamento, rol). `destacamento_id` nace sin FK (la tabla
  `destacamento` todavía no existe en este punto del changelog maestro) — ver
  ARCHITECTURE.md §10, "FK diferida". La FK real se agrega en
  `destacamentos/001-destacamento.xml`.
- `seguridad/004-refresh-token.xml` — tabla `refresh_token`.
- `seguridad/005-password-reset-token.xml` — tabla `password_reset_token`.
- `seguridad/006-seed-roles-permisos-usuarios.xml` — siembra los 4 roles
  (`SUPERVISOR_GENERAL`, `LIDER_PRINCIPAL`, `LIDER_GRUPO`, `PADRE`) y los permisos
  propios de este módulo.
- `seguridad/999-seed-rol-permiso-supervisor-general-todos.xml` — **incluido al
  final del changelog maestro**, después de todos los demás módulos: otorga a
  `SUPERVISOR_GENERAL` cualquier permiso ya sembrado, vía un único `<sql>` con una
  subconsulta (`INSERT ... SELECT ... WHERE NOT EXISTS ...`), en vez de un insert
  por permiso repetido en cada módulo (que es como lo hace market-backend con su
  rol `ADMIN`). `alcance_global=true` en este rol solo exime la validación de
  acceso por destacamento — la validación de permiso en sí siempre exige una fila
  real en `rol_permiso`.

Un changeset por cambio de esquema; nunca editar un changeset ya aplicado en un ambiente
compartido — se agrega uno nuevo.

---

## 12. Diferencias deliberadas frente a market-backend

Este módulo es un port, no una reescritura — estas son las únicas divergencias de
diseño, todas explícitamente pedidas para esta fase 1 de Exploradores del Rey:

- **Sin grupos de destacamentos**: no existe el equivalente a
  `UsuarioGrupoTienda`/`GrupoTienda`. Un usuario se asigna a destacamentos
  individuales únicamente. Si el producto llega a necesitar agrupar
  destacamentos (ej. por región/distrito), replicar el patrón de
  `UsuarioGrupoTienda` de market-backend en una fase futura.
- **`AdminUserSeeder` no asigna destacamento**: como el rol sembrado es
  `SUPERVISOR_GENERAL` (`alcance_global=true`), el usuario administrador de
  desarrollo se crea sin ninguna fila en `usuario_destacamento` — a diferencia de
  market-backend, que sí asigna su ADMIN sembrado a una tienda "CENTRAL" fija
  (innecesario aquí: `PermisosEfectivosResolverImpl` ya resuelve el alcance global
  sin necesitar una asignación de destacamento).
- **Sin módulo de auditoría persistente todavía**: `SecurityAuditPublisherImpl`
  solo loguea + emite métricas + alerta por correo (ver §7); no persiste en una
  tabla `audit_event` como lo hace la versión de market-backend posterior a su
  Fase 7.
- **Sin FEL ni `ProdSafetyGuard` de facturación**: no aplica a este dominio de
  negocio; se portó únicamente el `ProdSafetyGuard` de seguridad (seed/JWT).
