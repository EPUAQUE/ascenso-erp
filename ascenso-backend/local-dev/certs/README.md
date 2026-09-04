# Llaves JWT de desarrollo local

`dev-private.pem` / `dev-public.pem` son un par RSA-2048 **solo para
`application-local.yml`** — deliberadamente fuera de `src/main/resources` para que
nunca queden empacadas en el jar que se despliega. Nunca usar este par en ningún
ambiente real; producción usa su propio par, generado aparte y montado por fuera
del repositorio.

**No están commiteadas** — generarlas una vez al clonar el repo:

```
./generar-llaves.sh
```

(No hace nada si ya existen. Equivale a correr a mano:
`openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out dev-private.pem`
seguido de `openssl rsa -pubout -in dev-private.pem -out dev-public.pem`.)

`application-local.yml` las referencia por defecto vía
`file:./local-dev/certs/dev-*.pem` — ruta relativa al directorio desde el que se
ejecuta `mvn spring-boot:run` (la raíz del módulo `ascenso-backend/`).
