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

import android.telephony.PhoneNumberUtils

/**
 * @author Sean Owen
 */
internal class VCardTelDisplayFormatter @JvmOverloads constructor(private val metadataForIndex: List<Map<String, Set<String>>>? = null) :
    Formatter {
    override fun format(value: CharSequence, index: Int): CharSequence {
        var mValue = value
        mValue = PhoneNumberUtils.formatNumber(mValue.toString())
        val metadata =
            if (metadataForIndex == null || metadataForIndex.size <= index) null else metadataForIndex[index]
        mValue = formatMetadata(mValue, metadata)
        return mValue
    }

    companion object {
        private fun formatMetadata(
            value: CharSequence,
            metadata: Map<String, Set<String>>?
        ): CharSequence {
            if (metadata == null || metadata.isEmpty()) {
                return value
            }
            val withMetadata = StringBuilder()
            for ((_, values) in metadata) {
                if (values.isEmpty()) {
                    continue
                }
                val valuesIt = values.iterator()
                withMetadata.append(valuesIt.next())
                while (valuesIt.hasNext()) {
                    withMetadata.append(',').append(valuesIt.next())
                }
            }
            if (withMetadata.isNotEmpty()) {
                withMetadata.append(' ')
            }
            withMetadata.append(value)
            return withMetadata
        }
    }
}