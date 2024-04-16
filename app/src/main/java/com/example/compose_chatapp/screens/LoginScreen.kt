package com.example.compose_chatapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.compose_chatapp.DestinationScreen
import com.example.compose_chatapp.LiveChatViewModel
import com.example.compose_chatapp.checkedSignIn
import com.example.compose_chatapp.commonProgressBar
import com.example.compose_chatapp.navigateTo

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */

@Composable
fun LoginScreen(navController: NavController,viewModel: LiveChatViewModel){

    checkedSignIn(viewModel,navController)
    val emailState= remember {
        mutableStateOf(TextFieldValue())
    }
    val passwordState= remember {
        mutableStateOf(TextFieldValue())
    }
    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ), horizontalAlignment = Alignment.CenterHorizontally) {
            val focus= LocalFocusManager
            Image(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier
                .size(200.dp)
                .padding(16.dp))
            Text(text = "Sign Up", fontSize = 30.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            //email
            OutlinedTextField(value = emailState.value, onValueChange = {
                emailState.value=it
            }, label = { Text(text = "Email") }, modifier = Modifier.padding(8.dp))
            //password
            OutlinedTextField(value = passwordState.value, onValueChange = {
                passwordState.value=it
            }, label = { Text(text = "Password") }, modifier = Modifier.padding(8.dp))

            Button(onClick = { viewModel.Login(
                emailState.value.text,
                passwordState.value.text) }, modifier = Modifier.padding(8.dp)) {
                Text(text = "SignIn")
            }
            Text(
                text = "New User ? Go to Sign Up->",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.SignupScreen.route)
                    })
        }
    }
    if(viewModel.inProgress.value){
        commonProgressBar()
    }
}