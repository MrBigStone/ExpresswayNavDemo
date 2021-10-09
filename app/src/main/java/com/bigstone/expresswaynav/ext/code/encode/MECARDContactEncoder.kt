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

import android.telephony.PhoneNumberUtils
import java.util.regex.Pattern

/**
 * Encodes contact information according to the MECARD format.
 *
 * @author Sean Owen
 */
class MECARDContactEncoder : ContactEncoder() {
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
        newContents.append("MECARD:")
        val newDisplayContents = StringBuilder(100)
        val fieldFormatter: Formatter = MECARDFieldFormatter()
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "N",
            values = names,
            max = 1,
            displayFormatter = MECARDNameDisplayFormatter(),
            fieldFormatter = fieldFormatter,
            terminator = TERMINATOR
        )
        append(newContents, newDisplayContents, "ORG", organization, fieldFormatter, TERMINATOR)
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
        appendUpToUnique(
            newContents = newContents,
            newDisplayContents = newDisplayContents,
            prefix = "TEL",
            values = phones,
            max = Int.MAX_VALUE,
            displayFormatter = MECARDTelDisplayFormatter(),
            fieldFormatter = fieldFormatter,
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
        newContents.append(';')
        return arrayOf(newContents.toString(), newDisplayContents.toString())
    }

    private class MECARDFieldFormatter : Formatter {
        override fun format(value: CharSequence, index: Int): CharSequence {
            return ':'.toString() + NEWLINE.matcher(
                RESERVED_ME_CARD_CHARS.matcher(value).replaceAll("\\\\$1")
            ).replaceAll("")
        }

        companion object {
            private val RESERVED_ME_CARD_CHARS = Pattern.compile("([\\\\:;])")
            private val NEWLINE = Pattern.compile("\\n")
        }
    }

    private class MECARDTelDisplayFormatter : Formatter {
        override fun format(value: CharSequence, index: Int): CharSequence {
            return NOT_DIGITS.matcher(PhoneNumberUtils.formatNumber(value.toString()))
                .replaceAll("")
        }

        companion object {
            private val NOT_DIGITS = Pattern.compile("[^0-9]+")
        }
    }

    private class MECARDNameDisplayFormatter : Formatter {
        override fun format(value: CharSequence, index: Int): CharSequence {
            return COMMA.matcher(value).replaceAll("")
        }

        companion object {
            private val COMMA = Pattern.compile(",")
        }
    }

    companion object {
        private const val TERMINATOR = ';'
    }
}