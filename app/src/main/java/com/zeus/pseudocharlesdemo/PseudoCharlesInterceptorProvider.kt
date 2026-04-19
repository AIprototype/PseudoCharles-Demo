package com.zeus.pseudocharlesdemo

import android.content.Context
import com.zeus.pseudocharles.PseudoCharles
import com.zeus.pseudocharlesdemo.core.data.NetworkInterceptorProvider
import okhttp3.Interceptor

class PseudoCharlesInterceptorProvider(context: Context) : NetworkInterceptorProvider {
    private val pseudoCharlesInterceptor: Interceptor = PseudoCharles.interceptor(context)

    override fun interceptors(): List<Interceptor> = listOf(pseudoCharlesInterceptor)
}
