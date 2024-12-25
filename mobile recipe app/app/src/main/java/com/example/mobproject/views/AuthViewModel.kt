package com.example.mobproject.views

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobproject.network.RecipeApi
import com.example.mobproject.network.RecipeCategory
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthtentificated)
    val authState: StateFlow<AuthState> = _authState
    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName
    private val _email = MutableStateFlow("")
    private val _favoriteRecipes = MutableStateFlow<Set<String>>(emptySet())
    val favoriteRecipes: StateFlow<Set<String>> = _favoriteRecipes
    private val _recipeCategories = MutableStateFlow<List<RecipeCategory>?>(null)
    val recipeCategories: StateFlow<List<RecipeCategory>?> = _recipeCategories
    private val _userComments = MutableStateFlow<List<Comment>>(emptyList())
    val userComments: StateFlow<List<Comment>> = _userComments


    init {
        resetAuthState()
    }

    private fun resetAuthState() {
        _authState.value = AuthState.Unauthtentificated
        _firstName.value = ""
        _email.value = ""
        _recipeCategories.value = null
    }


    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty!")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                _email.value = document.getString("email") ?: ""
                                _firstName.value = document.getString("firstName") ?: ""
                                _authState.value = AuthState.Authentificated
                                getRecipes()
                                refreshFavorites()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore error:", exception.localizedMessage!!.toString())
                        }
                } else {
                    val exception = task.exception
                    _authState.value =
                        AuthState.Error(exception?.localizedMessage ?: "Something went wrong!")
                }
            }
    }

    data class Comment(
        val userId: String,
        val userName: String,
        val comment: String,
        val id: String
    )

    fun saveComment(comment: String, context: Context) {
        val user = auth.currentUser
        val commentId = FirebaseFirestore.getInstance().collection("users")
            .document(user!!.uid)
            .collection("comments")
            .document().id

        user?.let {
            val commentData = mapOf(
                "userId" to user.uid,
                "userName" to _firstName.value,
                "comment" to comment,
                "id" to commentId
            )
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("comments")
                .document(commentId)
                .set(commentData)
                .addOnSuccessListener {
                    val newComment = Comment(
                        userId = user.uid,
                        userName = _firstName.value,
                        comment = comment,
                        id = commentId
                    )
                    _userComments.value = _userComments.value + newComment

                    Toast.makeText(context, "Comment added successfully!", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error saving comment", exception)
                }
        }
    }

    fun saveRecipe(recipe: RecipeCategory) {
        val user = auth.currentUser
        val recipeId = recipe.idCategory ?: return
        val simpleData = mapOf(
            "idCategory" to recipe.idCategory,
            "categoryName" to recipe.strCategory,
            "categoryImg" to recipe.strCategoryThumb,
            "categoryDescription" to recipe.strCategoryDescription
        )

        user?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(it.uid)
                .collection("favorites")
                .document(recipeId)
                .set(simpleData)
                .addOnSuccessListener {
                    _favoriteRecipes.value = _favoriteRecipes.value + recipeId
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error saving recipe", exception)
                }
        }
    }

    fun deleteComment(commentBox: AuthViewModel.Comment, context: Context) {
        val user = auth.currentUser
        user?.let {
            val firestore = FirebaseFirestore.getInstance()
            val commentsRef = firestore.collection("users")
                .document(user.uid)
                .collection("comments")


            commentsRef.document(commentBox.id).get().addOnSuccessListener { document ->
                val id = document.getString("id")

                if (commentBox.id == id)
                    commentsRef.document(commentBox.id)

                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Comment deleted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Firestore", "Deleted comment with ID: $commentBox.id")

                            _userComments.value = _userComments.value - commentBox
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Failed to delete comment: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                "FirestoreError",
                                "Error deleting comment: ${e.localizedMessage}",
                                e
                            )
                        } else {
                    Toast.makeText(
                        context,
                        "You can delete just your own comment",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun getComments() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .get()
            .addOnSuccessListener { usersResult ->
                val allComments = mutableListOf<Comment>()
                val tasks = mutableListOf<Task<QuerySnapshot>>()

                for (userDocument in usersResult) {
                    val userId = userDocument.id
                    val userName = userDocument.getString("firstName") ?: "Anonymous"

                    val commentsTask = firestore.collection("users")
                        .document(userId)
                        .collection("comments")
                        .get()

                    tasks.add(commentsTask)

                    commentsTask.addOnSuccessListener { commentsResult ->
                        val userComments = commentsResult.mapNotNull { commentDocument ->
                            val commentText = commentDocument.getString("comment")
                            val commentId = commentDocument.id
                            commentDocument?.let {
                                Comment(
                                    userId = userId,
                                    userName = userName,
                                    comment = commentText.orEmpty(),
                                    id = commentId
                                )
                            }
                        }
                        allComments.addAll(userComments)
                    }
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _userComments.value = allComments
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching users: ${e.localizedMessage}", e)
            }
    }

    fun refreshFavorites() {
        val user = auth.currentUser
        user?.let { it ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(it.uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener { documents ->
                    val favorites = documents.map { it.id }.toSet()
                    _favoriteRecipes.value = favorites
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error fetching favorites", exception)
                }
        }
    }

    fun removeRecipeFromFavorite(recipe: RecipeCategory) {
        val user = auth.currentUser ?: return
        val recipeId = recipe.idCategory ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .collection("favorites")
            .document(recipeId)
            .delete()
            .addOnSuccessListener {
                _favoriteRecipes.value = _favoriteRecipes.value - recipeId
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error removing recipe", exception)
            }
    }


    fun getRecipes() {
        viewModelScope.launch {
            try {
                val response = RecipeApi.retrofitService.getCategories()
                if (response.isSuccessful) {
                    val categories = response.body()?.categories
                    _recipeCategories.value = categories
                } else {
                    Log.e("RecipeApi", "Response error: bug")
                }
            } catch (e: Exception) {
                Log.e("RecipeApi", "API call error: ${e.localizedMessage}")
            }
        }
    }

    fun signup(email: String, password: String, firstName: String) {
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
            _authState.value = AuthState.Error("Email, password, or first name can't be empty!")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userMap = hashMapOf("firstName" to firstName, "email" to email)
                    FirebaseFirestore.getInstance().collection("users").document(user!!.uid)
                        .set(userMap)
                        .addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                _email.value = email
                                _firstName.value = firstName
                                _authState.value = AuthState.Authentificated
                                getRecipes()
                            } else {
                                _authState.value =
                                    AuthState.Error("Firestore error: ${firestoreTask.exception?.localizedMessage}")
                            }
                        }
                } else {
                    val exception = task.exception
                    _authState.value =
                        AuthState.Error(exception?.localizedMessage ?: "Something went wrong!")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthtentificated
    }

    sealed class AuthState {
        object Authentificated : AuthState()
        object Unauthtentificated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }


}