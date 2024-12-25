package com.example.mobproject

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobproject.views.AboutPage
import com.example.mobproject.views.AddCommentPage
import com.example.mobproject.views.AuthViewModel
import com.example.mobproject.views.FavoriteListPage
import com.example.mobproject.views.HomePage
import com.example.mobproject.views.ListCommentsPage
import com.example.mobproject.views.LoginPage
import com.example.mobproject.views.SignupPage

enum class AppScreen(@StringRes val title: Int) {
    HomePage(title = R.string.app_name),
    LoginPage(title = R.string.login_page),
    SignupPage(title = R.string.signup_page),
    AboutPage(title = R.string.about),
    FavoriteListPage(R.string.favorite_list),
    AddCommentsPage(R.string.add_comment_page),
    ListCommentsPage(R.string.list_comments_page)

}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun StartApp(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreen.LoginPage.name, builder = {
        composable(AppScreen.LoginPage.name) {
            LoginPage(modifier, navController, authViewModel)
        }
        composable(AppScreen.SignupPage.name) {
            SignupPage(modifier, navController, authViewModel)
        }
        composable(AppScreen.HomePage.name) {
            HomePage(modifier, navController, authViewModel)
        }
        composable(AppScreen.AboutPage.name) {
            AboutPage(modifier, navController)
        }
        composable(AppScreen.FavoriteListPage.name) {
            FavoriteListPage(modifier, navController, authViewModel)
        }
        composable(AppScreen.AddCommentsPage.name) {
            AddCommentPage(modifier, navController, authViewModel)
        }
        composable(AppScreen.ListCommentsPage.name) {
            ListCommentsPage(modifier, navController, authViewModel)
        }

    })
}