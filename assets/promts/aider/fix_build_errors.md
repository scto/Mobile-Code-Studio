Aider Task: Fix Compilation Errors (:domain & :termux:shared)
1. Problem: Unresolved reference 'Inject' in :domain
Die UseCases im :domain Modul können die @Inject Annotation nicht finden.
* Ursache: Fehlende Dependency in der build.gradle.kts oder falsche Import-Anweisungen.
* Tasks:
   * Füge in domain/build.gradle.kts die Abhängigkeit für javax.inject oder hilt-android hinzu (vorzugsweise libs.javax.inject oder libs.hilt.android aus dem Version Catalog).
   * Korrigiere die Datei domain/src/main/java/com/scto/mcs/domain/usecase/CloneRepositoryUseCase.kt:
      * Stelle sicher, dass der Import import javax.inject.Inject lautet (Großbuchstabe 'I').
   * Korrigiere die Datei domain/src/main/java/com/scto/mcs/domain/usecase/LoadFileContentUseCase.kt:
      * Stelle sicher, dass der Import import javax.inject.Inject lautet.
2. Problem: Inconsistent JVM Target in :termux:shared
Java steht auf 1.8, während Kotlin auf 17 steht. Diese müssen übereinstimmen.
* Ursache: Diskrepanz in den compileOptions und kotlinOptions.
* Tasks:
   * Öffne termux/shared/build.gradle.kts.
   * Setze innerhalb des android-Blocks:
compileOptions {
   sourceCompatibility = JavaVersion.VERSION_17
   targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions {
   jvmTarget = "17"
}

3. Globaler Check: JVM Target Konsistenz
Um zukünftige Fehler zu vermeiden, stelle sicher, dass ALLE Module Java 17 verwenden.
   * Tasks:
   * Überprüfe alle build.gradle.kts Dateien im Projekt.
   * Überall dort, wo JavaVersion.VERSION_1_8 oder jvmTarget = "1.8" steht, muss dies auf VERSION_17 bzw. "17" aktualisiert werden.
Befehl an Aider: "Lies die fix_build_errors.md und behebe die Kompilierungsfehler. Achte besonders auf die korrekten Imports in den UseCases und die Konsistenz der JVM-Targets (Java 17) über alle Module hinweg, insbesondere in :termux:shared."