📱 Mobile-Code-Studio (MCS)

Mobile-Code-Studio ist eine leistungsstarke, modulare IDE für Android, die auf moderner Clean Architecture basiert. Sie kombiniert die Flexibilität von Termux mit der Präzision des Sora Editors und einer robusten JGit-Integration.

🏗 Architektur-Übersicht

Das Projekt folgt einer strengen Multi-Module-Struktur, um Skalierbarkeit und Testbarkeit zu gewährleisten.

graph TD

   %% UI & App Layer

   subgraph UI_Layer [UI & App Layer]

       App[":app (Main Entry)"]

       Features[":feature:* (Dashboard, Editor, Terminal, etc.)"]

   end



   %% Business Logic Layer

   subgraph Domain_Layer [Domain Layer]

       Domain[":core:domain (UseCases & Models)"]

   end



   %% Data & Subsystems

   subgraph Data_Layer [Data & Subsystems]

       Data[":core:data (Repositories)"]

       Termux[":termux:* (Native Terminal Emulator)"]

   end



   %% Infrastructure Layer

   subgraph Infrastructure [Infrastructure & Core]

       Common[":core:common"]

       Res[":core:resources (Zentrale Ressourcen)"]

       Utils[":core:utils"]

   end



   %% Dependencies

   App --> Features

   Features --> Domain

   Features --> Res

   Features --> Common

   Domain --> Data

   Data --> Termux

   Data --> Common

   Termux --> Common

   

   style App fill:#007ACC,stroke:#fff,stroke-width:2px,color:#fff

   style Features fill:#005FB8,stroke:#fff,color:#fff

   style Domain fill:#2D2D2D,stroke:#007ACC,color:#fff

   style Data fill:#1E1E1E,stroke:#444,color:#fff

   style Termux fill:#D32F2F,stroke:#fff,color:#fff

   style Res fill:#388E3C,stroke:#fff,color:#fff



🚀 Kern-Features

* 🛠 Volle Terminal-Integration: Echte Termux-Umgebung mit Paketverwaltung.

* 📝 Sora Editor: High-Performance Code-Editor mit TextMate-Syntax-Highlighting.

* 🌿 Git Support: Native Git-Operationen (Clone, Commit, Push) via JGit.

* 📦 Modernes Setup: Geführtes Onboarding zur Installation von JDK (17/21) und Android Build-Tools.

🛠 Tech Stack

* Sprache: 100% Kotlin

* UI: Jetpack Compose mit Material 3

* DI: Dagger-Hilt

* Build System: Gradle (Kotlin DSL) mit Version Catalogs

* Concurrency: Kotlin Coroutines & Flow

🛠 Installation & Setup

1. Repository klonen.

2. Projekt in AndroidIDE oder Android Studio öffnen.

3. Den assembleDebug Task ausführen.

4. Beim ersten Start dem Onboarding folgen, um die Terminal-Umgebung zu initialisieren.

© 2025 Mobile-Code-Studio Team. Licensed under GPL-3.0.
