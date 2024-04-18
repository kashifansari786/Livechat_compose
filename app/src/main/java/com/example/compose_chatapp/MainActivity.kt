package com.example.compose_chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.compose_chatapp.screens.LoginScreen
import com.example.compose_chatapp.ui.theme.Compose_chatAppTheme
import androidx.navigation.compose.composable
import com.example.compose_chatapp.screens.ChatListScreen
import com.example.compose_chatapp.screens.ProfileScreen
import com.example.compose_chatapp.screens.SignupScreen
import com.example.compose_chatapp.screens.SingleChatScreen
import com.example.compose_chatapp.screens.SingleStatusScreen
import com.example.compose_chatapp.screens.StatusScreen
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

//create a route
sealed class DestinationScreen(var route:String){
    object SignupScreen:DestinationScreen("signup")
    object LoginScreen:DestinationScreen("login")
    object ProfileScreen:DestinationScreen("profile")
    object ChatListScreen:DestinationScreen("chatList")
    object SingleChatScreen:DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id:String) ="singleChat/$id"
    }

    object StatusListScreen:DestinationScreen("statusList")
    object SingleStatusScreen:DestinationScreen("singleStatus/{userId}"){
        fun createRoute(userId:String) ="singleStatus/$userId"
    }

}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose_chatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    chatAppNavigation()
                }
            }
        }
    }

    @Composable
    fun chatAppNavigation(){
        val navController= rememberNavController()
        val viewModel:LiveChatViewModel= hiltViewModel()
        NavHost(navController = navController, startDestination = DestinationScreen.SignupScreen.route){
            composable(DestinationScreen.SignupScreen.route){
                SignupScreen(navController,viewModel)
            }
            composable(DestinationScreen.LoginScreen.route){
                LoginScreen(navController,viewModel)
            }
            composable(DestinationScreen.ChatListScreen.route){
                ChatListScreen(navController,viewModel)
            }
            composable(DestinationScreen.SingleChatScreen.route){
                val chatId=it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController,viewModel,it)
                }

            }
            composable(DestinationScreen.StatusListScreen.route){
                StatusScreen(navController,viewModel)
            }
            composable(DestinationScreen.SingleStatusScreen.route){
                val userId=it.arguments?.getString("userId")
                userId?.let { it1 -> SingleStatusScreen(navController,viewModel, it1) }
            }
            composable(DestinationScreen.ProfileScreen.route){
                ProfileScreen(navController,viewModel)
            }
        }

    }
}
