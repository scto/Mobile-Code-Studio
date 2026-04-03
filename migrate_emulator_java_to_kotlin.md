Aider Task: Java zu Kotlin Migration für :termux:emulator
1. Zielsetzung
Konvertiere alle Java-Dateien im Verzeichnis termux/emulator/src/main/java/ (und allen Unterordnern) nach Kotlin. Verschiebe die resultierenden Dateien in das entsprechende kotlin-Verzeichnis und lösche die ursprünglichen Java-Dateien.
2. Pfad-Spezifikation
* Quell-Pfad: termux/emulator/src/main/java/
* Ziel-Pfad: termux/emulator/src/main/kotlin/ (Spiegelung der Paketstruktur unter src/main/kotlin).
* Paket-Ziel: com.scto.mcs.termux.emulator
3. Workflow-Anweisungen
Führe für jede Java-Datei folgende Schritte aus:
1. Analysiere die Java-Datei. Achte besonders auf native Methoden (native keyword) und statische Initialisierer (static { System.loadLibrary(...) }).
2. Erzeuge den entsprechenden Kotlin-Code:
   * Konvertiere native Methoden in external fun.
   * Verschiebe static Blöcke und Konstanten in ein companion object.
   * Nutze idiomatische Kotlin-Konstrukte (Properties, Default-Werte, Null-Safety).
3. Erstelle die Zielordner-Struktur unter src/main/kotlin/ (falls noch nicht vorhanden).
4. Speichere die konvertierte Datei mit der Endung .kt.
5. Aktualisiere die package-Deklaration und alle internen import-Anweisungen auf das Schema com.scto.mcs.termux.emulator.
6. Lösche die ursprüngliche .java Datei sofort nach der erfolgreichen Erstellung der .kt Datei.
4. Qualitätsstandards
* Null-Safety: Vermeide !!. Nutze ? für Nullable-Typen und lateinit nur dort, wo es durch Dependency Injection oder Lifecycle notwendig ist.
* JNI-Kompatibilität: Stelle sicher, dass Funktionsnamen für native Methoden exakt erhalten bleiben, damit die JNI-Bridge im C++ Code weiterhin funktioniert.
* Konvertierung: Getter und Setter müssen in saubere Kotlin-Properties umgewandelt werden.
* Kommentare: Bestehende Kommentare (deutsch/englisch) müssen vollständig übernommen werden.
Befehl für Aider: "Lies dieses Dokument und starte die Migration für alle Dateien unter termux/emulator/src/main/java/. Gehe methodisch vor. Falls eine Datei JNI-Methoden enthält, achte darauf, dass die Signatur für den nativen Code erhalten bleibt. Bestätige den Abschluss, sobald keine .java Dateien mehr im Emulator-Modul vorhanden sind."