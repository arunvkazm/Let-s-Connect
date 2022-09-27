package com.example.baatcheet.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baatcheet.R
import com.example.baatcheet.`interface`.UsersInterface
import com.example.baatcheet.controller.UserAdapter
import com.example.baatcheet.databinding.FragmentUserListBinding
import com.example.baatcheet.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class UserListFragment : Fragment() , UsersInterface {

	private lateinit var binding : FragmentUserListBinding
	private lateinit var adapter : UserAdapter

	private lateinit var databaseReference : DatabaseReference

	val list : ArrayList<User> = ArrayList()


	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		// Inflate the layout for this fragment
		binding = FragmentUserListBinding.inflate(inflater , container , false)


		return binding.root
	}

	override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)


		//  Log.d(context !!.javaClass.simpleName , "ref model " + list.size)
		val layoutManager = LinearLayoutManager(context)
		binding.userListRecycler.layoutManager = layoutManager

		adapter = UserAdapter(list , this@UserListFragment)

		binding.userListRecycler.adapter = adapter

		databaseReference = FirebaseDatabase.getInstance().getReference("users")
		//  Log.d(TAG , "list: "+list)

		databaseReference.addValueEventListener(object : ValueEventListener {

			@SuppressLint("NotifyDataSetChanged")
			override fun onDataChange(snapshot : DataSnapshot) {

				list.clear()
				for (dataSnapshot in snapshot.children) {
					// Log.d(context !!.javaClass.simpleName , "ref " + dataSnapshot.key)
					val userModel : User? = dataSnapshot.getValue(User::class.java)
					if (! userModel?.userId.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
						list.add(userModel !!)
					}
				}

				adapter.notifyDataSetChanged()
			}

			override fun onCancelled(error : DatabaseError) {}
		})


	}


	override fun onItemClick( recieverID : String) {
		val bundle = Bundle()
		bundle.putString("recieverID" , recieverID)
		findNavController().navigate(R.id.action_userListFragment_to_chatFragment , bundle)
	}


}