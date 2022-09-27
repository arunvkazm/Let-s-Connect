package com.example.baatcheet.ui.auth

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.baatcheet.R
import com.example.baatcheet.databinding.FragmentRegisterBinding
import com.example.baatcheet.utilis.FireRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageMetadata


class RegisterFragment : Fragment() {

	private lateinit var binding : FragmentRegisterBinding
	private val GALLERY_REQ_CODE = 1000


	//private var Mreference = FirebaseDatabase.getInstance().getReference("users")
	private var data = HashMap<String , Any>()
	private lateinit var imgUri : Uri

	private lateinit var progressDialog : ProgressDialog

	// Initialize Firebase Auth
	var mAuth = FirebaseAuth.getInstance()


	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View? {
		// Inflate the layout for this fragment
		binding = FragmentRegisterBinding.inflate(inflater , container , false)


		progressDialog = ProgressDialog(context)
		progressDialog.setTitle("Please wait...")
		progressDialog.setCanceledOnTouchOutside(false)


		//Select Profile Image
		binding.profileImage.setOnClickListener {
			val intent = Intent(Intent.ACTION_PICK)
			intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			startActivityForResult(intent , GALLERY_REQ_CODE)
		}

		binding.btnSignup.setOnClickListener {
			Validation()
		}

		binding.txtSignin.setOnClickListener{
			findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

		}


		return binding.root
	}

	private fun Validation() {
		val name = binding.etregName.text.toString()
		val email = binding.etregEmail.text.toString()
		val mob = binding.etregMob.text.toString()
		val pass = binding.etregPass.text.toString()

		if (name.isEmpty()) {
			binding.etregName.error = ("Field cannot be empty")
			binding.etregName.requestFocus()
			return
		} else if (email.isEmpty()) {
			binding.etregEmail.error = ("Field cannot be empty")
			binding.etregEmail.requestFocus()
			return
		} else if (! Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			binding.etregName.error = ("Please Enter Valid email")
			binding.etregName.requestFocus()
			return
		} else if (mob.isEmpty()) {
			binding.etregMob.error = ("Field cannot be empty")
			binding.etregMob.requestFocus()
			return
		} else if (pass.isEmpty()) {
			binding.etregPass.error = ("Field cannot be empty")
			binding.etregPass.requestFocus()
			return
		} else {
			createAccount()
		}


	}


	private fun createAccount() {
		val regemail = binding.etregEmail.text.toString()
		val regpass = binding.etregPass.text.toString()

		mAuth.createUserWithEmailAndPassword(regemail , regpass).addOnCompleteListener {
			if (it.isSuccessful) {
				progressDialog.setMessage("Creating Account...")
				progressDialog.show()
				sendDatatoDatabase(mAuth.currentUser?.uid)
			}
		}

	}

	private fun sendDatatoDatabase(uid : String?) {
		val storageMetadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
		val name = System.currentTimeMillis().toString() + "jpeg"
		val reference = FireRef.userProfileRef.child(name)
		Log.d(TAG , "sendDatatoDatabase: " + name)

		reference.putFile(imgUri).addOnSuccessListener {
			reference.downloadUrl.addOnSuccessListener {
				data["name"] = binding.etregName.text.toString()
				data["email"] = binding.etregEmail.text.toString()
				data["phone"] = binding.etregMob.text.toString()
				data["password"] = binding.etregPass.text.toString()
				data["userId"] = FirebaseAuth.getInstance().uid.toString()
				data["profileimg"] = it.toString()

				FireRef.userRef.child(uid !!).setValue(data).addOnCompleteListener {
					progressDialog.dismiss()
					recieveAndStoreToken()

					findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
				}
				binding.etregName.text?.clear()
				binding.etregEmail.text?.clear()
				binding.etregMob.text?.clear()
				binding.etregPass.text?.clear()
				binding.profileImage.setImageResource(R.drawable.profile_image)
//				Toast.makeText(context , "Uploaded" , Toast.LENGTH_LONG).show()
			}
		}

	}

	private fun recieveAndStoreToken() {
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

	override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY_REQ_CODE) {
				binding.profileImage.setImageURI(data !!.data)
				imgUri = data.data !!
				//Log.d(TAG , "DATA: " + imgUri)

			}
		}
	}

}
