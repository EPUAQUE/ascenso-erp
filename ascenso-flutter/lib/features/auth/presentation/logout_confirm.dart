import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../application/auth_notifier.dart';

/// Único punto de entrada para cerrar sesión — nunca llamar
/// `authNotifierProvider.notifier.logout()` directamente desde una pantalla.
///
/// A diferencia de `market-flutter`, esta app todavía no tiene una cola de
/// sincronización offline (fase futura, ver CLAUDE.md/spec de scaffolding) —
/// cuando exista, este flujo deberá bloquear el logout con pendientes sin
/// sincronizar igual que allá. Por ahora solo pide una confirmación simple.
Future<void> cerrarSesionConConfirmacion(
  BuildContext context,
  WidgetRef ref,
) async {
  final confirmar = await showDialog<bool>(
    context: context,
    builder: (dialogContext) => AlertDialog(
      title: const Text('Cerrar sesión'),
      content: const Text('¿Seguro que deseas cerrar tu sesión?'),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(dialogContext).pop(false),
          child: const Text('Cancelar'),
        ),
        FilledButton(
          onPressed: () => Navigator.of(dialogContext).pop(true),
          child: const Text('Cerrar sesión'),
        ),
      ],
    ),
  );
  if (confirmar == true) {
    await ref.read(authNotifierProvider.notifier).logout();
  }
}
