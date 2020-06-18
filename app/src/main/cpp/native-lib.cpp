#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_crypto_delta_exchange_openexchange_utils_Native_getDeltaExchangeBaseUrl(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "https://api.delta.exchange/";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_crypto_delta_exchange_openexchange_utils_Native_getDeltaExchangeBaseWebSocketUrl(
        JNIEnv *env,
        jobject /* this */) {
    std::string url = "wss://api.delta.exchange:2096/";
    return env->NewStringUTF(url.c_str());
}