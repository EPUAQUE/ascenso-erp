import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/token_service.dart';
import '../data/auth_api.dart';
import '../data/sesion_usuario.dart';

final authApiProvider = Provider<AuthApi>((ref) => AuthApi(ApiClient.instance));

/// `null` = no autenticado. El access token vive solo en [TokenService]
/// (memoria) — este notifier solo orquesta login/logout/refresh de sesión.
class AuthNotifier extends AsyncNotifier<SesionUsuario?> {
  @override
  Future<SesionUsuario?> build() async {
    ApiClient.instance.onUnauthorized = () {
      state = const AsyncData(null);
      ref.read(destacamentoActivoProvider.notifier).seleccionar(null);
    };
    return null;
  }

  Future<void> login(String username, String password) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final api = ref.read(authApiProvider);
      final token = await api.login(username, password);
      TokenService.instance.set(token);
      final sesion = await api.me();
      // Con un solo destacamento asignado no tiene sentido pedir que lo
      // elija — se salta el DestacamentoPickerScreen directo.
      if (sesion.destacamentoIds.length == 1) {
        ref
            .read(destacamentoActivoProvider.notifier)
            .seleccionar(sesion.destacamentoIds.first);
      }
      return sesion;
    });
  }

  Future<void> logout() async {
    final api = ref.read(authApiProvider);
    try {
      await api.logout();
    } finally {
      TokenService.instance.clear();
      await ApiClient.instance.clearCookies();
      ref.read(destacamentoActivoProvider.notifier).seleccionar(null);
      state = const AsyncData(null);
    }
  }
}

final authNotifierProvider =
    AsyncNotifierProvider<AuthNotifier, SesionUsuario?>(AuthNotifier.new);

/// Destacamento elegido al login — se mantiene fijo durante toda la sesión.
class DestacamentoActivoNotifier extends Notifier<int?> {
  @override
  int? build() => null;

  void seleccionar(int? destacamentoId) => state = destacamentoId;
}

final destacamentoActivoProvider =
    NotifierProvider<DestacamentoActivoNotifier, int?>(
      DestacamentoActivoNotifier.new,
    );
