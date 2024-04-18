package com.example.compose_chatapp

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.compose_chatapp.constants.CHATS
import com.example.compose_chatapp.constants.MESSAGES
import com.example.compose_chatapp.constants.STATUS
import com.example.compose_chatapp.constants.USER_NODE
import com.example.compose_chatapp.data.ChatData
import com.example.compose_chatapp.data.ChatUser
import com.example.compose_chatapp.data.Message
import com.example.compose_chatapp.data.Status
import com.example.compose_chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */
@HiltViewModel
class LiveChatViewModel @Inject constructor(val auth:FirebaseAuth,val dataBase:FirebaseFirestore,val storage:FirebaseStorage): ViewModel() {



    var inProgress= mutableStateOf(false)
    val inProgressChat= mutableStateOf(false)
    var eventMutableState= mutableStateOf<Event<String>?>(null)
    var signIn= mutableStateOf(false)
    var userData= mutableStateOf<UserData?>(null)
    val chats= mutableStateOf<List<ChatData>>(listOf())
    val chatMessages= mutableStateOf<List<Message>>(listOf())
    val chatInProgress= mutableStateOf(false)
    var currentChatMessageListener:ListenerRegistration?=null
    val status= mutableStateOf<List<Status>>(listOf())
    val inProgressStatus= mutableStateOf(false)


