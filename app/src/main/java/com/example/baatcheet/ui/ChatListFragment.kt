package com.example.baatcheet.ui

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baatcheet.R
import com.example.baatcheet.`interface`.chatInterface
import com.example.baatcheet.controller.RecentChatsAdapter
import com.example.baatcheet.databinding.FragmentChatListBinding
import com.example.baatcheet.model.Message
import com.example.baatcheet.utilis.FireRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*


class ChatListFragment : Fragment() , chatInterface {

	private lateinit var binding : FragmentChatListBinding

	private lateinit var adapter : RecentChatsAdapter

	var chatLists : ArrayList<Message> = ArrayList()



	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View? {
		// Inflate the layout for this fragment
		binding = FragmentChatListBinding.inflate(inflater , container , false)



		val layoutManager = LinearLayoutManager(context)
		binding.recentChatRecyclerVIew.layoutManager = layoutManager
		adapter = RecentChatsAdapter(chatLists, this@ChatListFragment)
		binding.recentChatRecyclerVIew.adapter= adapter

		return binding.root
	}


	override fun onStart() {
		super.onStart()
		recentchats()
	}

	private fun recentchats() {

		FireRef.chatListRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).orderByChild("currenttime").addValueEventListener(
			object : ValueEventListener {

				override fun onDataChange(snapshot : DataSnapshot) {
					chatLists.clear()
					for (datasnapshot : DataSnapshot in snapshot.children) {
						chatLists.add(datasnapshot.getValue(Message::class.java) !!)

					}
					Collections.reverse(chatLists)
					adapter.notifyDataSetChanged()

				}


				override fun onCancelled(error : DatabaseError) {
					TODO("Not yet implemented")
				}

			}
		)
	}

	override fun onItemClick(position : Int , recieverID : String) {
		var chat : Message = chatLists[position]
		if (chat.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
			val recID : String? = chat.recieverId

		} else {
			val recID : String? = chat.senderId

		}
		val bundle = Bundle()
		bundle.putString("recieverID" , recieverID)
	//	Log.d(TAG , "recieverID: "+recieverID)
		findNavController().navigate(R.id.action_chatListFragment_to_chatFragment , bundle)
	}


}