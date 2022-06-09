package com.android.firebasechatapp.data.data_source.remote.response_handler

import com.android.firebasechatapp.R
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException

fun <T> parseRetrofitResponse(logTag: String? = null, response: Response<T>): Resource<T> {
    try {
        if (response.isSuccessful) {
            response.body()?.let { t: T ->
                return Resource.Success(t)
            } ?: run {
                return Resource.Error(UiText.StringResource(R.string.error_response_body_is_null))
            }
        } else {
            val error = APIError.getError(response.code())
            return Resource.Error(
                uiText = UiText.DynamicString(response.message()),
                throwable = error.exception
            )
        }
    } catch (ex: Exception) {
        return parseRetrofitErrorResponse(ex)
    }
}

fun <T> parseRetrofitResponseWithEmptyBody(logTag: String? = null, response: Response<T>): Resource<Unit> {
    try {
        if (response.isSuccessful) {
            return Resource.Success(Unit)
        } else {
            val error = APIError.getError(response.code())
            return Resource.Error(
                uiText = UiText.DynamicString(response.message()),
                throwable = error.exception
            )
        }
    } catch (ex: Exception) {
        return parseRetrofitErrorResponse(ex)
    }
}

fun <T> parseRetrofitErrorResponse(ex: Exception): Resource<T> {
    return when (ex) {
        is ConnectException -> {
            Resource.Error(UiText.StringResource(R.string.error_server_down))
        }
        is IOException -> {
            Resource.Error(UiText.StringResource(R.string.error_couldnt_reach_server))
        }
        else -> {
            val uiText = ex.message?.let { UiText.DynamicString(it) } ?: UiText.unknownError()
            Resource.Error(uiText)
        }
    }
}