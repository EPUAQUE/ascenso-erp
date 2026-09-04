import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/theme_notifier.dart';
import '../../auth/presentation/logout_confirm.dart';

/// Placeholder de inicio tras login — reemplazar por la pantalla real de
/// líder cuando arranque la fase de negocio de Exploradores del Rey.
class HomePlaceholderScreen extends ConsumerWidget {
  const HomePlaceholderScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final modoOscuro = ref.watch(themeModeProvider) == ThemeMode.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Exploradores del Rey'),
        actions: [
          IconButton(
            icon: Icon(
              modoOscuro ? Icons.light_mode_outlined : Icons.dark_mode_outlined,
            ),
            tooltip: modoOscuro ? 'Modo claro' : 'Modo oscuro',
            onPressed: () => ref.read(themeModeProvider.notifier).alternar(),
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Cerrar sesión',
            onPressed: () => cerrarSesionConConfirmacion(context, ref),
          ),
        ],
      ),
      body: const Center(child: Text('Próximamente — inicio de líder')),
    );
  }
}
