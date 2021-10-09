/*
 * Copyright (C) 2014 ZXing authors
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

import java.util.regex.Pattern

/**
 * @author Sean Owen
 */
internal class VCardFieldFormatter @JvmOverloads constructor(private val metadataForIndex: List<Map<String, Set<String>>>? = null) :
    Formatter {
    override fun format(value: CharSequence, index: Int): CharSequence {
        var mValue = value
        mValue = RESERVED_VCARD_CHARS.matcher(mValue).replaceAll("\\\\$1")
        mValue = NEWLINE.matcher(mValue).replaceAll("")
        val metadata =
            if (metadataForIndex == null || metadataForIndex.size <= index) null else metadataForIndex[index]
        mValue = formatMetadata(mValue, metadata)
        return mValue
    }

    companion object {
        private val RESERVED_VCARD_CHARS = Pattern.compile("([\\\\,;])")
        private val NEWLINE = Pattern.compile("\\n")
        private fun formatMetadata(
            value: CharSequence,
            metadata: Map<String, Set<String>>?
        ): CharSequence {
            val withMetadata = StringBuilder()
            if (metadata != null) {
                for ((key, values) in metadata) {
                    if (values.isEmpty()) {
                        continue
                    }
                    withMetadata.append(';').append(key).append('=')
                    if (values.size > 1) {
                        withMetadata.append('"')
                    }
                    val valuesIt = values.iterator()
                    withMetadata.append(valuesIt.next())
                    while (valuesIt.hasNext()) {
                        withMetadata.append(',').append(valuesIt.next())
                    }
                    if (values.size > 1) {
                        withMetadata.append('"')
                    }
                }
            }
            withMetadata.append(':').append(value)
            return withMetadata
        }
    }
}