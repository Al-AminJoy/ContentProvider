package com.alamin.contentprovider

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var contactList: ArrayList<String>

    private val registerActivityForResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){

        if (it){
            getContactList();
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.txtContactName)
        contactList = getContactList()
        var stringBuilder: StringBuilder = StringBuilder()

        for (data in contactList){
            stringBuilder.append(data)
        }

        textView.text = stringBuilder.toString()

    }

    @SuppressLint("Range")
    private fun getContactList() : ArrayList<String> {
        var contactList = ArrayList<String>()

        isAboveOreo {

            isPermissionGranted(this,android.Manifest.permission.READ_CONTACTS){
                if (it){
                    val contentResolver = applicationContext.contentResolver
                    val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null)
                    if (cursor?.moveToFirst() == true){
                        do {
                            var name = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))+"\n"
                            contactList.add(name!!)

                        }while (cursor.moveToNext())
                    }
                }else{
                    registerActivityForResult.launch(android.Manifest.permission.READ_CONTACTS)
                }
            }

        }

        return contactList

    }

    private fun isAboveOreo(call : () -> Unit){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            call.invoke()
        }
    }

    private fun isPermissionGranted(context:Context, permission: String, call:(Boolean) -> Unit){

        if (ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED){
            call.invoke(true)
        }else{
            call.invoke(false)
        }
    }
}