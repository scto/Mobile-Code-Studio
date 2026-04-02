#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>

#define LOG_TAG "VCSpaceNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_scto_mcs_core_NativeBridge_stringFromJNI(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("VCSpace Native Bridge Initialized");
}

extern "C" JNIEXPORT jint JNICALL
Java_com_scto_mcs_core_NativeBridge_createSymlink(JNIEnv* env, jobject /* this */, jstring target, jstring linkpath) {
    const char* target_str = env->GetStringUTFChars(target, nullptr);
    const char* linkpath_str = env->GetStringUTFChars(linkpath, nullptr);

    int result = symlink(target_str, linkpath_str);

    if (result != 0) {
        LOGE("Failed to create symlink: %s -> %s (errno: %d)", target_str, linkpath_str, errno);
    } else {
        LOGI("Successfully created symlink: %s -> %s", target_str, linkpath_str);
    }

    env->ReleaseStringUTFChars(target, target_str);
    env->ReleaseStringUTFChars(linkpath, linkpath_str);

    return result;
}
