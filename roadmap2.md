Roadmap - TermuxInstaller & VCSpace

Diese Roadmap dokumentiert die geplanten Erweiterungen für die stabile Wiederherstellung von Umgebungen, die JNI-Sicherheit und die Benutzererfahrung in Kotlin.

🏗️ Technische Infrastruktur & Stabilität (100+)

1. JNI Memory Safety Layer: Implementierung eines RAII-Musters (Resource Acquisition Is Initialization) in C++, um sicherzustellen, dass ReleaseStringUTFChars immer aufgerufen wird, selbst bei Fehlern oder vorzeitigen Funktionsabbrüchen.

2. Kotlin Native-Wrapper mit Result-Typen: Kapselung der NativeBridge-Aufrufe in Kotlin Result<T>-Objekte, um native Fehlercodes (errno) in aussagekräftige Kotlin-Exceptions oder Error-States zu übersetzen.

3. Coroutines-basierter Installer: Vollständige Auslagerung des Entpackungsprozesses in Dispatchers.IO, um ein Blockieren des UI-Threads während der Erstellung tausender Symlinks zu verhindern.

4. Atomic File Operations: Entwicklung eines Mechanismus, der Symlinks zuerst in einem temporären Verzeichnis (.tmp) validiert, bevor sie final verschoben werden, um Teilinstallationen bei Abstürzen zu vermeiden.

🛡️ Berechtigungen & Kompatibilität (110+)

1. Advanced Permission Checker (Kotlin): Ein dediziertes Jetpack Compose Modul zur Prüfung von MANAGE_EXTERNAL_STORAGE und spezifischen Pfad-Schreibrechten vor dem Start des Installers.

2. FileSystem Capability Probe: Ein nativer Testlauf zur Erkennung, ob das Ziel-Dateisystem (z.B. externe SD-Karten mit FAT32/exFAT) Symlinks technisch unterstützt.

3. SELinux Context Handling: Analyse und Workarounds für "Permission Denied" Fehler unter Android 13+, die trotz korrekter Dateirechte durch SELinux-Policies entstehen.

⚡ Performance & Analyse (120+)

1. Parallel Extraction Engine: Nutzung von Kotlin Flows/Channels, um Archivdaten parallel zu dekomprimieren und an die native Linking-Engine zu übergeben.

2. VCS-Doctor (Diagnose-Tool): Ein integriertes Tool zum Scannen der Verzeichnisstruktur auf tote Symlinks mit automatischer Reparaturfunktion ("Fix-it-Button").

3. I/O Benchmark Suite: Messung der Schreibgeschwindigkeiten und "Links per Second", um Engpässe in der C++ Bridge zu identifizieren.

🎨 UI/UX & Ecosystem (130+)

1. Real-Time Progress Dashboard: Detaillierte Fortschrittsanzeige in Compose, die zwischen Download-, Extraktions- und Link-Phase unterscheidet.

2. Multi-Root / Container-Management: Erweiterung der Architektur für den Betrieb mehrerer isolierter VCSpace-Instanzen (Side-by-Side).

3. Backup & Restore API: Kotlin-basierter Service zum Packen des gesamten Environments inklusive aller Symlink-Metadaten für Cloud-Backups.

Status: In Bearbeitung (Fokus auf Symlink-Restoration & JNI-Leaks)
