/*
 * Copyright (C) 2008 ZXing authors
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

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.client.result.ParsedResultType

/**
 * This class encodes data from an Intent into a QR code, and then displays it
 * full screen so that another person can scan it with their device.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
class QREncode private constructor(codeEncoder: QRCodeEncoder) {
    private var mQRCodeEncoder: QRCodeEncoder? = codeEncoder

    /**
     * [Builder.build] () QREncode.Builder().build()}
     *
     * @return
     */
    fun encodeAsBitmap(): Bitmap? {
        try {
            return mQRCodeEncoder!!.encodeAsBitmap()
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    class Builder(private val context: Context) {
        var barcodeFormat: BarcodeFormat? = null
        var parsedResultType: ParsedResultType? = ParsedResultType.TEXT
            private set
        var bundle: Bundle? = null
            private set
        var contents: String? = null //原内容
            private set
        var encodeContents: String? = null //编码内容
        var color = 0 //颜色

        var colors: IntArray? = null
            private set
        var addressBookUri: Uri? = null
            private set
        var isUseVCard = true
            private set
        var size = 0
        var logoBitmap: Bitmap? = null
            private set
        var logoSize = 0
            private set
        var qrBackground: Bitmap? = null
            private set
        var margin = 4
            private set

        fun setBarcodeFormat(barcodeFormat: BarcodeFormat?): Builder {
            this.barcodeFormat = barcodeFormat
            return this
        }

        /**
         * 设置二维码类型
         *
         * @param parsedResultType [ParsedResultType]
         * @return
         */
        fun setParsedResultType(parsedResultType: ParsedResultType?): Builder {
            this.parsedResultType = parsedResultType
            return this
        }

        /**
         * 设置联系人Uri
         *
         * @param addressBookUri
         */
        @Suppress("UNUSED")
        fun setAddressBookUri(addressBookUri: Uri?): Builder {
            this.addressBookUri = addressBookUri
            return this
        }

        /**
         * 设置内容，当 ParsedResultType 是 ADDRESSBOOK 、GEO 类型
         *
         * @param bundle
         * @return
         */
        @Suppress("UNUSED")
        fun setBundle(bundle: Bundle?): Builder {
            this.bundle = bundle
            return this
        }

        /**
         * 二维码内容
         *
         * @param contents tel、email等不需要前缀
         * @return
         */
        fun setContents(contents: String?): Builder {
            this.contents = contents
            return this
        }

        @Suppress("UNUSED")
        fun setEncodeContents(encodeContents: String?): Builder {
            this.encodeContents = encodeContents
            return this
        }

        /**
         * 设置二维码颜色
         *
         * @param color
         * @return
         */
        @Suppress("UNUSED")
        fun setColor(color: Int): Builder {
            this.color = color
            return this
        }

        /**
         * 设置二维码颜色
         *
         * @param leftTop     左上
         * @param leftBottom  左下
         * @param rightBottom 右下
         * @param rightTop    右上
         * @return
         */
        @Suppress("UNUSED")
        fun setColors(leftTop: Int, leftBottom: Int, rightBottom: Int, rightTop: Int): Builder {
            colors = null
            colors = IntArray(4)
            colors!![0] = leftTop
            colors!![1] = leftBottom
            colors!![2] = rightBottom
            colors!![3] = rightTop
            return this
        }

        /**
         * 设置vCard格式，默认true
         *
         * @param useVCard
         * @return
         */
        @Suppress("UNUSED")
        fun setUseVCard(useVCard: Boolean): Builder {
            isUseVCard = useVCard
            return this
        }

        /**
         * 二维码大小
         *
         * @param size
         * @return
         */
        fun setSize(size: Int): Builder {
            this.size = size
            return this
        }

        /**
         * 二维码中间的logo
         *
         * @param logoBitmap
         * @return
         */
        @Suppress("UNUSED")
        fun setLogoBitmap(logoBitmap: Bitmap?): Builder {
            this.logoBitmap = logoBitmap
            return this
        }

        /**
         * 二维码中间的logo，logoSize不能 > Math.min(logoBitmap.getWidth(), logoBitmap.getHeight())
         *
         * @param logoBitmap
         * @param logoSize
         * @return
         */
        fun setLogoBitmap(logoBitmap: Bitmap?, logoSize: Int): Builder {
            this.logoBitmap = logoBitmap
            this.logoSize = logoSize
            return this
        }

        /**
         * 设置二维码背景
         *
         * @param background
         * @return
         */
        @Suppress("UNUSED")
        fun setQrBackground(background: Bitmap?): Builder {
            qrBackground = background
            return this
        }

        /**
         * 设置二维码边框
         *
         * @param margin 范围值：0-4
         * @return
         */
        fun setMargin(margin: Int): Builder {
            this.margin = margin
            return this
        }

        /**
         * @return
         */
        @Deprecated("{@link #build()}")
        fun buildDeprecated(): QRCodeEncoder {
            checkParams()
            return QRCodeEncoder(this, context.applicationContext)
        }

        fun build(): QREncode {
            checkParams()
            val qrCodeEncoder = QRCodeEncoder(this, context.applicationContext)
            return QREncode(qrCodeEncoder)
        }

        private fun checkParams() {
            requireNotNull(parsedResultType) { "parsedResultType no found..." }
            require(
                !(parsedResultType !== ParsedResultType.ADDRESSBOOK && parsedResultType !==
                        ParsedResultType.GEO && contents == null)
            ) {
                "parsedResultType not" +
                        " ParsedResultType.ADDRESSBOOK and ParsedResultType.GEO, contents no " +
                        "found..."
            }
            require(
                !((parsedResultType === ParsedResultType.ADDRESSBOOK || parsedResultType ===
                        ParsedResultType.GEO)
                        && bundle == null && addressBookUri == null)
            ) {
                "parsedResultType yes" +
                        " ParsedResultType.ADDRESSBOOK or ParsedResultType.GEO, bundle and " +
                        "addressBookUri no found..."
            }
        }
    }

    companion object {
        /**
         * @param codeEncoder [Builder.buildDeprecated] () QREncode.Builder()
         * .buildDeprecated()}
         * @return
         */
        @Deprecated("")
        fun encodeQR(codeEncoder: QRCodeEncoder): Bitmap? {
            try {
                return codeEncoder.encodeAsBitmap()
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            return null
        }
    }
}