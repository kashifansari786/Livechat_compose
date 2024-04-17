package com.example.compose_chatapp

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.compose_chatapp.constants.USER_NODE
import com.example.compose_chatapp.data.ChatData
import com.example.compose_chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
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

    fun onAddChat(it:String){

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
}



