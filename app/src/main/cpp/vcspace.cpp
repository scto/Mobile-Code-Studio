#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "VCSpaceNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_scto_mcs_core_NativeBridge_stringFromJNI(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("VCSpace Native Bridge Initialized");
}
