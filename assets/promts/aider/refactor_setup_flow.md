Aider Task: Refactor Onboarding & Setup Flow for Mobile-Code-Studio
1. Zielsetzung
Refaktoriere den initialen App-Start-Flow. Die App muss einen geführten Prozess durchlaufen: Berechtigungen -> Konfiguration (JDK/SDK) -> Installation (Termux/Tools) -> Dashboard.
2. Phase A: Onboarding & Permissions (:feature:onboarding)
* Screen: OnboardingScreen
* Logik: Überprüfe beim ersten Start die Berechtigung MANAGE_EXTERNAL_STORAGE (für Android 11+).
* Aktion: Zeige eine kurze Erklärung, warum der Zugriff auf den Speicher für eine IDE notwendig ist. Bei Erfolg navigiere zum SetupScreen.
3. Phase B: Setup Konfiguration (:feature:setup)
* Screen: SetupScreen (Zustandsgesteuert)
* Schritt 1 (JDK Dialog): Zeige einen Dialog mit einer Liste zur Auswahl des JDKs:
   * openjdk-17
   * openjdk-21
* Schritt 2 (Build-Tools Dialog): Nach der JDK-Wahl zeige einen zweiten Dialog für die Android Build-Tools:
   * build-tools 33, 34, 35, 36.
* Speicherung: Speichere die Auswahl temporär im SetupViewModel oder via DataStore.
4. Phase C: Initialisierung & Terminal Bootstrap
* Screen: InitializationScreen
* Visualisierung: Ein Terminal-ähnliches Fenster, das den Installations-Fortschritt live anzeigt.
* Logik (Reihenfolge der Installation):
   1. Termux Initialisierung: Nutze den TermuxInstaller (Phase 4 der Roadmap), um das Dateisystem und die PTY-Bridge vorzubereiten.
   2. Basis Tools: Installation essentieller Binaries (bash, git, coreutils).
   3. JDK Installation: Installiere das in Phase B gewählte openjdk.
   4. Android SDK & Tools:
      * Lade die Command Line Tools herunter.
      * Installiere die gewählten build-tools.
      * Installiere platform-tools (adb, etc.) und die entsprechenden platforms.
* Navigation: Nach erfolgreichem Abschluss aller Installationen (Status: Completed), wechsle automatisch zum Dashboard.
5. Phase D: Dashboard (:feature:dashboard)
* Screen: DashboardScreen
* Inhalt: Zeige die Hauptoptionen der IDE an:
   * Projekt öffnen (File-Picker)
   * Projekt erstellen (Templates)
   * Projekt clonen (Git-Integration)
6. Technische Anforderungen
* Hilt: Nutze @HiltViewModel für die State-Verwaltung zwischen den Dialogen und dem Bootstrap.
* Compose: Verwende Material 3 Dialoge und eine saubere Navigation (Compose Navigation).
* Status-Handling: Nutze einen Flow<SetupState>, um die Installationsschritte (Downloading, Extracting, Installing, Finished) an die UI zu melden.
* Paket-Mapping: Alle Klassen im Namespace com.scto.mcs.
Anweisung an Aider: "Setze diesen Refactoring-Task Schritt für Schritt um. Beginne mit dem Onboarding und den Auswahl-Dialogen im :feature:setup Modul. Nutze den TermuxInstaller aus dem :core Modul als Basis für die Installations-Logik."