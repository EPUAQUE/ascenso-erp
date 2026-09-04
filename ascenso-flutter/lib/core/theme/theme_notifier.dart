import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Preferencia de tema — siempre abre en claro, cada usuario elige oscuro si
/// quiere (mismo criterio que `market-flutter`: nunca sigue el tema del
/// sistema operativo). Se persiste en `SharedPreferences` bajo la clave
/// `exploradores-tema`.
class ThemeNotifier extends Notifier<ThemeMode> {
  static const _prefKey = 'exploradores-tema';

  @override
  ThemeMode build() {
    _cargarGuardado();
    return ThemeMode.light;
  }

  Future<void> _cargarGuardado() async {
    final prefs = await SharedPreferences.getInstance();
    if (prefs.getString(_prefKey) == 'oscuro') {
      state = ThemeMode.dark;
    }
  }

  Future<void> alternar() async {
    state = state == ThemeMode.dark ? ThemeMode.light : ThemeMode.dark;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(
      _prefKey,
      state == ThemeMode.dark ? 'oscuro' : 'claro',
    );
  }
}

final themeModeProvider = NotifierProvider<ThemeNotifier, ThemeMode>(
  ThemeNotifier.new,
);
