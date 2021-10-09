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

import java.util.*

/**
 * Implementations encode according to some scheme for encoding contact information, like VCard or
 * MECARD.
 *
 * @author Sean Owen
 */
abstract class ContactEncoder {
    /**
     * @return first, the best effort encoding of all data in the appropriate format; second, a
     * display-appropriate version of the contact information
     */
    abstract fun encode(
        names: List<String?>?,
        organization: String?,
        addresses: List<String?>?,
        phones: List<String?>?,
        phoneTypes: List<String?>?,
        emails: List<String?>?,
        urls: List<String?>?,
        note: String?
    ): Array<String?>?

    companion object {
        /**
         * @return null if s is null or empty, or result of s.trim() otherwise
         */
        fun trim(s: String?): String? {
            if (s == null) {
                return null
            }
            val result = s.trim { it <= ' ' }
            return if (result.isEmpty()) null else result
        }

        @JvmStatic
        fun append(
            newContents: StringBuilder,
            newDisplayContents: StringBuilder,
            prefix: String?,
            value: String?,
            fieldFormatter: Formatter,
            terminator: Char
        ) {
            val trimmed = trim(value)
            if (trimmed != null) {
                newContents.append(prefix).append(fieldFormatter.format(trimmed, 0))
                    .append(terminator)
                newDisplayContents.append(trimmed).append('\n')
            }
        }

        @JvmStatic
        fun appendUpToUnique(
            newContents: StringBuilder,
            newDisplayContents: StringBuilder,
            prefix: String?,
            values: List<String?>?,
            max: Int,
            displayFormatter: Formatter?,
            fieldFormatter: Formatter,
            terminator: Char
        ) {
            values ?: return
            var count = 0
            val uniques: MutableCollection<String> = HashSet(2)
            for (i in values.indices) {
                val value = values[i]
                val trimmed = trim(value)
                if (trimmed != null && trimmed.isNotEmpty() && !uniques.contains(trimmed)) {
                    newContents.append(prefix).append(fieldFormatter.format(trimmed, i))
                        .append(terminator)
                    val display: CharSequence =
                        displayFormatter?.format(value = trimmed, index = i) ?: trimmed
                    newDisplayContents.append(display).append('\n')
                    if (++count == max) {
                        break
                    }
                    uniques.add(trimmed)
                }
            }
        }
    }
}