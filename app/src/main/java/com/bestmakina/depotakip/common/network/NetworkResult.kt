package com.bestmakina.depotakip.common.network

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data = data)
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data = data, message = message)
    class Loading<T>(data: T? = null) : NetworkResult<T>(data = data)
}