import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/app_colors.dart';
import '../application/auth_notifier.dart';
import 'logout_confirm.dart';

/// Se muestra solo si el usuario tiene más de un destacamento asignado. La
/// elección queda fija para toda la sesión.
class DestacamentoPickerScreen extends ConsumerWidget {
  const DestacamentoPickerScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sesion = ref.watch(authNotifierProvider).value;
    final destacamentoIds = sesion?.destacamentoIds.toList() ?? const <int>[];
    final colors = AppColors.of(context);

    return Scaffold(
      backgroundColor: colors.bg,
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 380),
          child: Card(
            margin: const EdgeInsets.all(24),
            child: Padding(
              padding: const EdgeInsets.all(28),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  if (destacamentoIds.isEmpty) ...[
                    Text(
                      'Este usuario no tiene ningún destacamento asignado.',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                        color: colors.text,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Contacta a un administrador para que te asigne un '
                      'destacamento antes de continuar.',
                      style: TextStyle(color: colors.textMuted),
                    ),
                    const SizedBox(height: 16),
                    SizedBox(
                      width: double.infinity,
                      child: OutlinedButton(
                        onPressed: () =>
                            cerrarSesionConConfirmacion(context, ref),
                        child: const Text('Salir'),
                      ),
                    ),
                  ] else ...[
                    Text(
                      '¿En qué destacamento trabajas hoy?',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.w700,
                        color: colors.text,
                      ),
                    ),
                    const SizedBox(height: 16),
                    for (final destacamentoId in destacamentoIds)
                      ListTile(
                        // Nombre real requeriría GET /destacamentos, aún no
                        // portado en esta fase — se muestra el id por ahora.
                        contentPadding: EdgeInsets.zero,
                        leading: Icon(
                          ref.watch(destacamentoActivoProvider) ==
                                  destacamentoId
                              ? Icons.radio_button_checked
                              : Icons.radio_button_unchecked,
                          color: colors.primary,
                        ),
                        title: Text(
                          'Destacamento #$destacamentoId',
                          style: TextStyle(color: colors.text),
                        ),
                        onTap: () => ref
                            .read(destacamentoActivoProvider.notifier)
                            .seleccionar(destacamentoId),
                      ),
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: FilledButton(
                        style: FilledButton.styleFrom(
                          backgroundColor: colors.primary,
                        ),
                        onPressed: ref.watch(destacamentoActivoProvider) == null
                            ? null
                            : () => context.go('/inicio'),
                        child: const Text('Continuar'),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
