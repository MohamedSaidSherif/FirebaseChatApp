package com.android.firebasechatapp.resource

typealias SimpleResource = Resource<Unit>

sealed class Resource<out T>(open val data: T? = null) {

    class Success<T>(override val data: T) : Resource<T>(data)

    class Error(
        val uiText: UiText = UiText.DynamicString(""),
        val throwable: Throwable? = null
    ) : Resource<Nothing>()

    class Loading<T>(data: T? = null) : Resource<T>(data)
}

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        val uiText = e.message?.let { UiText.DynamicString(it) } ?: UiText.unknownError()
        Resource.Error(uiText)
    }
}

/**
 * `true` if [Result] is of type [Resource.Success] & holds non-null [Resource.Success.data].
 */
val Resource<*>.isSucceeded
    get() = this is Resource.Success && data != null


/**
 * `true` if [Result] is of type [Resource.Loading].
 */
val Resource<*>.isLoading
    get() = this is Resource.Loading


val Resource<*>.error
    get() = (this as Resource.Error).throwable


