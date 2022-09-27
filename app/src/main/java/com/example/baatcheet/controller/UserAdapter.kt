package com.example.baatcheet.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.baatcheet.`interface`.UsersInterface
import com.example.baatcheet.model.User
import com.example.baatcheet.R
import com.squareup.picasso.Picasso


class UserAdapter(private val userlist : ArrayList<User>, private val click:UsersInterface) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


	override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.users_items_containers , parent , false)
		return ViewHolder(view)
	}


	override fun onBindViewHolder(holder : ViewHolder , position : Int) {
		val user = userlist[position]
		//var navController: NavController? = null
		holder.useremail.text = user.email
		holder.username.text = user.name
		Picasso.get().load(user.profileimg).into(holder.userprofile)
		holder.layout.setOnClickListener {

			click.onItemClick(user.userId.toString())
		}
	}


	override fun getItemCount() : Int {
		return userlist.size
	}

	class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
		val username : TextView = view.findViewById(R.id.user_name)
		val useremail : TextView = view.findViewById(R.id.user_email)
		val userprofile : ImageView = view.findViewById(R.id.user_profile)
		val layout : ConstraintLayout = view.findViewById(R.id.user_click)
	}


}