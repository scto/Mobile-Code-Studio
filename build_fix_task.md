Aider Task: Mobile-Code-Studio Build-Fähigkeit herstellen
1. Zielsetzung
Das Projekt soll erfolgreich kompiliert werden (./gradlew assembleDebug). Alle Module müssen korrekt verknüpft sein, Hilt muss initialisiert sein und eine minimale UI soll den Start der App ermöglichen.
2. Phase 1: Gradle & Dependency Check
Ziel: Konsistenz im gesamten Projekt sicherstellen.
* Tasks:
   * Überprüfe die libs.versions.toml auf fehlende Einträge (Hilt, Compose, Kotlin, NDK, JGit, Sora-Editor, Gson).
   * Stelle sicher, dass jedes Submodul (:app, :core, :termux:*, :feature:*) die korrekten plugins und dependencies in der build.gradle.kts hat.
   * Wichtig: Füge im :app Modul die NDK-Konfiguration hinzu, falls :termux:emulator JNI-Komponenten enthält.
   * Synchronisiere die Java-Version (17) über alle Module hinweg.
3. Phase 2: Hilt-Infrastruktur vervollständigen
Ziel: Laufzeitfehler durch fehlende Injektionen verhindern.
* Tasks:
   * Überprüfe MCSApplication.kt: Ist @HiltAndroidApp vorhanden? Werden die Manager initialisiert?
   * Erstelle fehlende Hilt-Module (@Module) in den :di Paketen der Submodule, falls Klassen mit @Inject noch nicht bereitgestellt werden.
   * Stelle sicher, dass die MainActivity.kt mit @AndroidEntryPoint annotiert ist.
4. Phase 3: Minimaler Navigations-Flow (MVP)
Ziel: Ein sichtbares Ergebnis beim App-Start.
* Tasks:
   * Implementiere eine einfache NavHost in der MainActivity.
   * Erstelle "Stub"-Screens (Platzhalter), falls Features noch nicht fertig portiert sind, damit die Navigation nicht abstürzt.
   * Flow: Onboarding (Permission Check) -> Setup (JDK Auswahl Dialog) -> Dashboard.
5. Phase 4: Kompilierungsfehler beheben
Ziel: Den Build-Prozess aktiv fixen.
* Tasks:
   * Analysiere Fehlermeldungen bezüglich Paketnamen (stelle sicher, dass alles auf com.scto.mcs gemappt ist).
   * Fixe alle "Unresolved reference" Fehler, die durch die Migration der Java-Files nach Kotlin entstanden sind (besonders im :termux Modul).
   * Überprüfe die AndroidManifest.xml auf korrekte Activity-Einträge und Berechtigungen.
Anweisung an Aider: "Versuche nun, das Projekt zu bauen mit ./gradlew assembleDebug. Analysiere die Fehlermeldungen und behebe sie systematisch. Achte besonders auf korrekte Hilt-Module und die Verknüpfung der neuen :termux Submodule. Das Ziel ist eine ausführbare APK, die den Onboarding-Screen anzeigt."