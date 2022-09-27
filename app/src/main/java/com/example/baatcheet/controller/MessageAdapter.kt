package com.example.baatcheet.controller

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.baatcheet.R
import com.example.baatcheet.model.Message
import com.example.baatcheet.model.User
import com.example.baatcheet.utilis.FireRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(var context : Context , var senderId : String , private var msgList : ArrayList<Message>) :
	RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

	private val MSG_TYPE_LEFT = 0
	private val MSG_TYPE_RIGHT = 1

	override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ViewHolder {
		if (viewType == MSG_TYPE_RIGHT) {
			val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_send_msg , parent , false)
			return ViewHolder(view)
		} else {
			val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_recieve_msg , parent , false)
			return ViewHolder(view)
		}

	}


	override fun onBindViewHolder(holder : ViewHolder , position : Int) {
		val message = msgList[position]


		if (message.type == "image") {
			holder.txtlayout.visibility = View.GONE
			holder.imglayout.visibility = View.VISIBLE
			Picasso.get().load(message.msg).into(holder.showImgMsg)
		} else {
			holder.txtlayout.visibility = View.VISIBLE
			holder.imglayout.visibility = View.GONE
			holder.showTextMsg.text = message.msg
		}


//		FireRef.userRef.child(senderId).addValueEventListener(
//			object : ValueEventListener {
//
//				override fun onDataChange(snapshot : DataSnapshot) {
//					val user = snapshot.getValue(User::class.java)
//					Picasso.get().load(user?.profileimg).into(holder.chat_profileImg)
//				}
//
//
//				override fun onCancelled(error : DatabaseError) {
//					TODO("Not yet implemented")
//				}
//
//			}
//		)

//		FireRef.userRef.child(message.recieverId.toString()).addValueEventListener(
//			object : ValueEventListener {
//
//				override fun onDataChange(snapshot : DataSnapshot) {
//					val user = snapshot.getValue(User::class.java)
//					Picasso.get().load(user?.profileimg).into(holder.chat_profileImg)
//				}
//
//
//				override fun onCancelled(error : DatabaseError) {
//					TODO("Not yet implemented")
//				}
//
//			}
//		)

	}


	override fun getItemCount() : Int {
		return msgList.size

	}

	override fun getItemViewType(position : Int) : Int {
		val chats = msgList[position].senderId

		val sharedPreferences : SharedPreferences = context.getSharedPreferences("login" , AppCompatActivity.MODE_PRIVATE)
		val id = sharedPreferences.getString("userid" , "")

		if (chats.equals(id)) {
			return MSG_TYPE_RIGHT
		} else {
			return MSG_TYPE_LEFT
		}
	}

	class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
		val showTextMsg : TextView = view.findViewById(R.id.text_msg)
		val showImgMsg : ImageView = view.findViewById(R.id.send_message_image)
		//val chat_profileImg : CircleImageView = view.findViewById(R.id.rec_profile)
		val txtlayout : RelativeLayout = view.findViewById(R.id.textMsg_container)
		val imglayout : RelativeLayout = view.findViewById(R.id.imgMSg_container)



	}
}