Master Refactoring Plan: Mobile-Code-Studio (MCS)

Dieses Dokument dient als ultimative Anleitung für Aider, um das Projekt Mobile-Code-Studio von einer Legacy-Struktur in eine moderne, modulare Clean-Architecture-IDE zu transformieren.

Phase 1: Modul-Struktur & Hierarchie (Die neue Basis)

Ziel: Herstellung der Ziel-Hierarchie unter dem :core Namespace.

1. Verschiebungen im Dateisystem:

   * Verschiebe /core nach /core/common.

   * Verschiebe /data nach /core/data.

   * Verschiebe /domain nach /core/domain.

   * Verschiebe das gesamte /termux Verzeichnis nach /core/termux/.

2. settings.gradle.kts Update:

   * Registriere die neuen Pfade:

      * :core:common, :core:data, :core:domain, :core:ui, :core:utils, :core:resources

      * :core:termux:application, :core:termux:emulator, :core:termux:shared, :core:termux:view

3. Abhängigkeiten (Globaler Scan):

   * Ersetze in allen build.gradle.kts Dateien:

      * project(":core") -> project(":core:common")

      * project(":data") -> project(":core:data")

      * project(":domain") -> project(":core:domain")

      * project(":termux:X") -> project(":core:termux:X")

Phase 2: Terminal-Bereinigung & Paket-Branding

Ziel: Löschen von Altlasten und Umstellung auf das MCS-Paketschema.

1. Kahlschlag:

   * Lösche alle Dateien/Klassen bezüglich Terminal, NativeBridge und TermuxConstants im Modul :app und :feature.

2. Paket-Rename:

   * Ändere global alle Paketnamen von com.itsaky.androidide zu com.scto.mcs.

   * Verschiebe die Dateien in die entsprechenden Ordnerstrukturen com/scto/mcs/....

   * Aktualisiere alle android { namespace = "..." } Einträge in den Build-Files.

Phase 3: Ressourcen-Zentralisierung (:core:resources)

Ziel: Ein Single-Source-of-Truth Modul für UI-Assets.

1. Modul-Setup: Erstelle :core:resources mit Namespace com.scto.mcs.core.resources.

2. Migration: Verschiebe alle /res Inhalte aus allen Modulen (außer :app) dorthin. Führe XML-Dateien logisch zusammen.

3. R-Klassen Refactoring: Aktualisiere alle Kotlin-Imports global von *.R zu com.scto.mcs.core.resources.R.

Phase 4: Kotlin-Migration (Step-by-Step)

Ziel: 100% Kotlin im Core-Termux-Bereich.

1. Fokus :core:termux:shared:

   * Konvertiere Java-Dateien nach Kotlin.

   * Wichtig: Entferne const bei UnixConstants.kt (nur val für JNI-Werte).

   * Entferne @NonNull Annotationen (nutze Kotlin Null-Safety).

   * Lösche die .java Originale nach erfolgreicher .kt Erstellung.

Phase 5: Android-Komponenten & Manifest-Konfiguration

Ziel: Integration der TerminalActivity und Provider in das Hauptmodul.

1. Manifest Update (:app):

   * Füge Permissions hinzu: FOREGROUND_SERVICE, REQUEST_INSTALL_PACKAGES, REQUEST_DELETE_PACKAGES, POST_NOTIFICATIONS.

   * Registriere com.scto.mcs.core.termux.application.activities.TerminalActivity.

   * Registriere MCSDocumentsProvider (Authority: com.scto.mcs.documents).

   * Registriere MCSFileProvider (Authority: ${applicationId}.providers.fileprovider).

2. Provider-Klassen: Erstelle MCSDocumentsProvider.kt und MCSFileProvider.kt im Paket com.scto.mcs.provider im Modul :app.

Phase 6: Branding & Dokumentation

Ziel: Visuelle Identität und GitHub-Präsenz.

1. Assets: Erstelle ic_mcs_logo.xml (Vektor) und adaptive Launcher Icons in :core:resources.

2. README.md: Erstelle die Root-README mit Badges (Kotlin, GPLv3) und dem Mermaid-Architektur-Diagramm.

Phase 7: Setup-Flow & Build-Fähigkeit

Ziel: App startbereit machen.

1. Onboarding: Implementiere den Flow: Permissions -> JDK/SDK Dialoge -> Termux Bootstrap Screen.

2. Build Fixes: Stelle sicher, dass alle Module JavaVersion.VERSION_17 nutzen und Hilt-Module vollständig sind.

Anweisung an Aider: "Führe dieses Refactoring Phase für Phase durch. Beginne mit Phase 1 (Struktur). Ich werde nach jeder Phase einen Commit verlangen, um die Stabilität zu prüfen."
