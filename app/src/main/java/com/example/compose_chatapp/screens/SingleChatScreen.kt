package com.example.compose_chatapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_chatapp.LiveChatViewModel
import com.example.compose_chatapp.commonDivider
import com.example.compose_chatapp.commonImage
import com.example.compose_chatapp.data.Message

/**
 * Created by Mohammad Kashif Ansari on 17,April,2024
 */

@Composable
fun SingleChatScreen(navController: NavController,viewModel: LiveChatViewModel,chatId:String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply={
        viewModel.onSendReply(chatId,reply)
        reply=""
    }
    val chatMessage=viewModel.chatMessages
    val myUser=viewModel.userData.value
    val currentChat=viewModel.chats.value.first{
        it.chatId==chatId
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.populateMessages(chatId)
    }
    BackHandler {
        viewModel.depopulateMessages()
    }
    val chatUser= if(myUser?.userId==currentChat.user1.userId) currentChat.user2 else currentChat.user1
    Column {
        chatHeaders(name = chatUser.name?:"", image = chatUser.imageUrl?:"") {
            navController.popBackStack()
            viewModel.depopulateMessages()
        }
        messageBox(modifier = Modifier.weight(1f), chatMessages = chatMessage.value, currentUserId = myUser?.userId?:"")
        replyBox(reply = reply, onReplyChange = {
            reply=it
        },onSendReply={
            onSendReply
        })
    }

}

@Composable
fun messageBox(modifier: Modifier,chatMessages:List<Message>,currentUserId:String){
    LazyColumn(modifier = modifier) {
        items(chatMessages){
            msg->
            val aliengment=if(msg.sendBy==currentUserId) Alignment.End else Alignment.Start
            val msgColor=if(msg.sendBy==currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)
            
            Column (modifier= Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalAlignment = aliengment){
                Text(
                    text = msg.message ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(color = msgColor)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
@Composable
fun chatHeaders(name:String,image:String,onBackClicked:()->Unit){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically){
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier
            .clickable { onBackClicked.invoke() }
            .padding(8.dp))
        commonImage(data = image, modifier = Modifier
            .padding(8.dp)
            .size(50.dp)
            .clip(CircleShape))
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
    }    
}

@Composable
fun replyBox(reply:String,onReplyChange:(String)->Unit,onSendReply:()->Unit){
    Column(modifier = Modifier.fillMaxWidth()) {
        commonDivider()
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
            TextField(value = reply, onValueChange = onReplyChange, maxLines = 3)
            Button(onClick = { onSendReply.invoke()}) {
                Text(text = "Send")
            }
        }
    }
}