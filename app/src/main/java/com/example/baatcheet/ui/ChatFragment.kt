package com.example.baatcheet.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baatcheet.R
import com.example.baatcheet.controller.MessageAdapter
import com.example.baatcheet.databinding.FragmentChatBinding
import com.example.baatcheet.model.Message
import com.example.baatcheet.model.User
import com.example.baatcheet.utilis.FireRef
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference


class ChatFragment : Fragment() {

	private lateinit var binding : FragmentChatBinding
	private var recieverdata : User? = null
	private lateinit var adapter : MessageAdapter
	private lateinit var mCtx : Context
	private val GALLERY_REQ_CODE = 10
	private val DOC_REQ_CODE = 100
	private val VIDEO_REQ_CODE = 1000
	private val AUDIO_REQ_CODE = 10000
	private val CONTACT_REQ_CODE = 1
	private lateinit var uri : Uri
	val intent = Intent()


	var chaList : ArrayList<Message> = ArrayList()

	lateinit var senderId : String
	var recieverID = ""
	lateinit var senderKey : String
	lateinit var recieverKey : String


	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		// Inflate the layout for this fragment
		binding = FragmentChatBinding.inflate(inflater , container , false)

		mCtx = container !!.context

		return binding.root

	}

	override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)

		binding.chatToolbar.setNavigationOnClickListener {

			findNavController().popBackStack()
		}

		recieverID = arguments?.getString("recieverID").toString()
		Log.d(TAG , "onCreateView: $recieverID")

		senderId = FirebaseAuth.getInstance().currentUser?.uid.toString()
		Log.d(TAG , "senderKey: " + senderId)
		senderKey = senderId + "_chat_" + recieverID
		recieverKey = recieverID + "_chat_" + senderId

		getRecieverData(recieverID)


		val layoutManager = LinearLayoutManager(context)
		binding.chatsRecyclerView.layoutManager = layoutManager
		adapter = MessageAdapter(mCtx , senderId , chaList)
		binding.chatsRecyclerView.adapter = (adapter)



		binding.btnAttachFile.setOnClickListener {
			showBottomSheet()
		}





		binding.sendmessage.setOnClickListener {
			val message = binding.etChatMessage.text.toString()
			if (message != "") {
				sendMessage(message , "text")
				binding.etChatMessage.text?.clear()
			} else {
				binding.etChatMessage.text?.clear()
			}
		}


	}

	private fun showBottomSheet() {
		val sheetDialog = BottomSheetDialog(mCtx , R.style.AppBottomSheetDialogTheme)
		sheetDialog.setContentView(R.layout.sheet_layout)
		sheetDialog.show()

		// open Camera
		sheetDialog.findViewById<LinearLayout>(R.id.camera_click)?.setOnClickListener {
			val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			startActivityForResult(cameraIntent , GALLERY_REQ_CODE)

		}

		//open VIdeoa
		sheetDialog.findViewById<LinearLayout>(R.id.video_click)?.setOnClickListener {
			val intent = Intent(Intent.ACTION_PICK)
			intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			startActivityForResult(intent , VIDEO_REQ_CODE)
			sheetDialog.hide()
		}

		//Open Gallery
		sheetDialog.findViewById<LinearLayout>(R.id.gallery_click)?.setOnClickListener {
			val intent = Intent(Intent.ACTION_PICK)
			intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			startActivityForResult(intent , GALLERY_REQ_CODE)
			sheetDialog.hide()
		}

		//Open Document
		sheetDialog.findViewById<LinearLayout>(R.id.files_click)?.setOnClickListener {
			val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
			pdfIntent.type = "application/pdf"
			pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
			startActivityForResult(pdfIntent , DOC_REQ_CODE)
		}

		sheetDialog.findViewById<LinearLayout>(R.id.audio_click)?.setOnClickListener {

			intent.type = "audio/*"
			intent.action = Intent.ACTION_GET_CONTENT
			startActivityForResult(intent ,AUDIO_REQ_CODE)
		}

		sheetDialog.findViewById<LinearLayout>(R.id.contact_click)?.setOnClickListener {
			val intent = Intent(
				Intent.ACTION_PICK ,
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI
			)
			startActivityForResult(intent , CONTACT_REQ_CODE)
		}


	}


	override fun onStart() {
		super.onStart()
		getChat()
	}

	private fun sendMessage(message : String , type : String) {
		val value : HashMap<String , String> = HashMap()
		value["recieverId"] = recieverID
		value["senderId"] = senderId
		value["msg"] = message
		value["type"] = type
		value["currenttime"] = System.currentTimeMillis().toString()
		value["recievername"] = recieverdata?.name.toString()

		val pushKey = FireRef.chatRef.push().key

		FireRef.chatRef.child(senderKey)
			.child(pushKey !!).setValue(value)
			.addOnCompleteListener {
				if (it.isSuccessful) {
					chaList(value , senderId , recieverID)

				}
			}
		FireRef.chatRef.child(recieverKey).child(pushKey).setValue(value)
			.addOnCompleteListener {
				if (it.isSuccessful) {
					chaList(value , recieverID , senderId)
				}
			}
	}

	private fun chaList(
		value : HashMap<String , String> , senderID : String , recieverID : String
	) {
		FireRef.chatListRef.child(senderID)
			.child(recieverID)
			.setValue(value)
			.addOnCompleteListener {

			}
	}

	private fun getRecieverData(recieverID : String) {
		FireRef.userRef.child(recieverID)
			.addValueEventListener(object : ValueEventListener {

				override fun onDataChange(snapshot : DataSnapshot) {
					recieverdata = snapshot.getValue(User::class.java)
					binding.chatToolbar.title = recieverdata?.name.toString()
					Log.d(javaClass.simpleName , "DATA" + recieverdata)
				}

				override fun onCancelled(error : DatabaseError) {}
			})
	}


	private fun getChat() {

		FireRef.chatRef.child(senderKey)
			.addValueEventListener(object : ValueEventListener {
				//			@SuppressLint("NotifyDataSetChanged")

				@SuppressLint("NotifyDataSetChanged")
				override fun onDataChange(snapshot : DataSnapshot) {
					Log.d(TAG , "senderKey: " + senderKey)
					chaList.clear()
					for (snapshot : DataSnapshot in snapshot.children) {

						chaList.add(
							snapshot
								.getValue(Message::class.java) !!
						)
					}
					try {
						binding.chatsRecyclerView
							.smoothScrollToPosition(adapter.itemCount - 1)
					} catch (e : Exception) {
						e.printStackTrace()
					}
					adapter.notifyDataSetChanged()
				}

				override fun onCancelled(error : DatabaseError) {

				}

			})
	}

	override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (resultCode == Activity.RESULT_OK) {

			when (requestCode) {
				GALLERY_REQ_CODE -> {
					uri = data?.data !!
					sendImage(uri)

				}
				DOC_REQ_CODE -> {
					uri = data?.data !!
					sendPdf(uri)
				}
				VIDEO_REQ_CODE -> {
					uri = data?.data !!
					Log.d(TAG , "VIDEOURI: $uri")
					sendVideo(uri)

				}
				AUDIO_REQ_CODE -> {
					uri = data?.data!!
					sendAudio(uri)
				}
				CONTACT_REQ_CODE -> {
					uri = data?.data!!
					sendContact(uri)
				}
			}
		}
	}

	private fun sendContact(uri : Uri) {
		val storageMetadata = StorageMetadata.Builder().setContentType("contact/vcf").build()
		val name = System.currentTimeMillis().toString() + ".vcf"
		val storageReference : StorageReference = FireRef.chatContactRef.child(name)

		storageReference.putFile(uri,storageMetadata).addOnCompleteListener{
			if (it.isSuccessful){
				storageReference.downloadUrl.addOnSuccessListener {
					var url = it.toString()
					sendMessage(url , "contact")
				}
			}
			else {
				Toast.makeText(context , "Contact sending failed" , Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun sendAudio(uri : Uri) {
		val storageMetadata = StorageMetadata.Builder().setContentType("audio/mp3").build()
		val name = System.currentTimeMillis().toString() + ".mp3"
		val storageReference : StorageReference = FireRef.chatAudioRef.child(name)

		storageReference.putFile(uri,storageMetadata).addOnCompleteListener{
			if (it.isSuccessful){
				storageReference.downloadUrl.addOnSuccessListener {
					var url = it.toString()
					sendMessage(url , "audio")
				}
			}
			else {
				Toast.makeText(context , "Audio sending failed" , Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun sendVideo(uri : Uri) {
		val storageMetadata = StorageMetadata.Builder().setContentType("video/mp4").build()
		val name = System.currentTimeMillis().toString() + ".mp4"
		val storageReference : StorageReference = FireRef.chatVideoRef.child(name)

		storageReference.putFile(uri , storageMetadata).addOnCompleteListener {
			if (it.isSuccessful) {
				storageReference.downloadUrl.addOnSuccessListener {
					var url = it.toString()
					sendMessage(url , "video")
				}
			} else {
				Toast.makeText(context , "Video sending failed" , Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun sendPdf(uri : Uri) {
		val storageMetadata = StorageMetadata.Builder().setContentType("application/pdf").build()
		val name = System.currentTimeMillis().toString() + ".pdf"
		val storageReference : StorageReference = FireRef.chatdocRef.child(name)

		storageReference.putFile(uri , storageMetadata).addOnCompleteListener {
			if (it.isSuccessful) {
				storageReference.downloadUrl.addOnSuccessListener {
					var url = it.toString()
					sendMessage(url , "doc")
				}
			} else {
				Toast.makeText(context , "Document sending failed" , Toast.LENGTH_SHORT).show()
			}
		}

	}

	private fun sendImage(uri : Uri) {
		val storageMetadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
		val name = System.currentTimeMillis().toString() + ".jpeg"
		var storageReference : StorageReference = FireRef.chatImagesRef.child(name)

		storageReference.putFile(uri , storageMetadata).addOnCompleteListener {
			if (it.isSuccessful) {
				storageReference.downloadUrl.addOnSuccessListener {
					var url = it.toString()
					sendMessage(url , "image")
				}
			} else {
				Toast.makeText(context , "Image sending failed" , Toast.LENGTH_SHORT).show()
			}
		}
	}


}
