package com.example.baatcheet.ui.auth


import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.baatcheet.R
import com.example.baatcheet.databinding.FragmentLoginBinding
import com.example.baatcheet.ui.MainActivity
import com.example.baatcheet.utilis.FireRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class LoginFragment : Fragment() {

	private lateinit var binding : FragmentLoginBinding
	//  private lateinit var navController: NavController

	var reference = FirebaseDatabase.getInstance().getReference("users")
	var mAuth = FirebaseAuth.getInstance()

	private lateinit var progressDialog : ProgressDialog


	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		// Inflate the layout for this fragment
		binding = FragmentLoginBinding.inflate(inflater , container , false)

		progressDialog = ProgressDialog(context)
		progressDialog.setTitle("Please wait...")
		progressDialog.setCanceledOnTouchOutside(false)

		//val navController : NavController
		binding.btnSignin.setOnClickListener {

			loginValidation()
		}

		binding.txtSignup.setOnClickListener {
			findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
		}


		return binding.root


	}

	private fun loginValidation() {
		val logemail = binding.etloginEmail.text.toString()
		val logpass = binding.etloginPass.text.toString()

		if (logemail.isEmpty()) {
			binding.etloginEmail.error = "Field cannot be empty"
			binding.etloginEmail.requestFocus()
			return
		} else if (logpass.isEmpty()) {
			binding.etloginPass.error = "Field cannot be empty"
			binding.etloginPass.requestFocus()
			return
		}
		logintoAccount()


	}

	private fun logintoAccount() {
		val email = binding.etloginEmail.text.toString()
		val pass = binding.etloginPass.text.toString()

		mAuth.signInWithEmailAndPassword(email , pass).addOnCompleteListener {
			if (it.isSuccessful) {
				progressDialog.dismiss()
				Toast.makeText(context , "Login Success" , Toast.LENGTH_SHORT).show()

				val sharedPreference = context?.getSharedPreferences("login" , Context.MODE_PRIVATE)
				val editor : SharedPreferences.Editor? = sharedPreference?.edit()
				editor?.putBoolean("flag" , true)
				editor?.putString("userid" , FirebaseAuth.getInstance().uid)
				editor?.apply()

				recieveAndStoreToken()

				startActivity(Intent(context , MainActivity::class.java))
				Log.d(TAG , "logintoAccount: " + sharedPreference?.getString("userid" , ""))
				activity?.finish()

			}
		}
	}

	private fun recieveAndStoreToken() {
		var data = HashMap<String , Any>()
		FirebaseMessaging.getInstance().token
			.addOnCompleteListener {
				if (it.isSuccessful) {

				//	val token : String? = it.result
					data["devicetoken"] = it.result
					val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()

					FireRef.userRef.child(userID).updateChildren(data)
				}
			}

	}

}

