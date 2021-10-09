package com.bigstone.expresswaynav.ext.code.encode

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract

/**
 * Created by hupei on 2016/8/25.
 */
class ParserUriToVCard {

    @SuppressLint("Recycle")
    fun parserUri(context: Context?, contactUri: Uri?): Bundle? {
        if (context == null || contactUri == null) return null
        val resolver = context.contentResolver
        val cursor: Cursor = try {
            resolver.query(contactUri, null, null, null, null)
        } catch (ignored: IllegalArgumentException) {
            return null
        } ?: return null
        val id: String
        val name: String?
        val hasPhone: Boolean
        cursor.use {
            if (!it.moveToFirst()) return null
            id = it.getString(it.getColumnIndex(BaseColumns._ID))
            name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            hasPhone = it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
        }
        val bundle = Bundle()
        if (name != null && name.isNotEmpty()) {
            bundle.putString(ContactsContract.Intents.Insert.NAME, massageContactData(name))
        }
        if (hasPhone) {
            resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + '=' + id,
                null,
                null
            )?.use { phonesCursor ->
                var foundPhone = 0
                val phonesNumberColumn =
                    phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val phoneTypeColumn =
                    phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                while (phonesCursor.moveToNext() && foundPhone < PHONE_KEYS.size) {
                    val number = phonesCursor.getString(phonesNumberColumn)
                    if (number != null && number.isNotEmpty()) {
                        bundle.putString(PHONE_KEYS[foundPhone], massageContactData(number))
                    }
                    val type = phonesCursor.getInt(phoneTypeColumn)
                    bundle.putInt(PHONE_TYPE_KEYS[foundPhone], type)
                    foundPhone++
                }
            }
        }
        resolver.query(
            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + '=' + id,
            null,
            null
        )?.use { methodsCursor ->
            if (methodsCursor.moveToNext()) {
                val data = methodsCursor.getString(
                    methodsCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                )
                if (data != null && data.isNotEmpty()) {
                    bundle.putString(
                        ContactsContract.Intents.Insert.POSTAL,
                        massageContactData(data)
                    )
                }
            }
        }
        resolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + '=' + id,
            null,
            null
        )?.use { emailCursor ->
            var foundEmail = 0
            val emailColumn =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
            while (emailCursor.moveToNext() && foundEmail < EMAIL_KEYS.size) {
                val email = emailCursor.getString(emailColumn)
                if (email != null && email.isNotEmpty()) {
                    bundle.putString(EMAIL_KEYS[foundEmail], massageContactData(email))
                }
                foundEmail++
            }
        }
        return if (bundle.isEmpty) null else bundle
    }

    companion object {
        const val URL_KEY = "URL_KEY"
        const val NOTE_KEY = "NOTE_KEY"

        @JvmField
        val PHONE_KEYS = arrayOf(
            ContactsContract.Intents.Insert.PHONE,
            ContactsContract.Intents.Insert.SECONDARY_PHONE,
            ContactsContract.Intents.Insert.TERTIARY_PHONE
        )

        @JvmField
        val PHONE_TYPE_KEYS = arrayOf(
            ContactsContract.Intents.Insert.PHONE_TYPE,
            ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE,
            ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE
        )

        @JvmField
        val EMAIL_KEYS = arrayOf(
            ContactsContract.Intents.Insert.EMAIL,
            ContactsContract.Intents.Insert.SECONDARY_EMAIL,
            ContactsContract.Intents.Insert.TERTIARY_EMAIL
        )

        private fun massageContactData(data: String): String {
            var mData = data
            if (mData.indexOf('\n') >= 0) {
                mData = mData.replace("\n", " ")
            }
            if (mData.indexOf('\r') >= 0) {
                mData = mData.replace("\r", " ")
            }
            return mData
        }
    }
}