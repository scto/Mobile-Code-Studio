Aider Task: Behebung der Kompilierungsfehler in :termux:shared
1. Zielsetzung
Behebung aller "Unresolved reference" und "Const val" Fehler im Modul :termux:shared, die durch die Migration von Java zu Kotlin entstanden sind.
2. Phase 1: Fehlende Abhängigkeiten (build.gradle.kts)
Ergänze in termux/shared/build.gradle.kts folgende Abhängigkeiten, da diese im Code referenziert werden:
AndroidX Annotations: Für @NonNull (nutze androidx.annotation:annotation).
Kotlinx Coroutines: Für Dispatchers und withContext (nutze org.jetbrains.kotlinx:kotlinx-coroutines-android).
Markwon (Markdown): Für Markwon, LinkifyPlugin etc. (nutze io.noties.markwon:core und entsprechende Plugins).
3. Phase 2: Syntax-Korrekturen (Kotlin-spezifisch)
A. UnixConstants.kt (Const Val Fehler)
Problem: const val darf in Kotlin nur für echte Compile-Time Konstanten verwendet werden. Werte, die via JNI oder System-Aufrufen ermittelt werden, dürfen kein const haben.
Task: Entferne das Schlüsselwort const bei allen Variablen in UnixConstants.kt, die Fehler werfen. Nutze einfaches val.
B. Annotations-Fix (Errno.kt, Error.kt, Logger.kt)
Problem: Unresolved reference 'NonNull'.
Task: 1. Importiere androidx.annotation.NonNull.
2. Da Kotlin bereits über das Typ-System (String vs String?) Non-Nullability garantiert, entferne die @NonNull Annotationen dort, wo sie redundant sind, es sei denn, sie werden für Java-Interop/Reflektion zwingend benötigt.
C. Coroutines-Fix (TermuxInstaller.kt)
Problem: Unresolved reference 'kotlinx', withContext, Dispatchers.
Task: Importiere kotlinx.coroutines.* und stelle sicher, dass Coroutine-Blöcke korrekt formatiert sind.
D. Markwon-Fix (MarkdownUtils.kt)
Problem: Schwere Fehler bei Markwon-Referenzen.
Task: - Korrigiere die Imports auf io.noties.markwon.*.
Refaktoriere den AbstractMarkwonPlugin und MarkwonVisitor Code auf die aktuelle Kotlin-Syntax der Markwon-Library (0.23.0+ kompatibel).
E. Logger.kt (String Formatting)
Problem: Unresolved reference 'string'.
Task: Java-Code wie String.format(...) wurde vermutlich falsch konvertiert. Ersetze dies durch Kotlin String-Templates ${...} oder die korrekte String.format Syntax. Fixe auch die "Argument type mismatch" Fehler (String? vs String).
Befehl an Aider:
"Lies die fix_termux_shared_errors.md. Behebe zuerst die fehlenden Abhängigkeiten in der build.gradle.kts von :termux:shared. Gehe dann die Dateien UnixConstants.kt, TermuxInstaller.kt, MarkdownUtils.kt und Logger.kt durch und korrigiere die Syntaxfehler gemäß den Anweisungen."