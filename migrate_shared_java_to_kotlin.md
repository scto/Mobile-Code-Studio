Aider Task: Java zu Kotlin Migration für :termux:shared
1. Zielsetzung
Konvertiere alle Java-Quelldateien im Verzeichnis termux/shared/src/main/java/ systematisch nach Kotlin. Die Dateien sollen nach termux/shared/src/main/kotlin/ verschoben werden, wobei die ursprünglichen Java-Dateien gelöscht werden.
2. Pfad-Spezifikationen
Quelle: termux/shared/src/main/java/
Ziel: termux/shared/src/main/kotlin/
Paket-Basis: com.scto.mcs.termux.shared
3. Workflow & Transformations-Regeln
Führe für jede Datei folgende Schritte aus:
Konvertierung: Erzeuge sauberen Kotlin-Code.
Nutze data class für reine Datenmodelle.
Nutze Kotlin Properties (val/var) anstelle von Getter/Setter-Methoden.
Korrektur bekannter Migrationsfehler:
UnixConstants: Entferne das Schlüsselwort const bei Variablen, deren Wert erst zur Laufzeit (z. B. via JNI) feststeht. Nutze dort einfaches val.
Annotations: Ersetze android.support.annotation oder javax.annotation durch androidx.annotation.NonNull oder entferne sie ganz, da Kotlin Null-Safety nativ über das Typsystem (String vs String?) regelt.
Coroutines: Wenn Java-Code Thread oder Handler nutzt, prüfe, ob eine Konvertierung zu withContext(Dispatchers.IO) sinnvoll ist (vor allem im TermuxInstaller).
Paketnamen: Ändere das Package-Statement in jeder Datei auf com.scto.mcs.termux.shared.[unterpaket].
Dateisystem:
Erstelle die Ziel-Ordnerstruktur unter src/main/kotlin/.
Speichere die neue .kt Datei.
Lösche die ursprüngliche .java Datei sofort nach erfolgreicher Migration.
4. Qualitätskontrolle
Vermeide den "Double Bang" Operator !!. Nutze stattdessen Safe-Calls ?. oder elvis Operatoren ?:.
Stelle sicher, dass companion object Blöcke für statische Member korrekt angelegt werden.
Anweisung an Aider:
"Arbeite dich methodisch durch alle Unterordner von termux/shared/src/main/java/. Konvertiere jede Datei nach Kotlin, verschiebe sie nach src/main/kotlin/ und lösche das Java-Original. Bestätige mir den Abschluss für jeden Unterordner (errors, file, installer, logger, markdown)."