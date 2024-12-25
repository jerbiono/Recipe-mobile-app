package com.example.mobproject.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobproject.AppScreen
import com.example.mobproject.R
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add a comment", fontSize = 20.sp) }
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
                        onClick = { navController.navigate(AppScreen.ListCommentsPage.name) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.comments),
                            contentDescription = "Add comment"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
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
                        onClick = { navController.navigate(AppScreen.AboutPage.name) }
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
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Your Comment") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))



            Button(
                onClick = {


                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && !comment.isBlank() && comment.isNotEmpty()) {
                        authViewModel.saveComment(comment, context)
                        comment = ""
                    } else {
                        Toast.makeText(context, "Please add a comment!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}
