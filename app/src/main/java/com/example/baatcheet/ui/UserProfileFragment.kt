package com.example.baatcheet.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.baatcheet.R
import com.example.baatcheet.databinding.FragmentUserProfileBinding
import com.example.baatcheet.model.User
import com.example.baatcheet.utilis.FireRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageMetadata
import com.squareup.picasso.Picasso


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private val GALLERY_REQ_CODE = 1000
    private  var imgUri : Uri? = null
    private lateinit var progressDialog : ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      binding = FragmentUserProfileBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)



        binding.back.setOnClickListener{
            findNavController().popBackStack()
        }


        //Fetch data from DB

        FireRef.userRef.child(FirebaseAuth.getInstance()
            .currentUser?.uid.toString())
            .addValueEventListener(
                object :ValueEventListener{

                    override fun onDataChange(snapshot : DataSnapshot) {
                        val user:User? =snapshot.getValue(User::class.java)
                        if (user?.profileimg == ""){
                            binding.userprofile.setImageResource(R.drawable.profile)
                        } else{
                            Picasso.get().load(user?.profileimg).into(binding.userprofile)
                        }

                        binding.userprofilename.setText(user?.name)
                        binding.usrprofileemail.setText(user?.email)
                        binding.userprofilemobile.setText(user?.phone)
                        binding.userprofilepassword.setText(user?.password)

                    }

                    override fun onCancelled(error : DatabaseError) {
                        Toast.makeText( context, error.message , Toast.LENGTH_SHORT).show()
                    }

                }
            )

        binding.edit.setOnClickListener {
            binding.edit.visibility = View.GONE
            binding.save.visibility = View.VISIBLE
            binding.imgCard.visibility = View.VISIBLE
            setViewEnabled(true)
        }

            binding.cgImage.setOnClickListener{
                val intent = Intent(Intent.ACTION_PICK)
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(intent , GALLERY_REQ_CODE)
            }

        binding.save.setOnClickListener{
            updateData()
        }

    }

    private fun updateData() {
        progressDialog.setMessage("Updating Your Profile...")
        progressDialog.show()
        val storageMetadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
        val name = System.currentTimeMillis().toString() + "jpeg"
        val reference = FireRef.userProfileRef.child(name)
        Log.d(ContentValues.TAG , "sendDatatoDatabase: " + name)

        reference.putFile(imgUri !!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener {
                var data = HashMap<String , Any>()
                data["name"] = binding.userprofilename.text.toString()
                data["email"] = binding.usrprofileemail.text.toString()
                data["phone"] = binding.userprofilemobile.text.toString()
                data["password"] = binding.userprofilepassword.text.toString()
                data["profileimg"] = it.toString()

                FireRef.userRef.child(FirebaseAuth.getInstance().currentUser !!.uid)
                    .updateChildren(data).addOnCompleteListener {
                        progressDialog.dismiss()
                        Toast.makeText(context , "User Profile Updated" , Toast.LENGTH_SHORT).show()
                }
                binding.save.visibility = View.GONE
                binding.edit.visibility = View.VISIBLE
                binding.imgCard.visibility = View.GONE
                setViewEnabled(false)
            }
        }
    }


    private fun setViewEnabled(status : Boolean) {
        binding.userprofilename.isEnabled = status
        binding.usrprofileemail.isEnabled = status
        binding.userprofilemobile.isEnabled = status
        binding.userprofilepassword.isEnabled = status

    }

    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                binding.userprofile.setImageURI(data !!.data)
                imgUri = data.data !!
                Log.d(ContentValues.TAG , "DATA: " + imgUri)

            }
        }
    }


}

