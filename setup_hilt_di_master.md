Master Task: Hilt Dependency Injection Setup (All Modules)

🎯 Zielsetzung

Implementiere ein konsistentes Hilt-DI-System über alle Module hinweg, um "Missing Bindings" Fehler zu vermeiden und eine saubere Kommunikation zwischen den Layern (Data -> Domain -> UI) zu ermöglichen.

Phase 1: Die Application-Basis (:app)

Ziel: Initialisierung des Hilt-Graphen.

1. MCSApplication.kt:

   * Annotiere die Klasse mit @HiltAndroidApp.

2. MainActivity.kt:

   * Annotiere die Klasse mit @AndroidEntryPoint.

3. AppModule.kt (in :app:di):

   * Erstelle ein @Module / @InstallIn(SingletonComponent::class), um globale App-Abhängigkeiten (Context, etc.) bereitzustellen, falls nötig.

Phase 2: Infrastruktur & Daten (:core)

Ziel: Bereitstellung von Repositories und Hilfsklassen.

1. CoreDataModule.kt (in :core:data:di):

   * Erstelle Bindings für alle Repositories. Nutze @Binds für Interfaces (z.B. RepositoryImpl zu Repository).

   * @InstallIn(SingletonComponent::class).

2. CoreUtilsModule.kt (in :core:utils:di):

   * Stelle Klassen wie Environment, FileSystemUtils oder PreferenceManager via @Provides zur Verfügung.

3. UseCase Injection (:core:domain):

   * Stelle sicher, dass ALLE UseCase-Klassen (z.B. CloneRepositoryUseCase) eine @Inject constructor() Deklaration haben.

Phase 3: Termux Subsystem (:core:termux)

Ziel: Integration der komplexen Termux-Logik in den DI-Graphen.

1. TermuxModule.kt (in :core:termux:shared/application:di):

   * Stelle die TerminalSession Verwaltung und den TermuxInstaller bereit.

   * Da viele Termux-Klassen den Context benötigen, stelle sicher, dass @ApplicationContext verwendet wird.

2. JNI & Emulator:

   * Falls der Emulator oder die Bridge Instanzen benötigt werden, erstelle entsprechende @Provides-Methoden.

Phase 4: Feature ViewModels (:feature:*)

Ziel: Verbindung der UI mit der Geschäftslogik.

1. ViewModel-Standard:

   * Alle ViewModels (z.B. TerminalViewModel, EditorViewModel) müssen mit @HiltViewModel annotiert sein und die UseCases via @Inject constructor() erhalten.

2. Activity/Fragment/Screen EntryPoints:

   * Jeder Screen (Composable oder Fragment) muss in einer Umgebung laufen, die mit @AndroidEntryPoint markiert ist.

🛠 Spezifische Anweisungen für Aider

* Keine Field Injection: Nutze ausschließlich Constructor Injection (@Inject constructor()).

* Scopes: Nutze @Singleton nur dort, wo es zwingend notwendig ist (z.B. Repositories oder Session-Manager).

* Fehlerbehebung: Wenn Aider auf "Circular Dependency" stößt (z.B. Modul A braucht B und B braucht A), verschiebe die gemeinsame Schnittstelle in :core:common.

* Package-Names: Alle DI-Module müssen im Paket com.scto.mcs.[modulname].di liegen.

Befehl an Aider: "Arbeite den setup_hilt_di_master.md Plan ab. Beginne mit der MCSApplication im :app Modul und arbeite dich dann durch :core:data und :core:domain, bevor du die ViewModels in den :feature Modulen konfigurierst."
