package com.zeus.pseudocharlesdemo.core.data

import okhttp3.Interceptor

interface NetworkInterceptorProvider {
    fun interceptors(): List<Interceptor>
}
