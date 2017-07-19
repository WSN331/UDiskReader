#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_frz_jnitest_util_JniUtil_testJni(JNIEnv *env, jclass type) {

    // TODO

    std::string hello = "Hello from JNI";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_frz_jnitest_MainActivity_jniTest(JNIEnv *env, jobject instance) {

    // TODO

    std::string hello = "Hello from JNI";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
jstring
Java_com_example_frz_jnitest_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++++";
    return env->NewStringUTF(hello.c_str());
}
