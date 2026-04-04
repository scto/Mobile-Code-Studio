Aider Task: Step-by-Step Java-zu-Kotlin Migration (:termux:shared)
​1. Zielsetzung
​Konvertiere alle Java-Quelldateien im Modul :termux:shared nach Kotlin. Die Migration erfolgt schrittweise pro Unterordner, um maximale Präzision zu gewährleisten.
​2. Pfad-Definitionen
​Basis-Quelle: termux/shared/src/main/java/com/scto/mcs/termux/shared/
​Basis-Ziel: termux/shared/src/main/kotlin/com/scto/mcs/termux/shared/
​3. Der Migrations-Workflow (Stufe für Stufe)
​Bearbeite die Unterordner in dieser exakten Reihenfolge. Schließe einen Ordner komplett ab (inkl. Löschen der Java-Dateien), bevor du den nächsten startest:
​STEP 1: errors/ (Errno.kt, Error.kt etc.)
​STEP 2: file/ (Inkl. filesystem/ und UnixConstants.kt)
​STEP 3: installer/ (TermuxInstaller.kt etc.)
​STEP 4: logger/ (Logger.kt etc.)
​STEP 5: markdown/ (MarkdownUtils.kt etc.)
​STEP 6: Alle verbleibenden Ordner (models, net, shell, termux etc.)
​4. Konvertierungs-Regeln pro Datei
​Properties: Wandle Getter/Setter in Kotlin-Properties (val/var) um.
​Null-Safety: Nutze ? für Nullable-Typen. Entferne @NonNull-Annotationen, da Kotlin dies nativ im Typ-System löst.
​UnixConstants Fix: Entferne das Schlüsselwort const bei Werten, die via JNI/Laufzeit ermittelt werden (nur val verwenden).
​Dateisystem-Aktion:
​Erstelle die Zielstruktur unter src/main/kotlin/....
​Lösche die .java Datei sofort, nachdem die .kt Datei erfolgreich erstellt und geprüft wurde.
​5. Interaktions-Anweisung
​"Bitte arbeite jetzt STEP 1 ab. Wenn du mit dem Ordner errors/ fertig bist und alle Java-Dateien dort gelöscht hast, halte an und frage mich: 'Soll ich mit STEP 2 (file/) fortfahren?'"
​Befehl an Aider:
"Lies die migrate_shared_step_by_step.md. Wir starten mit STEP 1. Konvertiere den Inhalt von termux/shared/src/main/java/com/scto/mcs/termux/shared/errors/ nach Kotlin, verschiebe ihn nach src/main/kotlin und lösche die Java-Originale."