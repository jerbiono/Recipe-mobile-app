package com.example.mobproject.views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mobproject.AppScreen
import com.example.mobproject.R
import com.example.mobproject.network.RecipeCategory

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val favoriteRecipes by authViewModel.favoriteRecipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Favorites", fontSize = 20.sp) }
            )
        },
        bottomBar = {
            BottomAppBar(
                contentPadding = PaddingValues(horizontal = 16.dp),
                content = {

                    IconButton(
                        onClick = { navController.navigate(AppScreen.HomePage.name) },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home"
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
            if (favoriteRecipes.isEmpty()) {
                Text(text = "You have no favorites yet.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(favoriteRecipes.toList()) { recipeId ->
                        val recipe =
                            authViewModel.recipeCategories.value?.find { it.idCategory == recipeId }
                        recipe?.let {
                            FavoriteRecipeCard(recipe = it, authViewModel = authViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteRecipeCard(recipe: RecipeCategory, authViewModel: AuthViewModel) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {


        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter: Painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = recipe.strCategoryThumb)
                    .apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                        error(R.drawable.ic_launcher_background)
                    }).build()
            )
            Image(
                painter = painter,
                contentDescription = recipe.strCategory,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipe.strCategory ?: "Unknown",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipe.strCategoryDescription ?: "No Description",
                fontSize = 16.sp
            )
            Box {
                IconButton(
                    onClick = {
                        authViewModel.removeRecipeFromFavorite(recipe)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(80.dp)
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = "Add to Favorites",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)

                    )
                }
            }
        }
    }
}