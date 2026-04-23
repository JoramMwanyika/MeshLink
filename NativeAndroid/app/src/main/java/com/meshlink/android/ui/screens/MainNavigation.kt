package com.meshlink.android.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { 
            SplashScreen(onGetStarted = { 
                navController.navigate("setup")
            }) 
        }
        composable("setup") { 
            SetupScreen(onContinue = { 
                navController.navigate("main") { popUpTo("setup") { inclusive = true } } 
            }) 
        }
        composable("main") { 
            MainScreen(onChatClick = { id, name -> 
                navController.navigate("chat/$id/$name")
            }) 
        }
        composable("chat/{id}/{name}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ChatScreen(peerId = id, peerName = name, onBack = { navController.popBackStack() })
        }
    }
}
