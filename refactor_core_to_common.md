Aider Task: Refactoring - Verschiebe Modul :core nach :core:common
1. Zielsetzung
Das bestehende Modul :core soll in eine hierarchische Struktur unter :core:common verschoben werden. Alle Projektreferenzen und Abhängigkeiten müssen aktualisiert werden, damit das Projekt weiterhin erfolgreich kompiliert.
2. Schritt 1: Dateisystem-Operationen
* Verschiebe den gesamten Inhalt des Ordners core/ in ein neues Verzeichnis core/common/.
* Stelle sicher, dass die src/, build.gradle.kts und libs/ Ordner nun unter core/common/ liegen.
* Lösche leere Verzeichnisse im alten core/ Pfad, die nicht mehr benötigt werden.
3. Schritt 2: Projekt-Konfiguration (Gradle)
* settings.gradle.kts:
   * Ändere include(":core") zu include(":core:common").
   * Falls project(":core").projectDir manuell gesetzt war, aktualisiere den Pfad auf file("core/common").
4. Schritt 3: Abhängigkeiten aktualisieren
Suche in allen build.gradle.kts Dateien des gesamten Projekts nach Referenzen auf das alte Modul und ersetze sie:
* Ersetze implementation(project(":core")) durch implementation(project(":core:common")).
* Ersetze api(project(":core")) durch api(project(":core:common")).
* Ersetze testImplementation(project(":core")) durch testImplementation(project(":core:common")).
5. Schritt 4: Paketnamen & Imports (Optional)
Hinweis: Normalerweise ändern sich Paketnamen nicht zwingend bei einer Modul-Verschiebung, aber falls gewünscht:
* Überprüfe, ob Paketnamen wie com.scto.mcs.core angepasst werden müssen (z.B. zu com.scto.mcs.core.common).
* Falls ja: Führe einen globalen "Find & Replace" für die betroffenen Imports durch.
6. Schritt 5: Verifikation
* Führe ./gradlew clean aus.
* Überprüfe die Konsistenz der Hilt-Module, da sich die Pfade für die Code-Generierung geändert haben könnten.
* Stelle sicher, dass alle :feature:* Module, das :data Modul und das :app Modul korrekt auf :core:common verweisen.
Anweisung an Aider: "Führe das Refactoring jetzt durch. Beginne mit dem Verschieben der Dateien und der Anpassung der settings.gradle.kts. Gehe dann systematisch durch alle anderen Module und aktualisiere die Projekt-Abhängigkeiten. Bestätige den Erfolg mit einer kurzen Zusammenfassung der geänderten Dateien."