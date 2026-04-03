Aider Task: Modulare Termux-Migration & Java-to-Kotlin Conversion
1. Ausgangslage
* Die Quell-Dateien (Java) befinden sich bereits im Projektordner unter ./termux/.
* Das Ziel ist die Integration als saubere, modulare Struktur innerhalb von com.scto.mcs.
Phase 1: Vollständiger Cleanup
Ziel: Entferne alle alten Terminal-Implementierungen, um Namenskollisionen zu vermeiden.
* Tasks:
   * Lösche alle Dateien im Zusammenhang mit der alten Terminal-Implementierung (z.B. TermuxConstants.kt, TermuxInstaller.kt, alte ViewModels oder Screens).
   * Entferne Terminal-bezogene Einträge aus der settings.gradle.kts und den build.gradle.kts Dateien, die nicht zur neuen Struktur gehören.
Phase 2: Modul-Setup (Gradle)
Ziel: Erstellung der neuen vierstufigen Modul-Hierarchie.
* Tasks:
   * Erstelle/Konfiguriere folgende Submodule in settings.gradle.kts:
      * :termux:application
      * :termux:emulator
      * :termux:shared
      * :termux:view
   * Erstelle die jeweiligen build.gradle.kts Dateien mit den notwendigen Abhängigkeiten (Hilt, AndroidX, etc.).
   * Stelle sicher, dass :termux:view das Modul :termux:emulator einbindet usw.
Phase 3: Migration & Kotlin-Konvertierung
Ziel: Verschieben der Dateien aus dem ./termux/ Ordner in die neuen Module und Konvertierung nach Kotlin.
* Tasks:
   * Scanne den Ordner ./termux/ und ordne die Dateien den Modulen application, emulator, shared und view zu.
   * WICHTIG: Konvertiere alle Java-Dateien in 100% idiomatischen Kotlin-Code.
   * Ändere alle Paketnamen von com.teixeira.vcspace oder com.termux zu com.scto.mcs.termux.[submodule].
   * Behandle Sichtbarkeiten (internal/public) und Nullability während der Konvertierung sorgfältig.
Phase 4: App-Integration (UI & Logik)
Ziel: Anbindung der neuen Module an die Mobile-Code-Studio Oberfläche.
* Tasks:
   * TerminalViewModel: Erstelle ein Hilt-ViewModel in :feature:terminal, das die TerminalSession aus den neuen Modulen verwaltet.
   * TerminalScreen: Erstelle ein Compose-Screen, das die TerminalView (aus :termux:view) via AndroidView einbindet.
   * TerminalSettings: Implementiere einen Einstellungs-Screen für Schriftgröße, Farben und Cursor-Stil, der die Werte in einem DataStore speichert.
Phase 5: Bootstrap-Installation & Lifecycle
Ziel: Implementierung des Standard-Termux-Installations-Workflows.
* Tasks:
   * Implementiere die Bootstrap-Logik:
      1. Download der ABI-spezifischen Bootstrap-ZIP (URLs siehe roadmap.md).
      2. Extraktion nach /data/data/com.scto.mcs/files/usr.
      3. Symlink-Fixing: Nutze android.system.Os.symlink, um die Links für sh, bin/ und lib/ wiederherzustellen.
      4. Initialisierung der Shell-Umgebung (Export von PATH, LD_LIBRARY_PATH).
   * Verknüpfe diesen Prozess mit dem SetupScreen.
📝 Anweisung an Aider
"Lies dieses Dokument und beginne mit Phase 1 und 2. Sobald die Modulstruktur steht, arbeite dich durch Phase 3, indem du die Dateien aus dem lokalen ./termux/ Verzeichnis nimmst, sie verschiebst und nach Kotlin konvertierst. Halte dich strikt an den Paketnamen com.scto.mcs."