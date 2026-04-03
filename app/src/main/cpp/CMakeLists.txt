cmake_minimum_required(VERSION 3.22.1)

project("vcspace")

# Add the Termux JNI headers to the include path so that native code can access them
include_directories(../../../termux/emulator/src/main/jni)

add_library(${CMAKE_PROJECT_NAME} SHARED
       vcspace.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
       android
       log
)
