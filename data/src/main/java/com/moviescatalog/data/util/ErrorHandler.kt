package com.moviescatalog.data.util

import com.moviescatalog.domain.exception.MovieException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility object for mapping exceptions to domain-level MovieExceptions.
 * Provides consistent error handling across the data layer.
 */
object ErrorHandler {

    /**
     * Maps a throwable to a MovieException based on its type.
     *
     * @param throwable The exception to map
     * @return A MovieException representing the error
     */
    fun mapException(throwable: Throwable): MovieException {
        return when (throwable) {
            // Network connectivity errors
            is UnknownHostException -> MovieException.NetworkException(
                errorMessage = "No internet connection. Please check your network.",
                cause = throwable
            )

            is SocketTimeoutException -> MovieException.NetworkException(
                errorMessage = "Connection timeout. Please try again.",
                cause = throwable
            )

            is IOException -> MovieException.NetworkException(
                errorMessage = "Network error occurred. Please try again.",
                cause = throwable
            )

            // HTTP errors from Retrofit
            is HttpException -> {
                val statusCode = throwable.code()
                val errorMessage = when (statusCode) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please check your API key."
                    403 -> "Access forbidden."
                    404 -> "Resource not found."
                    429 -> "Too many requests. Please try again later."
                    in 500..599 -> "Server error. Please try again later."
                    else -> "API error occurred (Status: $statusCode)"
                }
                MovieException.ApiException(
                    statusCode = statusCode,
                    errorMessage = errorMessage,
                    cause = throwable
                )
            }

            // JSON parsing errors
            is com.google.gson.JsonParseException,
            is com.google.gson.JsonSyntaxException -> MovieException.DataException(
                errorMessage = "Failed to parse server response.",
                cause = throwable
            )

            // Database errors
            is android.database.sqlite.SQLiteException -> MovieException.DatabaseException(
                errorMessage = "Database error occurred.",
                cause = throwable
            )

            // MovieException instances (already domain exceptions)
            is MovieException -> throwable

            // Unknown errors
            else -> MovieException.UnknownException(
                errorMessage = throwable.message ?: "An unexpected error occurred.",
                cause = throwable
            )
        }
    }

    /**
     * Executes a suspend function and wraps the result in a Result type with proper error mapping.
     *
     * @param block The suspend function to execute
     * @return Result.success with data or Result.failure with a mapped MovieException
     */
    suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    /**
     * Executes a database operation and wraps the result in a Result type with proper error mapping.
     *
     * @param block The database operation to execute
     * @return Result.success with data or Result.failure with a mapped MovieException
     */
    fun <T> safeDatabaseCall(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }
}
