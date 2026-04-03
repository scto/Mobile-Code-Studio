Configure project :app
e: file:///storage/emulated/0/AndroidIDEProjects/Mobile-Code-Studio/app/build.gradle.kts:27:20: Assignment type mismatch: actual type is 'String', but 'File?' was expected.
w: file:///storage/emulated/0/AndroidIDEProjects/Mobile-Code-Studio/app/build.gradle.kts:44:5: 'fun BaseAppModuleExtension.kotlinOptions(configure: Action<DeprecatedKotlinJvmOptions>): Unit' is deprecated. Please migrate to the compilerOptions DSL. More details are here: https://kotl.in/u1r8ln.
w: file:///storage/emulated/0/AndroidIDEProjects/Mobile-Code-Studio/app/build.gradle.kts:45:9: 'var jvmTarget: String' is deprecated. Please migrate to the compilerOptions DSL. More details are here: https://kotl.in/u1r8ln.

[Incubating] Problems report is available at: file:///storage/emulated/0/AndroidIDEProjects/Mobile-Code-Studio/build/reports/problems/problems-report.html

FAILURE: Build failed with an exception.

* Where:
Build file '/storage/emulated/0/AndroidIDEProjects/Mobile-Code-Studio/app/build.gradle.kts' line: 27

* What went wrong:
Script compilation errors:

  Line 27:             path = "src/main/cpp/CMakeLists.txt"
                              ^ Assignment type mismatch: actual type is 'String', but 'File?' was expected.

  Line 44:     kotlinOptions {
               ^ 'fun BaseAppModuleExtension.kotlinOptions(configure: Action<DeprecatedKotlinJvmOptions>): Unit' is deprecated. Please migrate to the compilerOptions DSL. More details are here: https://kotl.in/u1r8ln.

  Line 45:         jvmTarget = "17"
                   ^ 'var jvmTarget: String' is deprecated. Please migrate to the compilerOptions DSL. More details are here: https://kotl.in/u1r8ln.

3 errors

