import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../features/auth/application/auth_notifier.dart';
import '../features/auth/presentation/destacamento_picker_screen.dart';
import '../features/auth/presentation/forgot_password_screen.dart';
import '../features/auth/presentation/login_screen.dart';
import '../features/auth/presentation/reset_password_screen.dart';
import '../features/home/presentation/home_placeholder_screen.dart';

class RouterRefreshNotifier extends ChangeNotifier {
  RouterRefreshNotifier(Ref ref) {
    ref.listen(authNotifierProvider, (_, _) => notifyListeners());
    ref.listen(destacamentoActivoProvider, (_, _) => notifyListeners());
  }
}

/// Único guard de navegación: sesión + destacamento elegido. La ruta inicial
/// tras login es siempre `/inicio` (placeholder por ahora — ver
/// `HomePlaceholderScreen`; ninguna pantalla de negocio real todavía).
final routerProvider = Provider<GoRouter>((ref) {
  final refreshNotifier = RouterRefreshNotifier(ref);
  ref.onDispose(refreshNotifier.dispose);

  return GoRouter(
    initialLocation: '/login',
    refreshListenable: refreshNotifier,
    redirect: (context, state) {
      final authState = ref.read(authNotifierProvider);
      if (authState.isLoading) return null;

      final sesion = authState.value;
      final isLoggingIn = state.matchedLocation == '/login';
      final isPickingDestacamento = state.matchedLocation == '/destacamento';
      final isRecuperandoPassword =
          state.matchedLocation == '/olvide-password' ||
          state.matchedLocation == '/restablecer-password';

      if (sesion == null) {
        return (isLoggingIn || isRecuperandoPassword) ? null : '/login';
      }

      final destacamentoActivo = ref.read(destacamentoActivoProvider);
      if (destacamentoActivo == null) {
        return isPickingDestacamento ? null : '/destacamento';
      }

      if (isLoggingIn || isPickingDestacamento) return '/inicio';

      return null;
    },
    routes: [
      GoRoute(path: '/login', builder: (context, state) => const LoginScreen()),
      GoRoute(
        path: '/olvide-password',
        builder: (context, state) => const ForgotPasswordScreen(),
      ),
      GoRoute(
        path: '/restablecer-password',
        builder: (context, state) => const ResetPasswordScreen(),
      ),
      GoRoute(
        path: '/destacamento',
        builder: (context, state) => const DestacamentoPickerScreen(),
      ),
      GoRoute(
        path: '/inicio',
        builder: (context, state) => const HomePlaceholderScreen(),
      ),
    ],
  );
});
