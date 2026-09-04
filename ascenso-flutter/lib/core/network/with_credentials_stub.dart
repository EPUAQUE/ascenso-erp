import 'package:dio/dio.dart';

/// No-op fuera de web — Android/iOS no necesitan `withCredentials`, ahí el
/// refresh token viaja vía un `CookieJar` persistente
/// (`cookie_manager_io.dart`) en vez de depender del navegador.
void enableWithCredentials(Dio dio) {}