    init {
        val currentUser=auth.currentUser
        signIn.value=currentUser!=null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signUp(name:String,number:String,email:String,password:String){
        inProgress.value=true
        if(name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customException = "please fill all fields")
            return
        }
        inProgress.value=true
        dataBase.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        signIn.value=true
                        createOrUpdateProfile(name,number)
                        //navigateTo(navController = )
                    } else handleException(it.exception,"Signup failed")
                }
            }else{
                handleException(customException = "number already exist")
                inProgress.value=false
            }
        }

    }

    fun populateMessages(chatId:String){
        chatInProgress.value=true
        currentChatMessageListener=dataBase.collection(CHATS).document(chatId).collection(MESSAGES).addSnapshotListener{
            value,error->
            if(error!=null)
                handleException(error)
            if(value!=null){
                chatMessages.value=value.documents.mapNotNull {
                    it.toObject<Message>()
                }.sortedBy { it.timeStamp }
                chatInProgress.value=false
            }

        }
    }
    fun depopulateMessages(){
        chatMessages.value= listOf()
        currentChatMessageListener=null
    }
    fun populateChats(){
        inProgressChat.value=true
        dataBase.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId",userData.value?.userId),
                Filter.equalTo("user2.userId",userData.value?.userId)
            )
        ).addSnapshotListener{
            value,error->
            if(error!=null)
                handleException(error)
            if(value!=null){
                chats.value=value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProgressChat.value=false
            }
        }
    }

    fun onSendReply(chatId:String,message:String){
        val time=Calendar.getInstance().time.toString()
        val msg=Message(userData.value?.userId,message,time)
        dataBase.collection(CHATS).document(chatId).collection(MESSAGES).document().set(msg)
    }
    fun onAddChat(number :String){
        if(number.isEmpty() or !number.isDigitsOnly())
            handleException(customException = "Number must contain digits only")
        else{
            dataBase.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number",number),
                        Filter.equalTo("user2.number",userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number",userData.value?.number),
                        Filter.equalTo("user2.number",number))
                )).get().addOnSuccessListener {
                    if(it.isEmpty){
                        dataBase.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
                            if(it.isEmpty)
                                handleException(customException = "number not found")
                            else{
                                val chatPartner=it.toObjects<UserData>()[0]
                                val id=dataBase.collection(CHATS).document().id
                                val chat=ChatData(chatId = id, ChatUser(
                                    userData.value?.userId,
                                    userData.value?.name,
                                    userData.value?.imageUrl,
                                    userData.value?.number

                                ),ChatUser(
                                    chatPartner.userId,
                                    chatPartner.name,
                                    chatPartner.imageUrl,
                                    chatPartner.number)
                                )
                                dataBase.collection(CHATS).document(id).set(chat)

                            }
                        }.addOnFailureListener{
                            handleException(it)
                        }
                    }else
                        handleException(customException = "Chat already exists")
            }
        }
    }
    fun Login(email:String,password:String){
        if(email.isEmpty() or password.isEmpty()){
            handleException(customException = "Please fill all the fields")
            return
        }else{
            inProgress.value=true
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener() {
                if(it.isSuccessful){
                    signIn.value=true
                    inProgress.value=false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                }
            }
        }

    }

    fun logout(){
        auth.signOut()
        signIn.value=false
        userData.value=null
        depopulateMessages()
        currentChatMessageListener=null
        eventMutableState.value=Event("Logout")

    }

    fun createOrUpdateProfile(name: String?=null, number: String?=null,imageUrl:String?=null) {
        var uId=auth.currentUser?.uid
        val userData=UserData(
            uId,name?:userData.value?.name,number?:userData.value?.number,imageUrl?:userData.value?.imageUrl
        )
        uId?.let {
            inProgress.value=true
            dataBase.collection(USER_NODE).document(uId).get().addOnSuccessListener {
                if(it.exists()){
                    //update user data
                    dataBase.collection(USER_NODE).document(uId).set(userData)
                    inProgress.value=false
                    getUserData(uId)
                }else
                {
                    dataBase.collection(USER_NODE).document(uId).set(userData)
                    inProgress.value=false
                    getUserData(uId)
                }
            }.addOnFailureListener{
                handleException(it,"cannot retrieve user")
            }

        }
    }

    private fun getUserData(uId: String) {
        inProgress.value=true
        dataBase.collection(USER_NODE).document(uId).addSnapshotListener{
            value,error->
            if(error!=null){

            }
            if(value!=null){
                var user=value.toObject<UserData>()

                userData.value=user
                inProgress.value=false
                populateChats()
                populateStatus()
            }
        }
    }

    fun handleException(exception: Exception?=null,customException:String=""){
        val errMessage=exception?.localizedMessage?:""
        val message=if(customException.isNullOrEmpty()) errMessage else customException

        eventMutableState.value= Event(message)
        inProgress.value=false
    }
    fun uploadProfileImage(uri:Uri,userId:String){
        uploadImage(uri,userId){
            userData.value?.imageUrl=uri.toString()
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }
    fun uploadImage(uri: Uri,userId:String,onSuccess:(Uri)->Unit){
        inProgress.value=true
        val storageRef=storage.reference
        val randomUuId=UUID.randomUUID()
        val imageRef=storageRef.child("images/{$userId}")
        val uploadTask=imageRef.putFile(uri).addOnSuccessListener {
            val result=it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProgress.value=false
        }.addOnFailureListener{
            handleException(it)
        }
    }
    fun uploadStatusImage(uri: Uri,onSuccess:(Uri)->Unit){
        inProgress.value=true
        val storageRef=storage.reference
        val randomUuId=UUID.randomUUID()
        val imageRef=storageRef.child("images/{$randomUuId}")
        val uploadTask=imageRef.putFile(uri).addOnSuccessListener {
            val result=it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProgress.value=false
        }.addOnFailureListener{
            handleException(it)
        }
    }

    fun uploadStatus(uri: Uri) {
        uploadStatusImage(uri){
            createStatus(it.toString())
        }

    }
    fun createStatus(imageUri:String){
        val newStatus=Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number,
            ),imageUri,System.currentTimeMillis()
        )
        dataBase.collection(STATUS).document().set(newStatus)
    }
    fun populateStatus(){
        val timeDelta=24L * 60 * 60 * 1000  //24hour
        val timeCutOff=System.currentTimeMillis() - timeDelta
        inProgressStatus.value=true
        dataBase.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId",userData.value?.userId),
                Filter.equalTo("user2.userId",userData.value?.userId)
            )
        ).addSnapshotListener{
            value,error->
            if(error!=null)
                handleException(error)
            if(value!=null){
                val currentUsersConnections= arrayListOf(userData.value?.userId)
                val chats=value.toObjects<ChatData>()
                chats.forEach {
                    chat->
                    if(chat.user1.userId==userData.value?.userId)
                        currentUsersConnections.add(chat.user2.userId)
                    else
                        currentUsersConnections.add(chat.user1.userId)
                }
                dataBase.collection(STATUS).whereGreaterThan("timeStamp",timeCutOff).whereIn("user.userId",currentUsersConnections).addSnapshotListener{
                    value,error->
                    if(error!=null)
                        handleException(error)
                    if(value!=null){
                        status.value=value.toObjects()
                        inProgressStatus.value=false
                    }

                }
            }
        }
    }
}



