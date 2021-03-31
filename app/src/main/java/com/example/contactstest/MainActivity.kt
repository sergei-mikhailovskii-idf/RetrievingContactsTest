package com.example.contactstest

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.provider.ContactsContract.CommonDataKinds.Phone


class MainActivity : AppCompatActivity() {

    private val contactPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                getContactRequest.launch(null)
            }
        }

    private val contactsPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                getContacts()
            }
        }

    private val getContactRequest =
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
                        Phone.CONTENT_URI,
                        null,
                        Phone.CONTACT_ID + " = " + id,
                        null,
                        null
                    ) ?: return@registerForActivityResult
                    phones.moveToFirst()
                    val contactNumber: String =
                        phones.getString(phones.getColumnIndex(Phone.NUMBER))
                    Log.d("contact", "The phone number is $contactNumber")
                    phones.close()
                }
            }
            contentCursor.close()
        }

    private fun getContacts() {
        val managedCursor = contentResolver.query(
            Phone.CONTENT_URI,
            arrayOf(Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER),
            null,
            null,
            Phone.DISPLAY_NAME + " ASC"
        )

        if (managedCursor?.moveToFirst() == true) {
            Log.d(MainActivity::class.java.simpleName, "Size: ${managedCursor.count}")
            while (!managedCursor.isAfterLast) {
                Log.d(
                    MainActivity::class.java.simpleName,
                    "Phone: ID: ${Phone._ID}, DISPLAY_NAME: ${Phone.DISPLAY_NAME}, NUMBER: ${Phone.NUMBER}"
                )
                managedCursor.moveToNext()
            }
        }

        managedCursor?.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvRetrievePhoneNumber = findViewById<TextView>(R.id.tv_retrieve_phone_number)
        val tvRetrieveContactsList = findViewById<TextView>(R.id.tv_retrieve_contacts_list)

        tvRetrievePhoneNumber.setOnClickListener {
            contactPermissionRequest.launch(Manifest.permission.READ_CONTACTS)
        }

        tvRetrieveContactsList.setOnClickListener {
            contactsPermissionRequest.launch(Manifest.permission.READ_CONTACTS)
        }

    }
}