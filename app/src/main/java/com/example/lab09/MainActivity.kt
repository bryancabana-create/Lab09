package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab09.ui.theme.Lab09Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab09Theme {
                ProgPrincipal9()
            }
        }
    }
}

fun crearServicio(): PostApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(PostApiService::class.java)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgPrincipal9() {
    val navController = rememberNavController()
    val servicio = crearServicio()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("JSONPlaceholder Access")
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "inicio",
                    onClick = {
                        if (currentRoute != "inicio") {
                            navController.navigate("inicio") {
                                popUpTo("inicio") { inclusive = true }
                            }
                        }
                    },
                    icon = { Text("⌂") },
                    label = { Text("Inicio") },
                )

                NavigationBarItem(
                    selected = (currentRoute == "posts") || (currentRoute?.startsWith("postsVer") == true),
                    onClick = {
                        if (currentRoute != "posts") {
                            navController.navigate("posts") {
                                popUpTo("inicio")
                            }
                        }
                    },
                    icon = { Text("≡") },
                    label = { Text("Posts") },
                )
            }
        },
    ) { paddingValues ->
        Contenido(
            pv = paddingValues,
            navController = navController,
            servicio = servicio,
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    servicio: PostApiService,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv),
    ) {
        NavHost(
            navController = navController,
            startDestination = "inicio",
        ) {
            composable("inicio") {
                ScreenInicio()
            }

            composable("posts") {
                ScreenPosts(navController, servicio)
            }

            composable(
                "postsVer/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    },
                ),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                ScreenPost(
                    navController = navController,
                    servicio = servicio,
                    id = id,
                )
            }
        }
    }
}

@Composable
fun ScreenInicio() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Bienvenido al Laboratorio 09",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
