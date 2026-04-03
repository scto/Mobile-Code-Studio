Aider Task: Java zu Kotlin Migration für :termux:application

1. Zielsetzung

Konvertiere alle Java-Dateien im Verzeichnis Mobile-Code-Studio/termux/application/src/main/java/com/ (und allen Unterordnern) nach Kotlin. Verschiebe die resultierenden Dateien in das entsprechende kotlin-Verzeichnis und lösche die ursprünglichen Java-Dateien.

2. Pfad-Spezifikation

* Quell-Pfad: Mobile-Code-Studio/termux/application/src/main/java/com/

* Ziel-Pfad: Mobile-Code-Studio/termux/application/src/main/kotlin/com/scto/mcs/termux/application/ (oder entsprechend der Paketstruktur unter src/main/kotlin).

3. Workflow-Anweisungen

Führe für jede Java-Datei folgende Schritte aus:

1. Analysiere die Java-Datei auf Logik, Nullability und Paketstruktur.

2. Erzeuge den entsprechenden Kotlin-Code (verwende idiomatische Konstrukte wie data class, properties, when etc.).

3. Erstelle die Zielordner-Struktur unter src/main/kotlin/ (falls noch nicht vorhanden).

4. Speichere die konvertierte Datei mit der Endung .kt.

5. Aktualisiere die package-Deklaration und alle internen import-Anweisungen auf das neue Schema com.scto.mcs.termux.application.

6. Lösche die ursprüngliche .java Datei sofort nach der erfolgreichen Erstellung der .kt Datei.

4. Qualitätsstandards

* Nutze Null-Safety (vermeide !!, wenn möglich).

* Konvertiere Getter/Setter in Kotlin-Properties.

* Behalte Kommentare (in Deutsch) bei.

* Stelle sicher, dass die Sichtbarkeiten (public, protected, private) korrekt übersetzt werden.

Befehl für Aider: "Lies dieses Dokument und starte die Migration für alle Dateien unter Mobile-Code-Studio/termux/application/src/main/java/com/. Gehe methodisch vor und bestätige, wenn alle Java-Dateien gelöscht und durch Kotlin-Dateien unter src/main/kotlin ersetzt wurden."
