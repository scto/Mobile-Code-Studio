#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>
#include <cerrno>

#define LOG_TAG "VCSpaceNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// RAII wrapper for JNI string to ensure ReleaseStringUTFChars is called
class JniString {
public:
    JniString(JNIEnv* env, jstring jstr) : env_(env), jstr_(jstr) {
        str_ = env_->GetStringUTFChars(jstr_, nullptr);
    }
    ~JniString() {
        if (str_) {
            env_->ReleaseStringUTFChars(jstr_, str_);
        }
    }
    const char* c_str() const { return str_; }
private:
    JNIEnv* env_;
    jstring jstr_;
    const char* str_;
};

extern "C" JNIEXPORT jstring JNICALL
Java_com_scto_mcs_core_NativeBridge_stringFromJNI(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("VCSpace Native Bridge Initialized");
}

extern "C" JNIEXPORT jint JNICALL
Java_com_scto_mcs_core_NativeBridge_createSymlink(JNIEnv* env, jobject /* this */, jstring target, jstring linkpath) {
    JniString target_str(env, target);
    JniString linkpath_str(env, linkpath);

    int result = symlink(target_str.c_str(), linkpath_str.c_str());

    if (result != 0) {
        LOGE("Failed to create symlink: %s -> %s (errno: %d)", target_str.c_str(), linkpath_str.c_str(), errno);
    } else {
        LOGI("Successfully created symlink: %s -> %s", target_str.c_str(), linkpath_str.c_str());
    }

    return result;
}
