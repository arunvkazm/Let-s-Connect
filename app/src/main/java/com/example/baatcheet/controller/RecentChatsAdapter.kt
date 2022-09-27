package com.example.baatcheet.controller

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.baatcheet.R
import com.example.baatcheet.`interface`.chatInterface
import com.example.baatcheet.model.Message
import com.example.baatcheet.model.User
import com.example.baatcheet.utilis.FireRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlin.math.log

class RecentChatsAdapter(private val chatlist : ArrayList<Message> , private val click : chatInterface) :
	RecyclerView.Adapter<RecentChatsAdapter.ViewHolder>() {


	override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchat_layout , parent , false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder : ViewHolder , position : Int) {
		val chats = chatlist[position]
		holder.username.text = chats.recievername
		holder.lastmsg.text = chats.msg
		//Log.d(TAG , "MSG: "+chats.msg)

			FireRef.userRef.child(chats.recieverId.toString()).addValueEventListener(
				object :ValueEventListener{

					override fun onDataChange(snapshot : DataSnapshot) {
						var userpic: User? =   snapshot.getValue(User::class.java)
						Picasso.get().load(userpic?.profileimg).into(holder.userprofile)
						Log.d(TAG , "UserPRofile:"+userpic?.profileimg)
					}


					override fun onCancelled(error : DatabaseError) {

					}

				}
			)



		if (chats.type.equals("image")) {
			holder.lastmsg.text = "image"
		}
		else if (chats.type.equals("doc")){
			holder.lastmsg.text = "doc"
		}
		else {
			holder.lastmsg.text = chats.msg
		}

		if (! chats.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
			FireRef.userRef.child(chats.senderId.toString()).addValueEventListener(object : ValueEventListener {

				override fun onDataChange(snapshot : DataSnapshot) {
					var users : User? = snapshot.getValue(User::class.java)
					holder.username.text = users?.name
					if (chats.type.equals("image")) {
						holder.lastmsg.text = "Recieve image"
					} else if (chats.type.equals("doc")){
						holder.lastmsg.text = "Recieve doc"
					}
					else {
						holder.lastmsg.text = chats.msg
					}
				}

				override fun onCancelled(error : DatabaseError) {
					TODO("Not yet implemented")
				}

			})
		} else {
			holder.username.text = chats.recievername
			if (chats.type == "image") {
				holder.lastmsg.text = "Sent Image"
			}else if (chats.type.equals("doc")){
				holder.lastmsg.text = "sent doc"
			}
			else {
				holder.lastmsg.text = chats.msg
			}
		}


		holder.layout.setOnClickListener {
			if (chats.senderId.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
				click.onItemClick(position , chats.recieverId.toString())
				//	Log.d(TAG , "RecieverName: "+chats.recieverId)
			} else {
				click.onItemClick(position , chats.senderId.toString())
			}
		}

	}

	override fun getItemCount() : Int {
		return chatlist.size
	}

	class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
		val username : TextView = view.findViewById(R.id.recent_user_name)
		val lastmsg : TextView = view.findViewById(R.id.recent_last_msg)
		val userprofile : ImageView = view.findViewById(R.id.recent_user_profile)
		val layout : ConstraintLayout = view.findViewById(R.id.chat_click)
	}
}