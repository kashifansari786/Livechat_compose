package com.example.compose_chatapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */

fun navigateTo(navController: NavController,route:String){

    //navController is navigate to desired route which is passed in the constructor
    navController.navigate(route){
        //stack popup to passed route(no value is stored after this route)
        popUpTo(route)
        //launch only one screen
        launchSingleTop=true
    }
}
@Composable
fun commonProgressBar(){
    Row (modifier = Modifier
        .alpha(0.5f)
        .background(Color.LightGray)
        .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        CircularProgressIndicator()
    }
}
@Composable
fun checkedSignIn(viewModel: LiveChatViewModel,navController: NavController){
    val alreadySignIn= remember {
        mutableStateOf(false)
    }
    val signIn=viewModel.signIn.value
    if(signIn && !alreadySignIn.value){
        alreadySignIn.value=true
        navController.navigate(DestinationScreen.ChatListScreen.route){
            popUpTo(0)
        }
    }
}