Aider Task: Migration zur modularen Termux-Struktur
1. Zielsetzung
Ersetze die aktuelle Termux-Implementierung durch eine modulare Struktur basierend auf dem android-code-studio Repository. Erstelle neue Submodule und portiere die Logik für Terminal-Emulation, Session-Management und UI.
2. Neue Modul-Struktur
Erstelle und konfiguriere folgende Module in der settings.gradle.kts und erstelle die entsprechenden Verzeichnisse:
1. :termux: Dieses Modul wird die Kern-Emulations-Logik halten.
2. :core:utils: Für allgemeine Hilfsklassen wie die System-Umgebung.
3. Implementierung des :termux Moduls
Portiere den Code von scto/android-code-studio/termux in das neue Modul :termux.
* Organisiere den Code in folgende Pakete unter com.scto.mcs.termux:
   * .application
   * .emulator
   * .shared
   * .view
* Wichtig: Alle Dateien müssen auf den Paketnamen com.scto.mcs.termux angepasst werden.
4. Implementierung :core:utils
Portiere die Datei Environment.java von hier.
* Anforderung: Konvertiere die Datei von Java nach Kotlin.
* Paketname: com.scto.mcs.core.utils.
* Stelle sicher, dass die Pfade in der Environment.kt (wie TERMUX_PREFIX) mit den TermuxConstants aus der roadmap.md übereinstimmen.
5. Terminal UI & Logik (:feature:editor / :feature:terminal)
Implementiere die Terminal-Oberfläche basierend auf den bereitgestellten Quellen:
A. TerminalSession & Logik
* Portiere alle Dateien aus terminal/session nach com.scto.mcs.feature.terminal.session.
* Portiere alle Dateien aus fragments/terminal in das entsprechende Feature-Paket.
B. ViewModel
* Portiere TerminalFragmentViewModel.kt von hier.
* Refaktoriere es zu einem Hilt-ViewModel namens TerminalViewModel unter com.scto.mcs.feature.terminal.
C. TerminalScreen (Compose Integration)
* Konvertiere das TerminalFragment.kt (Quelle) in ein Jetpack Compose Composable namens TerminalScreen.
* Nutze AndroidView, um die TerminalView aus dem neuen :termux Modul einzubinden.
* Verbinde den Screen mit dem TerminalViewModel.
6. Cleanup & Integration
* Entferne die bisherige, einfache Termux-Implementierung in Mobile-Code-Studio (alte Phase 4 Ansätze), die nun durch das :termux Modul ersetzt wird.
* Aktualisiere die build.gradle.kts Dateien der betroffenen Module, um Abhängigkeiten zu :termux und :core:utils hinzuzufügen.
* Stelle sicher, dass Hilt für alle neuen Komponenten verwendet wird.
Anweisung an Aider: "Führe diese Migration Schritt für Schritt durch. Beginne mit der Erstellung der Module :termux und :core:utils. Portiere dann den Kern-Code und passe die Paketnamen strikt an com.scto.mcs an. Konvertiere Java-Dateien dabei in sauberes Kotlin."