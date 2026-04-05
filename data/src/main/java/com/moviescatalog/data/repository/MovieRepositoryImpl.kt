package com.moviescatalog.data.repository


import com.moviescatalog.data.local.MovieDao
import com.moviescatalog.data.local.dto.toDomainModel
import com.moviescatalog.data.local.dto.toEntity
import com.moviescatalog.data.remote.api.MovieApiService
import com.moviescatalog.data.remote.dto.toDomainModel
import com.moviescatalog.data.util.ErrorHandler
import com.moviescatalog.domain.model.Movie
import com.moviescatalog.domain.model.MovieCategory
import com.moviescatalog.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getMovies(sortBy: String, page: Int): Result<List<Movie>> {
        // 1. Try to get movies from the database first (cache-first strategy)
        val cacheResult = ErrorHandler.safeDatabaseCall {
            movieDao.getMoviesByCategory(sortBy)
        }

        cacheResult.getOrNull()?.let { cachedMovies ->
            if (cachedMovies.isNotEmpty()) {
                return Result.success(cachedMovies.map { it.toDomainModel() })
            }
        }

        // 2. If cache is empty or failed, fetch from the API
        return ErrorHandler.safeApiCall {
            val response = api.discoverMovies(sortBy, page)
            val movies = response.results.map { it.toEntity(sortBy) }

            // 3. Cache the results (ignore cache write errors)
            try {
                movieDao.insertMovies(movies)
            } catch (e: Exception) {
                // Log error but don't fail the operation
                // In a production app, you'd use a proper logging framework here
            }

            movies.map { it.toDomainModel() }
        }
    }

    override suspend fun getMovieById(movieId: Int): Result<Movie> {
        // 1. Try to get movie from cache first
        val cacheResult = ErrorHandler.safeDatabaseCall {
            movieDao.getMovieById(movieId)
        }

        cacheResult.getOrNull()?.let { cachedMovie ->
            return Result.success(cachedMovie.toDomainModel())
        }

        // 2. If not in cache, fetch from API
        return ErrorHandler.safeApiCall {
            val response = api.getMovieById(movieId)

            // 3. Cache the result (ignore cache write errors)
            try {
                movieDao.insertMovie(response.toEntity(MovieCategory.POPULAR.name))
            } catch (e: Exception) {
                // Log error but don't fail the operation
                // In a production app, you'd use a proper logging framework here
            }

            response.toDomainModel()
        }
    }


}


