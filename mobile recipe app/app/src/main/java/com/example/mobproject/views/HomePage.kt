package com.example.mobproject.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mobproject.AppScreen
import com.example.mobproject.R
import com.example.mobproject.network.RecipeCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val authState = authViewModel.authState.collectAsState()
    val recipeCategories by authViewModel.recipeCategories.collectAsState()
    var searchText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.Unauthtentificated -> navController.navigate(AppScreen.LoginPage.name)
            else -> Unit
        }
    }
    LaunchedEffect(searchText) {
        coroutineScope.launch {
            listState.scrollToItem(0)
        }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text(text = "Available Recipes", fontSize = 20.sp) }
            )
        },
        bottomBar = {
            BottomAppBar(
                contentPadding = PaddingValues(horizontal = 16.dp),
                content = {
                    IconButton(
                        onClick = { navController.navigate(AppScreen.FavoriteListPage.name) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.liste),
                            contentDescription = "Favorites"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { navController.navigate(AppScreen.AddCommentsPage.name) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_comment),
                            contentDescription = "Add comment"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { authViewModel.signout() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.signout),
                            contentDescription = "Sign Out"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(

                        onClick = {
                            navController.navigate(AppScreen.AboutPage.name)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = "About"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(text = "Search recipes...") },
                singleLine = true
            )

            val categories = recipeCategories ?: emptyList()
            val filteredCategories = categories.filter {
                it.strCategory?.contains(searchText, ignoreCase = true) == true
            }

            if (filteredCategories.isEmpty()) {
                Text(
                    text = "No categories available",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                authViewModel.refreshFavorites()
                LazyColumn(
                    state = listState
                ) {
                    items(filteredCategories) { category ->
                        CategoryCard(category = category, authViewModel = authViewModel)
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryCard(category: RecipeCategory, authViewModel: AuthViewModel) {

    val favoriteRecipes by authViewModel.favoriteRecipes.collectAsState()

    val isAdded = favoriteRecipes.contains(category.idCategory)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val painter: Painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = category.strCategoryThumb)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                            error(R.drawable.ic_launcher_background)
                        }).build()
                )
                Image(
                    painter = painter,
                    contentDescription = category.strCategory,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.strCategory ?: "Unknown",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = category.strCategoryDescription ?: "No Description",
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (!isAdded) {
                IconButton(
                    onClick = {
                        authViewModel.saveRecipe(category)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(80.dp)
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.no_favorite),
                        contentDescription = "Add to Favorites",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)

                    )
                }
            } else {
                IconButton(
                    onClick = {
                        authViewModel.removeRecipeFromFavorite(category)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(80.dp)
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = "Add to Favorites",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)

                    )
                }
            }
        }
    }
}