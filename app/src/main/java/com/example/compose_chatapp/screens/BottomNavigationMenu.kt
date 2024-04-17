package com.example.compose_chatapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_chatapp.DestinationScreen
import com.example.compose_chatapp.R
import com.example.compose_chatapp.navigateTo

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */

enum class BottomNavigationItems(val icon:Int,val navDestination:DestinationScreen){
    CHATLIST(R.drawable.chat,DestinationScreen.ChatListScreen),
    STATUSLIST(R.drawable.status,DestinationScreen.StatusListScreen),
    PROFILE(R.drawable.profile,DestinationScreen.ProfileScreen)
}
@Composable
fun BottomNavigationMenu(selectedItem:BottomNavigationItems,navController: NavController){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp)
        .wrapContentHeight()
        .background(Color.White)) {
        for (item in BottomNavigationItems.values()){
            Image(painter = painterResource(id = item.icon), contentDescription = null, modifier = Modifier.size(40.dp).padding(4.dp).weight(1f).clickable {
                navigateTo(navController,item.navDestination.route)
            })
        }
    }
}