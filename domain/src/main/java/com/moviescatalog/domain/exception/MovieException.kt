package com.moviescatalog.domain.exception

/**
 * Base sealed class for all domain-level exceptions in the MovieCatalog app.
 * This hierarchy provides type-safe error handling throughout the application layers.
 */
sealed class MovieException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Network-related errors (no connection, timeout, etc.)
     */
    data class NetworkException(
        val errorMessage: String = "Network error occurred",
        val cause: Throwable? = null
    ) : MovieException(errorMessage, cause)

    /**
     * API-related errors (4xx, 5xx status codes)
     */
    data class ApiException(
        val statusCode: Int,
        val errorMessage: String = "API error occurred",
        val cause: Throwable? = null
    ) : MovieException("API Error ($statusCode): $errorMessage", cause)

    /**
     * Data parsing/serialization errors
     */
    data class DataException(
        val errorMessage: String = "Data parsing error occurred",
        val cause: Throwable? = null
    ) : MovieException(errorMessage, cause)

    /**
     * Database/local storage errors
     */
    data class DatabaseException(
        val errorMessage: String = "Database error occurred",
        val cause: Throwable? = null
    ) : MovieException(errorMessage, cause)

    /**
     * Resource not found errors (404, empty results, etc.)
     */
    data class NotFoundException(
        val errorMessage: String = "Resource not found",
        val cause: Throwable? = null
    ) : MovieException(errorMessage, cause)

    /**
     * Unknown or unexpected errors
     */
    data class UnknownException(
        val errorMessage: String = "An unknown error occurred",
        val cause: Throwable? = null
    ) : MovieException(errorMessage, cause)
}
