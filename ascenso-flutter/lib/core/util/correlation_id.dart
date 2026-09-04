import 'package:uuid/uuid.dart';

final _uuid = Uuid();

/// UUID v4 criptográficamente aleatorio — clave de idempotencia para toda
/// operación que el backend pueda reintentar de forma segura por esta clave.
/// Generar UNA VEZ por intento y reutilizar en un reintento manual —
/// regenerarla en cada llamada anularía la protección contra duplicados que
/// esto existe para dar.
String nuevoCorrelationId() => _uuid.v4();
