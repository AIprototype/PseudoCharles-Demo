package com.zeus.pseudocharlesdemo.core.domain

sealed interface DataError : com.zeus.pseudocharlesdemo.core.domain.Error {

    enum class Network : DataError {
        NO_INTERNET,
        REQUEST_TIMEOUT,
        SERVER_ERROR,
        SERIALIZATION,
        TOO_MANY_REQUESTS,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }
}
