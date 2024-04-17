package com.example.compose_chatapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_chatapp.DestinationScreen
import com.example.compose_chatapp.LiveChatViewModel
import com.example.compose_chatapp.commonDivider
import com.example.compose_chatapp.commonImage
import com.example.compose_chatapp.commonProgressBar
import com.example.compose_chatapp.navigateTo

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */

@Composable
fun ProfileScreen(navController: NavController,viewModel: LiveChatViewModel){

    val inProgress=viewModel.inProgress.value
    if(inProgress)
        commonProgressBar()
    else{
        val userData=viewModel.userData.value
        var name by rememberSaveable {
            mutableStateOf(userData?.name?:"")
        }
        var number by rememberSaveable {
            mutableStateOf(userData?.number?:"")
        }
        Column {
            profileContent(modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
                viewModel,
                name = name,
                number = number,
                onNameChange = {name=it},
                onNumberChange = {number=it},
                onSave = {
                         viewModel.createOrUpdateProfile(name=name,number=number)
                },
                onBack = {
                         navigateTo(navController=navController, route = DestinationScreen.ChatListScreen.route)
                },
                onLogout = {
                    viewModel.logout()
                    navigateTo(navController=navController, route = DestinationScreen.LoginScreen.route)
                })
            BottomNavigationMenu(selectedItem = BottomNavigationItems.CHATLIST, navController = navController)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileContent(
    modifier: Modifier,
    viewModel: LiveChatViewModel,
    name:String,
    number:String,
    onNameChange:(String)->Unit,
    onNumberChange:(String)->Unit,
    onBack:()->Unit,
    onSave:()->Unit,
    onLogout:()->Unit){
    val imageUri=viewModel.userData.value?.imageUrl
    Column(modifier = modifier) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Back", modifier=Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save", modifier = Modifier.clickable {
                onSave.invoke()
            })
        }
        commonDivider()
        profileImage(imageUri,viewModel)
        commonDivider()
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), verticalAlignment = Alignment.CenterVertically){
            Text(text = "Name",modifier=Modifier.width(100.dp))
            TextField(value = name,
                onValueChange = onNameChange,
                colors=TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ))
        }
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), verticalAlignment = Alignment.CenterVertically){
            Text(text = "Number",modifier=Modifier.width(100.dp))
            TextField(value = number,
                onValueChange = onNumberChange,
                colors=TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ))
        }
        commonDivider()
        Row (modifier= Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onLogout.invoke()
            }, horizontalArrangement = Arrangement.Center){
            Text(text = "Logout")
        }
    }
}

@Composable
fun profileImage(imageUrl:String?=null,viewModel: LiveChatViewModel){

    val launcher= rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        it?.let {
            viewModel.userData?.value?.userId?.let { it1 -> viewModel.uploadProfileImage(it, it1) }
        }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)){
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = CircleShape, modifier = Modifier
                .padding(8.dp)
                .size(100.dp)) {
                commonImage(data = imageUrl)
            }
            Text(text = "Change profile picture")
        }
        if(viewModel.inProgress.value)
            commonProgressBar()
    }
}