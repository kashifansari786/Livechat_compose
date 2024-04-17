package com.example.compose_chatapp.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.compose_chatapp.LiveChatViewModel

/**
 * Created by Mohammad Kashif Ansari on 16,April,2024
 */

@Composable
fun StatusScreen(navController: NavController,viewModel: LiveChatViewModel){
    BottomNavigationMenu(selectedItem = BottomNavigationItems.CHATLIST, navController = navController)
}