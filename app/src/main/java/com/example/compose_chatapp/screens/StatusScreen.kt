package com.example.compose_chatapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_chatapp.DestinationScreen
import com.example.compose_chatapp.LiveChatViewModel
import com.example.compose_chatapp.TitleText
import com.example.compose_chatapp.commonDivider
import com.example.compose_chatapp.commonProgressBar
import com.example.compose_chatapp.commonRow
import com.example.compose_chatapp.navigateTo

/**
 * Created by Mohammad Kashif Ansari on 16,April,2024
 */

@Composable
fun StatusScreen(navController: NavController,viewModel: LiveChatViewModel){

    val inProcess=viewModel.inProgressStatus.value
    if(inProcess)
        commonProgressBar()
    else{
        val statuses=viewModel.status.value
        val userData=viewModel.userData.value
        val myStatus=statuses.filter { it.user.userId==userData?.userId }
        val otherStatus=statuses.filter { it.user.userId!=userData?.userId }
        val launchers= rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            uri->
            uri?.let {
                viewModel.uploadStatus(uri)
            }
        }
        Scaffold( topBar = {
            TitleText(txt = "Status")
        },floatingActionButton = {
            fab {
                launchers.launch("images/*")
            }
        }, content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it))
            {

                if(statuses.isEmpty()){
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "No Status Available")
                    }
                }else{
                    if(myStatus.isNotEmpty()){
                        commonRow(imageUrl = myStatus[0].user.imageUrl, name = myStatus[0].user.name) {
                            navigateTo(navController,DestinationScreen.SingleStatusScreen.createRoute(myStatus[0].user.userId!!))

                        }
                        commonDivider()
                        val uniqueUsers=otherStatus.map {
                            it.user
                        }.toSet().toList()  //for this we will get unique users only
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(uniqueUsers){
                                user->
                                commonRow(imageUrl = user.imageUrl, name = user.name) {
                                    navigateTo(navController,DestinationScreen.SingleStatusScreen.createRoute(user.userId!!))
                                }
                            }
                        }
                    }
                }
                BottomNavigationMenu(selectedItem = BottomNavigationItems.CHATLIST, navController = navController)
            }

        })

    }

}

@Composable
fun fab(onFabClick:()->Unit){
    FloatingActionButton(
        onClick = { onFabClick },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(40.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Add Status", tint = Color.White)
    }
}