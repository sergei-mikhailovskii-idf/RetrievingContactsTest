package com.example.contactstest

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val contactsPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                getContactsRequest.launch(null)
            }
        }

    private val getContactsRequest =
        registerForActivityResult(ActivityResultContracts.PickContact()) {
            val contentCursor = contentResolver.query(it, null, null, null, null)
                ?: return@registerForActivityResult

            if (contentCursor.moveToFirst()) {
                val id =
                    contentCursor.getString(contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val hasPhone =
                    contentCursor.getString(contentCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if (hasPhone.equals("1", ignoreCase = true)) {
                    val phones = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null,
                        null
                    ) ?: return@registerForActivityResult
                    phones.moveToFirst()
                    val contactNumber: String =
                        phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.d("contact", "The phone number is $contactNumber")
                    phones.close()
                }
            }
            contentCursor.close()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.text)
        text.setOnClickListener {
            contactsPermissionRequest.launch(Manifest.permission.READ_CONTACTS)
        }

    }
}