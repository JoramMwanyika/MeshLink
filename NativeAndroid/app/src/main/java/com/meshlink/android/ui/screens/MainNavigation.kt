package com.meshlink.android.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meshlink.android.ui.viewmodel.MeshViewModel
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(viewModel: MeshViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { 
            SplashScreen(onGetStarted = { 
                navController.navigate("setup")
            }) 
        }
        composable("setup") { 
            val scope = rememberCoroutineScope()
            SetupScreen(onContinue = { username ->
                val app = navController.context.applicationContext as com.meshlink.android.MeshLinkApplication
                scope.launch {
                    val deviceId = app.identityManager.createAccount(username)
                    // Restart mesh services with the new identity and name
                    app.transportManager.startMeshServices(deviceId)
                    navController.navigate("main") { popUpTo("setup") { inclusive = true } }
                }
            }) 
        }
        composable("main") { 
            MainScreen(viewModel, onChatClick = { id, name -> 
                navController.navigate("chat/$id/$name")
            }) 
        }
        composable("chat/{id}/{name}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ChatScreen(viewModel, peerId = id, peerName = name, onBack = { navController.popBackStack() })
        }
    }
}
