package com.example.compose_chatapp.screens

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.compose_chatapp.DestinationScreen
import com.example.compose_chatapp.LiveChatViewModel
import com.example.compose_chatapp.TitleText
import com.example.compose_chatapp.commonProgressBar

/**
 * Created by Mohammad Kashif Ansari on 16,April,2024
 */

@Composable
fun ChatListScreen(navController: NavController,viewModel: LiveChatViewModel){

    val inProgress=viewModel.inProgressChat.value
    if(inProgress)
        commonProgressBar()
    else
    {
        val chats=viewModel.chats.value
        val userData=viewModel.userData.value
        val showDialog= remember {
            mutableStateOf(false)
        }
        val onFabClick:()->Unit={
            showDialog.value=true
        }
        val onDismiss:()->Unit={
            showDialog.value=false
        }
        val onAddChat:(String)->Unit={
            viewModel.onAddChat(it)
            showDialog.value=false
        }
        Scaffold(
            topBar = {
                TitleText(txt = "Chats")
            },
            floatingActionButton = { fab(
                showDialog = showDialog.value,
                onfabClick = { onFabClick },
                onDismiss = { onDismiss },
                onAddChat={onAddChat})
        },
            content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {

                if(chats.isEmpty()){
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "No Chat Available")
                    }
                }
                BottomNavigationMenu(selectedItem = BottomNavigationItems.CHATLIST, navController = navController)
            }
        })

    }

}

@Composable
fun fab(showDialog:Boolean,onfabClick:()->Unit,onDismiss:()->Unit,onAddChat:(String)->Unit){
    val addChatMember= remember {
        mutableStateOf("")
    }
    if(showDialog){
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatMember.value=""
        }, confirmButton = {
            Button(onClick = { onAddChat(addChatMember.value) }) {
                Text(text = "Add Chat")
            }
        }, title = {
                   Text(text = "Add Chat")
        }, text = {
                  OutlinedTextField(value = addChatMember.value, onValueChange = {addChatMember.value=it}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }, modifier = Modifier.fillMaxWidth(), properties = DialogProperties(dismissOnBackPress = true))
        FloatingActionButton(
            onClick = { onfabClick },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = CircleShape,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
        }
    }
}