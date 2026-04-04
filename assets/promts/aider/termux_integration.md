Aider Task: Deep Termux Integration for Mobile-Code-Studio (MCS)

1. Project Objective

Implement a fully functional Termux-based terminal environment within the com.scto.mcs package. This includes NDK configuration, bootstrap installation using specific URLs, environment mapping, and UI integration.

2. Build & NDK Configuration (:app & :core)

* NDK Setup: Update app/build.gradle.kts:

   * Set ndkVersion = "26.1.10909125" (or latest LTS).

   * Configure externalNativeBuild { cmake { path = "src/main/cpp/CMakeLists.txt" } } for PTY/JNI support.

   * Set ndk { abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64")) }.

* Dependencies: Add com.termux:termux-view (adapted for MCS) to the :feature:terminal module.

3. Termux Constants & Environment Mapping (:core)

* Create TermuxConstants.kt in com.scto.mcs.core.constants:

   * PREFIX = "/data/data/com.scto.mcs/files/usr"

   * HOME = "/data/data/com.scto.mcs/files/home"

   * TEMP = "/data/data/com.scto.mcs/files/usr/tmp"

* Bootstrap URLs:

   * AARCH64: https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023/bootstrap-aarch64.zip

   * ARM: https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023/bootstrap-arm.zip

   * X86_64: https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-x86_64.zip

4. Termux Setup Logic & Installer (:core & :feature:setup)

* TermuxInstaller (Kotlin): - Implement ABI detection logic to select the correct Bootstrap URL.

   * Implement downloadBootstrap() using OkHttp or Cronet.

   * Implement installBootstrap(): Extract ZIP to PREFIX.

   * Symlink Handling: Since standard Java ZIP extraction doesn't preserve symlinks, implement a native helper or use Runtime.exec("ln -s ...") logic to ensure the environment is functional.

* Environment Setup: Create a setupEnvironment() method that exports:

   * PATH=$PREFIX/bin:$PATH

   * LD_LIBRARY_PATH=$PREFIX/lib

   * HOME=$HOME

   * TERM=xterm-256color

5. Terminal UI & Activity (:feature:terminal)

* TerminalActivity: Dedicated Activity for the terminal session to ensure process persistence.

* TerminalScreen (Compose):

   * AndroidView wrapper for TerminalView.

   * Integration with TerminalViewModel and TerminalSession.

   * Extra Keys Row: Implement a row for Ctrl, Alt, Tab, Esc, and Arrow keys.

6. Settings Expansion (:feature:settings)

* TermuxSettingsScreen: A sub-screen for terminal-specific settings:

   * Font Size, Font Style, Color Schemes.

   * "Environment Reset" (Deletes PREFIX and triggers re-setup).

* SettingsViewModel: Add state management for Termux settings using DataStore.

7. Implementation Steps for Aider

1. Infrastructure: Update build.gradle.kts and create TermuxConstants.kt.

2. Core Logic: Implement the TermuxInstaller with the provided GitHub URLs.

3. UI: Build the TerminalActivity and Compose TerminalScreen.

4. Settings: Add the Termux configuration UI and ViewModel logic.

Instruction for Aider: "Lies die termux_master_integration.md sorgfältig. Implementiere zuerst die NDK-Konfiguration und die Konstanten. Achte penibel darauf, dass alle Pfade auf 'com.scto.mcs' gemappt sind. Nutze für die Installation die bereitgestellten Visual-Code-Space URLs."
