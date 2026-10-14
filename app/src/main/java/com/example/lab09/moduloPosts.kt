package com.example.lab09

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun ScreenPosts(
    navController: NavHostController,
    servicio: PostApiService
) {
    val listaPosts: SnapshotStateList<PostModel> = remember { mutableStateListOf() }
    var isLoading by remember { mutableStateOf(value = true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun cargarPosts() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val listado = servicio.getUserPosts()
                listaPosts.clear()
                listado.forEach { listaPosts.add(it) }
            } catch (e: Exception) {
                Log.e("POSTS", "Error al obtener posts", e)
                errorMessage = e.localizedMessage ?: "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarPosts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Cargando publicaciones...")
                    }
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { cargarPosts() }) {
                        Text("Reintentar")
                    }
                }
            }

            listaPosts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones disponibles")
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(listaPosts) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.id.toString(),
                                modifier = Modifier.weight(0.1f),
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.padding(horizontal = 4.dp))
                            Text(
                                text = item.title,
                                modifier = Modifier.weight(0.75f)
                            )
                            IconButton(
                                onClick = {
                                    navController.navigate("postsVer/${item.id}")
                                    Log.e("POSTS", "ID = ${item.id}")
                                },
                                modifier = Modifier.weight(0.15f)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Ver"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenPost(
    navController: NavHostController,
    servicio: PostApiService,
    id: Int
) {
    var post by remember { mutableStateOf<PostModel?>(null) }
    var isLoading by remember { mutableStateOf(value = true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun cargarPost() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val xpost = servicio.getUserPostById(id)
                post = xpost.copy()
            } catch (e: Exception) {
                Log.e("POST", "Error al obtener post $id", e)
                errorMessage = e.localizedMessage ?: "Error al obtener el post"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(id) {
        cargarPost()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Cargando publicación #$id...")
                    }
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                        Button(onClick = { cargarPost() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            post != null -> {
                val currentPost = post!!
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("← Volver")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentPost.id.toString(),
                        onValueChange = {},
                        label = { Text("id") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = currentPost.userId.toString(),
                        onValueChange = {},
                        label = { Text("userId") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = currentPost.title,
                        onValueChange = {},
                        label = { Text("title") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = currentPost.body,
                        onValueChange = {},
                        label = { Text("body") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    currentPost.link?.let { link ->
                        OutlinedTextField(
                            value = link,
                            onValueChange = {},
                            label = { Text("link") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    currentPost.commentCount?.let { comments ->
                        OutlinedTextField(
                            value = comments.toString(),
                            onValueChange = {},
                            label = { Text("comment_count") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
