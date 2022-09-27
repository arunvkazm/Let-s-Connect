package com.example.baatcheet.utilis

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FireRef {
    val userRef = FirebaseDatabase.getInstance().getReference("users")
    val chatRef = FirebaseDatabase.getInstance().getReference("Chats")
    val chatListRef = FirebaseDatabase.getInstance().getReference("chat_list")
    val chatImagesRef = FirebaseStorage.getInstance().getReference("chatImages")
    val chatdocRef = FirebaseStorage.getInstance().getReference("chatDoc")
    val chatVideoRef = FirebaseStorage.getInstance().getReference("chatVideo")
    val chatAudioRef = FirebaseStorage.getInstance().getReference("chatAudio")
    val chatContactRef = FirebaseStorage.getInstance().getReference("chatContact")
    val userProfileRef = FirebaseStorage.getInstance().getReference("userprofile")
    //val userToken = FirebaseDatabase.getInstance().getReference("token")
}