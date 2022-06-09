package com.android.firebasechatapp.data.data_source.remote.response_handler


sealed class APIException : Exception() {
    object BadRequest400Exception : APIException()
    object RoleUnauthorized401Exception : APIException()
    object RouteNotExist404Exception : APIException()
    object DuplicateUser409Exception : APIException()
    object InternalServerError500Exception : APIException()
    object UnknownErrorException : APIException()
}

sealed class DevicesApiException : Exception() {
    object DuplicateUserDeviceException : DevicesApiException()
}

sealed class ReadingUploadingApiException : Exception() {
    object UploadingEmptyReadingList : ReadingUploadingApiException()
}