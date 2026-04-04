Aider Task: Systematische Java-zu-Kotlin Migration (Termux Module)

1. Zielsetzung

Konvertiere alle Java-Quelldateien in den angegebenen Submodulen nach Kotlin. Verschiebe die Dateien dabei vom src/main/java Verzeichnis in das entsprechende src/main/kotlin Verzeichnis und passe die Paketnamen an.

2. Quell- und Zielpfade

Verarbeite alle Dateien in den folgenden Verzeichnissen (inklusive aller Unterordner):

1. Modul: :termux:application (AKTUELL IN BEARBEITUNG)

   * Von: termux/application/src/main/java/com/

   * Nach: termux/application/src/main/kotlin/com/scto/mcs/termux/application/

2. Modul: :termux:emulator

   * Von: termux/emulator/src/main/java/com/

   * Nach: termux/emulator/src/main/kotlin/com/scto/mcs/termux/emulator/

3. Modul: :termux:shared

   * Von: termux/shared/src/main/java/com/

   * Nach: termux/shared/src/main/kotlin/com/scto/mcs/termux/shared/

4. Modul: :termux:view

   * Von: termux/view/src/main/java/com/

   * Nach: termux/view/src/main/kotlin/com/scto/mcs/termux/view/

3. Konvertierungs-Regeln

* Sprachstandard: Nutze 100% idiomatischen Kotlin-Code (Properties statt Getter/Setter, data class für POJOs, etc.).

* Null-Safety: Analysiere den Java-Code sorgfältig auf mögliche Null-Werte und setze ? (Nullable) oder !! / lateinit korrekt ein.

* Paketnamen: Ändere die package-Deklaration in jeder Datei auf das neue Schema com.scto.mcs.termux.[submodule]....

* Imports: Aktualisiere alle internen Imports, damit sie auf die neuen Kotlin-Paketpfade verweisen.

* Dateiendung: Ändere die Endung von .java zu .kt.

4. Workflow-Anweisung für Aider

1. Lies eine Java-Datei aus dem Quellverzeichnis.

2. Generiere den entsprechenden Kotlin-Code.

3. Erstelle die Zielverzeichnis-Struktur unter src/main/kotlin/... falls diese noch nicht existiert.

4. Speichere die neue .kt Datei am Zielort.

5. Lösche die ursprüngliche .java Datei erst, wenn die .kt Datei erfolgreich erstellt wurde.

6. Wiederhole diesen Vorgang für alle Dateien in allen Unterordnern der genannten Pfade.

Befehl an Aider: "Beginne jetzt mit der Migration. Arbeite dich Modul für Modul vor (zuerst shared, dann emulator, dann application, dann view). Achte peinlich genau darauf, dass kein Java-Code im Projekt verbleibt und alle Paketnamen auf 'com.scto.mcs' aktualisiert werden."
