package com.android.firebasechatapp.data.data_source.remote.response_handler

import androidx.annotation.StringRes
import com.android.firebasechatapp.R

enum class APIError(
    val code: Int,
    val exception: APIException,
    @StringRes val messageResourceId: Int
) {
    BAD_REQUEST_400(
        400,
        APIException.BadRequest400Exception,
        R.string.create_user_response_code_400_message
    ),
    ROLE_UNAUTHORIZED_401(
        401,
        APIException.RoleUnauthorized401Exception,
        R.string.create_user_response_code_401_message
    ),
    ROUTE_NOT_EXIST_404(
        404,
        APIException.RouteNotExist404Exception,
        R.string.create_user_response_code_404_message
    ),
    DUPLICATE_USER_409(
        409,
        APIException.DuplicateUser409Exception,
        R.string.create_user_response_code_409_message
    ),
    INTERNAL_SERVER_API_ERROR_500(
        500,
        APIException.InternalServerError500Exception,
        R.string.create_user_response_code_500_message
    ),
    UNKNOWN_API_ERROR(
        -1,
        APIException.UnknownErrorException,
        R.string.error_message_unknown
    );

    companion object {
        fun getError(code: Int): APIError {
            return values().firstOrNull { it.code == code } ?: UNKNOWN_API_ERROR
        }

        fun getError(exception: Throwable): APIError {
            values().forEach {
                if (it.exception == exception) {
                    return it
                }
            }
            return UNKNOWN_API_ERROR
        }
    }
}