Mobile-Code-Studio: Coding Standards
​Dependency Injection (Hilt)
​Alle Klassen und Module MÜSSEN Dagger-Hilt für Dependency Injection verwenden. Manuelle Instanziierung von Managern oder Repositories ist untersagt.
​Regeln für Aider:
​Application: Die Klasse MCSApplication im :app Modul muss mit @HiltAndroidApp annotiert sein.
​Klassen: Alle Manager, Repositories und UseCases müssen @Inject constructor() verwenden.
​Module: Jedes Modul (z.B. :core, :data) muss eine DI-Package mit einem @Module besitzen, das in der SingletonComponent::class installiert wird.
​Interfaces: Nutze @Binds in abstrakten Modulen, um Interfaces (aus :domain) an Implementierungen (in :data) zu binden.
​UI: Alle Activities und ViewModels müssen mit @AndroidEntryPoint bzw. @HiltViewModel annotiert sein.
​Keine ServiceLocator: Nutze niemals manuelle Singletons via object oder companion object, wenn eine Injection möglich ist.
​Sprache & Frameworks
​Sprache: 100% Kotlin.
​UI: Jetpack Compose (Material 3).
​Asynchronität: Kotlin Coroutines & Flow.
