package com.moviescatalog.domain.model

/**
 * Domain model representing pagination information for API requests and responses.
 *
 * @property currentPage The current page number (1-based)
 * @property totalPages The total number of pages available
 * @property totalResults The total number of results across all pages
 * @property hasNextPage True if there are more pages available
 * @property hasPreviousPage True if there are previous pages available
 */
data class PaginationInfo(
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalResults: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false
) {
    companion object {
        /**
         * Creates a PaginationInfo from API response values
         */
        fun from(
            currentPage: Int,
            totalPages: Int,
            totalResults: Int
        ): PaginationInfo {
            return PaginationInfo(
                currentPage = currentPage,
                totalPages = totalPages,
                totalResults = totalResults,
                hasNextPage = currentPage < totalPages,
                hasPreviousPage = currentPage > 1
            )
        }

        /**
         * Creates an empty PaginationInfo for initial state
         */
        fun empty(): PaginationInfo {
            return PaginationInfo()
        }
    }
}

/**
 * Wrapper class for paginated data responses
 *
 * @param T The type of items in the paginated list
 * @property items The list of items for the current page
 * @property paginationInfo Pagination metadata
 */
data class PaginatedData<T>(
    val items: List<T>,
    val paginationInfo: PaginationInfo
) {
    companion object {
        /**
         * Creates an empty paginated data response
         */
        fun <T> empty(): PaginatedData<T> {
            return PaginatedData(
                items = emptyList(),
                paginationInfo = PaginationInfo.empty()
            )
        }
    }
}

/**
 * Pagination request parameters
 *
 * @property page The page number to request (1-based)
 * @property pageSize The number of items per page (default: 20)
 */
data class PaginationRequest(
    val page: Int = 1,
    val pageSize: Int = 20
) {
    init {
        require(page > 0) { "Page must be greater than 0" }
        require(pageSize > 0) { "Page size must be greater than 0" }
    }

    /**
     * Creates the next page request
     */
    fun nextPage(): PaginationRequest {
        return copy(page = page + 1)
    }

    /**
     * Creates the previous page request
     */
    fun previousPage(): PaginationRequest {
        return if (page > 1) copy(page = page - 1) else this
    }

    companion object {
        /**
         * Default pagination request (first page, 20 items)
         */
        fun default(): PaginationRequest {
            return PaginationRequest()
        }
    }
}
