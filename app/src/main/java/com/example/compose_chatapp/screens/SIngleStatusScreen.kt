package com.example.compose_chatapp.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_chatapp.DestinationScreen
import com.example.compose_chatapp.LiveChatViewModel
import com.example.compose_chatapp.commonImage

/**
 * Created by Mohammad Kashif Ansari on 18,April,2024
 */

enum class StatusState{
    INITIAL,ACTIVE, COMPLETED
}
@Composable
fun SingleStatusScreen(navController: NavController,viewModel: LiveChatViewModel,userId:String){
    val statuses=viewModel.status.value.filter {
        it.user.userId==userId
    }
    if(statuses.isNotEmpty()){
        val currentStatus= remember {
            mutableStateOf(0)
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)){
            commonImage(data = statuses[currentStatus.value].imageUrl, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            Row (modifier = Modifier.fillMaxWidth()){
                statuses.forEachIndexed{
                    index,status->
                    customProgressIndicator(modifier = Modifier.weight(1f).height(7.dp).padding(1.dp), state = if(currentStatus.value<index) StatusState.INITIAL else if(currentStatus.value==index) StatusState.ACTIVE else StatusState.COMPLETED) {
                        if(currentStatus.value<statuses.size-1)
                            currentStatus.value++
                        else
                            navController.popBackStack()
                    }
                }
            }
        }
    }

}

@Composable
fun customProgressIndicator(modifier: Modifier,state:StatusState,onComplete:()->Unit){
    var progress=if(state==StatusState.INITIAL) 0f else 1f
    if(state==StatusState.ACTIVE){
        val toggleState= remember {
            mutableStateOf(false)
        }
        LaunchedEffect(key1 = toggleState) {
            toggleState.value=true
        }
        val animation: Float by animateFloatAsState(
            if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = { onComplete.invoke() }
        )
        progress=animation

    }
    LinearProgressIndicator(modifier = modifier, color = Color.Red,progress=progress)
}