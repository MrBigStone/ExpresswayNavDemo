/*
 * Copyright (C) 2011 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bigstone.expresswaynav.ext.code.encode

import android.provider.ContactsContract
import java.util.*
import kotlin.collections.HashMap

/**
 * Encodes contact information according to the vCard format.
 *
 * @author Sean Owen
 */
internal class VCardContactEncoder : ContactEncoder() {
    override fun encode(
        names: List<String?>?,
        organization: String?,
        addresses: List<String?>?,
        phones: List<String?>?,
        phoneTypes: List<String?>?,
        emails: List<String?>?,
        urls: List<String?>?,
        note: String?
    ): Array<String?> {
        val newContents = StringBuilder(100)
        newContents.append("BEGIN:VCARD").append(TERMINATOR)
        newContents.append("VERSION:3.0").append(TERMINATOR)
        val newDisplayContents = StringBuilder(100)
        val fieldFormatter: Formatter = VCardFieldFormatter()
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "N",
            values = names,
            max = 1,
            displayFormatter = null,
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        append(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "ORG",
            value = organization,
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "ADR",
            values = addresses,
            max = 1,
            displayFormatter = null,
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        val phoneMetadata = buildPhoneMetadata(phones, phoneTypes)
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "TEL",
            values = phones,
            max = Int.MAX_VALUE,
            displayFormatter = VCardTelDisplayFormatter(phoneMetadata),
            fieldFormatter = VCardFieldFormatter(phoneMetadata),
            terminator = TERMINATOR
        )
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "EMAIL",
            values = emails,
            max = Int.MAX_VALUE,
            displayFormatter = null,
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "URL",
            values = urls,
            max = Int.MAX_VALUE,
            displayFormatter = null,
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        append(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "NOTE",
            value = note,
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        newContents.append("END:VCARD").append(TERMINATOR)
        return arrayOf(newContents.toString(), newDisplayContents.toString())
    }

    companion object {
        private const val TERMINATOR = '\n'
        private fun buildPhoneMetadata(
            phones: Collection<String?>?,
            phoneTypes: List<String?>?
        ): List<Map<String, Set<String>>>? {
            if (phoneTypes == null || phoneTypes.isEmpty()) {
                return null
            }
            val metadataForIndex: MutableList<Map<String, Set<String>>> = ArrayList()
            for (i in phones!!.indices) {
                if (phoneTypes.size <= i) {
                    metadataForIndex.add(HashMap())
                } else {
                    val metadata: MutableMap<String, Set<String>> = HashMap()
                    metadataForIndex.add(metadata)
                    val typeTokens: MutableSet<String> = HashSet()
                    metadata["TYPE"] = typeTokens
                    val typeString = phoneTypes[i]
                    val androidType = maybeIntValue(typeString)
                    if (androidType == null) {
                        typeTokens.add(typeString ?: "")
                    } else {
                        val purpose = vCardPurposeLabelForAndroidType(androidType)
                        val context = vCardContextLabelForAndroidType(androidType)
                        if (purpose != null) {
                            typeTokens.add(purpose)
                        }
                        if (context != null) {
                            typeTokens.add(context)
                        }
                    }
                }
            }
            return metadataForIndex
        }

        private fun maybeIntValue(value: String?): Int? {
            return value?.toIntOrNull()
        }

        private fun vCardPurposeLabelForAndroidType(androidType: Int): String? {
            return when (androidType) {
                ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX -> "fax"
                ContactsContract.CommonDataKinds.Phone.TYPE_PAGER, ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER -> "pager"
                ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD -> "textphone"
                ContactsContract.CommonDataKinds.Phone.TYPE_MMS -> "text"
                else -> null
            }
        }

        private fun vCardContextLabelForAndroidType(androidType: Int): String? {
            return when (androidType) {
                ContactsContract.CommonDataKinds.Phone.TYPE_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_PAGER -> "home"
                ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN, ContactsContract.CommonDataKinds.Phone.TYPE_WORK, ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK, ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER -> "work"
                else -> null
            }
        }
    }
}