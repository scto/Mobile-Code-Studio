Mobile-Code-Studio: Entwicklungsleitfaden & Aider-Prompts

Dieses Dokument beschreibt den schrittweisen Aufbau von Mobile-Code-Studio, einer mobilen IDE für Android. Die Entwicklung folgt der Clean Architecture und einer Multi-Modul-Struktur.

🛠 Projekt-Spezifikationen

* Paket-Name: com.scto.mcs

* Gradle: 8.11.2 (Kotlin DSL)

* Kotlin: 2.2.0

* Java: 17

* SDKs: Compile 36, Target 35, Min 26

* UI: Jetpack Compose mit Material 3

📂 Modul-Struktur

* :app – Einstiegspunkt & Hilt-Setup

* :core – Zentrale Manager (Terminal, Editor-Config, Files)

* :core:ui – Design System (Theme, Icons, Komponenten)

* :domain – Business Logik (Interfaces, Use Cases, Entities)

* :data – Daten-Implementierung (Repositories, JGit, File-I/O)

* :feature:onboarding – Berechtigungen

* :feature:setup – Terminal-Bootstrap & SDK/JDK Setup

* :feature:dashboard – Projektverwaltung & Git-Clone

* :feature:editor – Code-Editor & Build-Panel

* :feature:settings – App-Einstellungen

* :feature:debug – Logcat-Viewer

🚀 Phase 1: Die Infrastruktur

Ziel: Aufbau der Gradle-Struktur und des Version Catalogs.

Aider Prompt:

"Konfiguriere die Projekt-Basis. Erstelle oder aktualisiere die libs.versions.toml mit folgenden Versionen:

* Kotlin 2.2.0, Gradle 8.11.2, Java 17, Compose BOM (neueste)

* Hilt (2.51), JGit (6.8.0), Sora-Editor (0.23.0) Setze in der Root-build.gradle.kts den Hilt-Classpath und konfiguriere die settings.gradle.kts für alle Submodule: :app, :core, :core:ui, :domain, :data, :feature:onboarding, :feature:setup, :feature:dashboard, :feature:editor, :feature:settings, :feature:debug. Stelle sicher, dass compileSdk 36 und targetSdk 35 voreingestellt sind."

🧠 Phase 2: Core-Logik & Manager (:core)

Ziel: Implementierung der "Gehirn"-Komponenten als Singletons.

Aider Prompt:

"Implementiere im Modul :core die zentralen Manager als Hilt-Singletons:

1. TerminalEnvironment: Erstellt Ordnerstruktur (home, usr/bin, tmp) im internen App-Speicher und verwaltet PATH, JAVA_HOME und ANDROID_HOME.

2. EditorConfigManager: Lädt TextMate-Grammatiken asynchron und synchronisiert das Editor-Farbschema mit Material 3.

3. FileSystemUtils: Funktionen für Dateizugriffe im mobilecodestudio Pfad inklusive FileProvider Setup.

4. Erstelle ein Hilt-Modul in :core, das diese Klassen bereitstellt."

🎨 Phase 3: Design System (:core:ui)

Ziel: Einheitliches Look-and-Feel der IDE.

Aider Prompt:

"Initialisiere das Modul :core:ui:

1. Erstelle das MCSTheme (Material 3) mit einer Dark-Mode Palette für IDEs (Hintergrund: #1E1E1E, Akzente: Deep Blue).

2. Implementiere Basis-Komponenten: MCSToolbar, MCSButton und MCSIcons (Icons für Files, Folder, Git, Play, Terminal).

3. Erstelle eine TerminalText Komponente mit Monospace-Font."

🏗 Phase 4: Clean Architecture (:domain & :data)

Ziel: Trennung von Logik und technischer Umsetzung.

Aider Prompt:

"Fülle die Module :domain und :data:

1. :domain: Erstelle Repository-Interfaces für ProjectRepository, GitRepository und EditorRepository. Erstelle UseCases wie LoadFileContentUseCase und CloneRepositoryUseCase.

2. :data: Implementiere diese Repositories unter Nutzung der Manager aus :core.

3. Verbinde die Schichten via @Binds in einem Hilt-RepositoryModule in :data."

🚦 Phase 5: Onboarding & Setup (:feature:setup)

Ziel: Berechtigungserteilung und Laufzeit-Installation.

Aider Prompt:

"Implementiere den Start-Flow:

1. :feature:onboarding: Screen für MANAGE_EXTERNAL_STORAGE Permission (Android 11+).

2. :feature:setup: Terminal-Setup-Screen mit Dialogen zur Wahl von JDK (17/21) und Android SDK (33-36). Simuliere den Installationsfortschritt in einer Terminal-View und speichere die Pfade in TerminalEnvironment ab."

📂 Phase 6: Dashboard & Git (:feature:dashboard)

Ziel: Projektverwaltung und Klonen von Repositories.

Aider Prompt:

"Implementiere das Dashboard:

1. Screen mit Optionen: Projekt öffnen, Erstellen, Clonen und Einstellungen.

2. Integriere CloneProjectDialog: Nutze GitRepository (JGit), um Repositories mit Fortschrittsanzeige in den mobilecodestudio Ordner zu klonen. Navigiere nach Erfolg zum Editor."

✍️ Phase 7: Editor & Build (:feature:editor)

Ziel: Das funktionale Herzstück der App.

Aider Prompt:

"Implementiere das :feature:editor Modul:

1. Nutze AndroidView für den Sora-Editor. Binde Syntax-Highlighting über EditorConfigManager ein.

2. Implementiere ein Terminal-Panel am unteren Bildschirmrand.

3. Build-Funktion: Führe ./gradlew assembleDebug im Projektpfad aus, nutze JDK/SDK Pfade aus :core und streame den Output live in das Terminal-Panel."

💡 Best Practices für Aider

1. Kontext bewahren: Nutze /add [Dateipfad], bevor du eine neue Phase startest, damit Aider die Schnittstellen kennt.

2. Build-Checks: Führe nach jeder Phase einen Gradle-Sync in Android Studio durch.

3. Fehlerbehebung: Wenn Aider einen Fehler macht, nutze /undo und beschreibe das Problem genauer.
