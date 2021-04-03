package com.samoylenko.kt12.api

import com.samoylenko.kt12.uimodel.ApiError
import com.samoylenko.kt12.uimodel.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection

class PostInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request())
            .let {response ->
                when{
                    response.isSuccessful -> response
                    response.code == HttpURLConnection.HTTP_INTERNAL_ERROR -> throw ApiException(ApiError.ServerError)
                    else -> throw ApiException(ApiError.UnknownError)
                }
            }
}