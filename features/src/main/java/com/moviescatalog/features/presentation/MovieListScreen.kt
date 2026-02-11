package com.moviescatalog.features.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.moviescatalog.domain.model.Movie
import com.moviescatalog.domain.model.MovieCategory
import com.moviescatalog.features.R
import com.moviescatalog.features.viewmodel.MovieListViewModel

@Composable
fun MovieListScreen(
    navController: NavController,
    padding: PaddingValues,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val moviesByCategory = viewModel.moviesByCategory
    val isLoading by viewModel.isLoading.collectAsState()

    // Show loading indicator while initial data is being fetched
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()



        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(MovieCategory.entries) { category ->
            val movies by moviesByCategory[category]?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
            val isCategoryLoading by viewModel.isLoadingByCategory[category]?.collectAsState() ?: remember { mutableStateOf(false) }

            MovieSection(
                title = stringResource(id = category.titleRes),
                movies = movies,
                isLoading = isCategoryLoading,
                onLoadMore = { viewModel.fetchNextPage(category) },
                navController = navController
            )
        }
    }
}

@Composable
fun MovieSection(
    title: String,
    movies: List<Movie>,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(movies) { index, movie ->
                // Trigger pagination when the last item is visible
                if (index == movies.lastIndex) {
                    LaunchedEffect(Unit) {
                        onLoadMore()
                    }
                }

                MovieCard(movie = movie, navController = navController)
            }

            // Show a small loader while the next page is being fetched
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(225.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable {
                navController.navigate("movie_detail/${movie.id}")
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .placeholder(R.drawable.no_poster_image)
                    .error(R.drawable.no_poster_image)
                    .build()
            ),
            contentDescription = movie.title ?: "Movie Poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(150.dp)
                .height(225.dp)
        )

    }
}

