package com.example.mobproject.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobproject.AppScreen
import com.example.mobproject.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCommentsPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val comments by authViewModel.userComments.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.getComments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Comments", fontSize = 20.sp) }
            )
        },
        bottomBar = {
            BottomAppBar(
                contentPadding = PaddingValues(horizontal = 16.dp),
                content = {
                    IconButton(onClick = { navController.navigate(AppScreen.HomePage.name) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { navController.navigate(AppScreen.FavoriteListPage.name) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.liste),
                            contentDescription = "Favorites"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { navController.navigate(AppScreen.AboutPage.name) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = "About"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(comments) { commentBox ->
                CommentItem(commentBox, authViewModel)
            }
        }
    }
}

@Composable
fun CommentItem(commentBox: AuthViewModel.Comment, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = commentBox.userName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = commentBox.comment,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            if (auth.currentUser!!.uid == commentBox.userId) {
                IconButton(
                    onClick = {
                        authViewModel.deleteComment(commentBox, context)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.remove),
                        contentDescription = "Delete Comment"
                    )
                }
            }
        }
    }
}