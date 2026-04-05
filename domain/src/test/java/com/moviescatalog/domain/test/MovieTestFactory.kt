package com.moviescatalog.domain.test

import com.moviescatalog.domain.model.Movie
import com.moviescatalog.domain.model.MovieCategory

/**
 * Test data factory for creating mock Movie objects for testing purposes.
 * Provides convenient builder methods for creating test data with customizable fields.
 */
object MovieTestFactory {

    /**
     * Creates a sample Movie with default values for testing
     */
    fun createMovie(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "This is a test movie overview",
        posterPath: String? = "/test_poster.jpg",
        backdropPath: String? = "/test_backdrop.jpg",
        releaseDate: String = "2024-01-01",
        voteAverage: Double = 7.5,
        voteCount: Int = 1000,
        popularity: Double = 100.0,
        originalLanguage: String = "en",
        adult: Boolean = false,
        video: Boolean = false,
        genreIds: List<Int> = listOf(28, 12, 878) // Action, Adventure, Science Fiction
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = popularity,
            originalLanguage = originalLanguage,
            adult = adult,
            video = video,
            genreIds = genreIds
        )
    }

    /**
     * Creates a list of sample movies for testing
     */
    fun createMovieList(count: Int = 5): List<Movie> {
        return (1..count).map { index ->
            createMovie(
                id = index,
                title = "Test Movie $index",
                overview = "Overview for test movie $index",
                voteAverage = 5.0 + (index % 5),
                popularity = 50.0 + (index * 10)
            )
        }
    }

    /**
     * Creates a popular movie for testing
     */
    fun createPopularMovie(id: Int = 1): Movie {
        return createMovie(
            id = id,
            title = "Popular Movie $id",
            voteAverage = 8.5,
            voteCount = 5000,
            popularity = 500.0
        )
    }

    /**
     * Creates a top-rated movie for testing
     */
    fun createTopRatedMovie(id: Int = 1): Movie {
        return createMovie(
            id = id,
            title = "Top Rated Movie $id",
            voteAverage = 9.0,
            voteCount = 10000,
            popularity = 300.0
        )
    }

    /**
     * Creates a movie with minimal information for testing edge cases
     */
    fun createMinimalMovie(id: Int = 1): Movie {
        return createMovie(
            id = id,
            title = "Minimal Movie",
            overview = "",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            voteCount = 0,
            popularity = 0.0
        )
    }
}
