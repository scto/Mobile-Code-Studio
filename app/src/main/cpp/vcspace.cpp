#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>
#include <cerrno>
#include <cstdio>
#include <sys/stat.h>

// Include Termux JNI headers if needed for shared functionality
// #include "termux.h" 

#define LOG_TAG "VCSpaceNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// RAII wrapper for JNI string to ensure ReleaseStringUTFChars is called
class JniString {
public:
    JniString(JNIEnv* env, jstring jstr) : env_(env), jstr_(jstr) {
        if (jstr_ != nullptr) {
            str_ = env_->GetStringUTFChars(jstr_, nullptr);
        } else {
            str_ = nullptr;
        }
    }
    ~JniString() {
        if (str_) {
            env_->ReleaseStringUTFChars(jstr_, str_);
        }
    }
    const char* c_str() const { return str_ ? str_ : ""; }
private:
    JNIEnv* env_;
    jstring jstr_;
    const char* str_;
};

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL
Java_com_scto_mcs_core_NativeBridge_stringFromJNI(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("VCSpace Native Bridge Initialized");
}

JNIEXPORT jint JNICALL
Java_com_scto_mcs_core_NativeBridge_nativeCreateSymlink(JNIEnv* env, jobject /* this */, jstring target, jstring linkpath) {
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

JNIEXPORT jboolean JNICALL
Java_com_scto_mcs_core_NativeBridge_nativeCheckSymlinkSupport(JNIEnv* env, jobject /* this */, jstring testDir) {
    JniString dir_str(env, testDir);
    std::string testFile = std::string(dir_str.c_str()) + "/.symlink_test";
    std::string linkFile = std::string(dir_str.c_str()) + "/.symlink_test_link";

    // Create a dummy file
    FILE* f = fopen(testFile.c_str(), "w");
    if (!f) return JNI_FALSE;
    fclose(f);

    // Try to create a symlink
    int result = symlink(testFile.c_str(), linkFile.c_str());
    bool supported = (result == 0);

    // Cleanup
    unlink(testFile.c_str());
    if (supported) {
        unlink(linkFile.c_str());
    }

    return supported ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_scto_mcs_core_NativeBridge_nativeCheckFileSystemCapabilities(JNIEnv* env, jobject /* this */, jstring testDir) {
    JniString dir_str(env, testDir);
    std::string dir = dir_str.c_str();
    
    int capabilities = 0;

    // 1. Check Writable
    std::string testFile = dir + "/.write_test";
    FILE* f = fopen(testFile.c_str(), "w");
    if (f) {
        fclose(f);
        unlink(testFile.c_str());
        capabilities |= 2; // Bit 1 set
    }

    // 2. Check Symlinks
    std::string symlinkTestFile = dir + "/.symlink_test";
    std::string symlinkLinkFile = dir + "/.symlink_test_link";
    
    // Create dummy file for symlink test
    FILE* f2 = fopen(symlinkTestFile.c_str(), "w");
    if (f2) {
        fclose(f2);
        int result = symlink(symlinkTestFile.c_str(), symlinkLinkFile.c_str());
        if (result == 0) {
            capabilities |= 1; // Bit 0 set
            unlink(symlinkLinkFile.c_str());
        }
        unlink(symlinkTestFile.c_str());
    }

    return capabilities;
}

JNIEXPORT jint JNICALL
Java_com_scto_mcs_core_NativeBridge_nativeCheckPermissionAccess(JNIEnv* env, jobject /* this */, jstring path) {
    JniString path_str(env, path);
    if (access(path_str.c_str(), R_OK | W_OK) == 0) {
        return 0;
    }
    return errno;
}

#ifdef __cplusplus
}
#endif
