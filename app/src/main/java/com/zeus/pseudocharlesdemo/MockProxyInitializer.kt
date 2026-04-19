package com.zeus.pseudocharlesdemo

import android.content.Context
import com.zeus.pseudocharles.PseudoCharles

// Single entry point for both debug and release. In release, the -noop artifact
// makes this a cheap no-op (PseudoCharles.isEnabled() stays false) so the same
// call site works for every build variant.
fun initializeMockProxy(context: Context) {
    PseudoCharles.interceptor(context)
}
