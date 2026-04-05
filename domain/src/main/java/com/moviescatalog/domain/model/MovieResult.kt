package com.moviescatalog.domain.model

import com.moviescatalog.domain.exception.MovieException

/**
 * A wrapper class for handling success and error states in the domain layer.
 * Provides a type-safe way to handle operations that can succeed or fail.
 *
 * @param T The type of data returned on success
 */
sealed class MovieResult<out T> {
    /**
     * Represents a successful operation with data
     */
    data class Success<T>(val data: T) : MovieResult<T>()

    /**
     * Represents a failed operation with an error
     */
    data class Error(val exception: MovieException) : MovieResult<Nothing>()

    /**
     * Represents a loading state
     */
    object Loading : MovieResult<Nothing>()

    /**
     * Returns true if this result is a success
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this result is an error
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this result is loading
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the data if success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the exception if error, null otherwise
     */
    fun exceptionOrNull(): MovieException? = when (this) {
        is Error -> exception
        else -> null
    }
}

/**
 * Extension function to map success data to another type
 */
inline fun <T, R> MovieResult<T>.map(transform: (T) -> R): MovieResult<R> {
    return when (this) {
        is MovieResult.Success -> MovieResult.Success(transform(data))
        is MovieResult.Error -> this
        is MovieResult.Loading -> this
    }
}

/**
 * Extension function to handle success and error cases
 */
inline fun <T> MovieResult<T>.onSuccess(action: (T) -> Unit): MovieResult<T> {
    if (this is MovieResult.Success) {
        action(data)
    }
    return this
}

/**
 * Extension function to handle error cases
 */
inline fun <T> MovieResult<T>.onError(action: (MovieException) -> Unit): MovieResult<T> {
    if (this is MovieResult.Error) {
        action(exception)
    }
    return this
}
