Aider Task: Implement Stable Termux Installer & Native Bridge

1. Objective

Implement the TermuxInstaller in Kotlin for reliable bootstrap setup and configure the native C++ bridge (PTY) using the provided CMake structure. Focus on stable ZIP extraction and symlink restoration.

2. Native Build Configuration

Create app/src/main/cpp/CMakeLists.txt with the following content:

cmake_minimum_required(VERSION 3.22.1)

project("vcspace")



add_library(${CMAKE_PROJECT_NAME} SHARED

       vcspace.cpp

       terminal/executor.cpp

)



target_link_libraries(${CMAKE_PROJECT_NAME}

       android

       log

)



Note: Create placeholder vcspace.cpp and terminal/executor.cpp files in the cpp directory if they don't exist.

3. TermuxInstaller Implementation (:core)

Create the TermuxInstaller class in com.scto.mcs.core.setup.

A. ABI Detection & URL Mapping

Map the following URLs based on Build.SUPPORTED_ABIS:

* aarch64: https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023/bootstrap-aarch64.zip

* arm: https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-16.12.2023/bootstrap-arm.zip

* x86_64: https://github.com/Visual-Code-Space/terminal-packages/releases/download/bootstrap-x86_64.zip

B. Extraction & Symlink Handling (Critical)

1. Extraction: Extract the selected ZIP to TermuxConstants.PREFIX.

2. Symlink Restoration: Standard Java ZipInputStream does not preserve symlink metadata. You MUST implement a post-extraction phase.

   * Instruction: Use android.system.Os.symlink(target, link) or Runtime.getRuntime().exec("ln -s target link") to manually recreate all necessary symlinks for the Termux environment to function.

   * Reference the internal bootstrap manifest if available, or handle common links in $PREFIX/bin.

C. Environment Initialization

After extraction, create a setup.sh in $PREFIX/bin to:

* Set HOME, PREFIX, and PATH.

* Ensure all binaries in $PREFIX/bin have executable permissions (chmod 700).

4. DI & UI Feedback

* Provide TermuxInstaller via Hilt as a @Singleton.

* Implement a Flow<InstallStatus> to report: Downloading(progress), Extracting, FixingSymlinks, Completed, Error(message).

5. Coding Standards

* Language: Kotlin.

* Error Handling: Use exponential backoff for downloads.

* Logging: Detailed logs for each symlink created to aid debugging.

Instruction for Aider: "Implementiere die TermuxInstaller Klasse und die CMakeLists.txt wie beschrieben. Achte besonders auf den Hinweis zu den Symlinks: Nutze Os.symlink oder Runtime.exec um Symlinks nach dem Entpacken wiederherzustellen, da das Terminal sonst keine Befehle finden wird."
