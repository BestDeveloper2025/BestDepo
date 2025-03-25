package com.bestmakina.depotakip.common.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", Credentials.basic("Best Developer", "bestmak"))
            .build()
        return chain.proceed(request)
    }
}