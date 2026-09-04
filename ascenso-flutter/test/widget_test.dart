// Smoke test: la app arranca sin sesión y muestra la pantalla de login.

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:ascenso_flutter/main.dart';

void main() {
  testWidgets('Muestra la pantalla de login al arrancar', (
    WidgetTester tester,
  ) async {
    await tester.pumpWidget(const ProviderScope(child: AscensoApp()));
    await tester.pump();

    expect(find.text('Bienvenido, líder'), findsOneWidget);
  });
}
