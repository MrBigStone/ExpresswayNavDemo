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
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.provider.ContactsContract
import android.view.WindowManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

/**
 * This class does the work of decoding the user's request and extracting all
 * the data to be encoded in a barcode.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
class QRCodeEncoder(
    private val encodeBuild: QREncode.Builder,
    private val context: Context
) {
    private fun encodeContentsFromZXing(build: QREncode.Builder) {
        if (build.barcodeFormat == null
            || build.barcodeFormat == BarcodeFormat.QR_CODE
        ) {
            build.setBarcodeFormat(BarcodeFormat.QR_CODE)
            encodeQRCodeContents(build)
        }
    }

    private fun encodeQRCodeContents(build: QREncode.Builder) {
        when (build.parsedResultType) {
            ParsedResultType.WIFI -> encodeBuild.encodeContents = build.contents
            ParsedResultType.CALENDAR -> encodeBuild.encodeContents = build.contents
            ParsedResultType.ISBN -> encodeBuild.encodeContents = build.contents
            ParsedResultType.PRODUCT -> encodeBuild.encodeContents = build.contents
            ParsedResultType.VIN -> encodeBuild.encodeContents = build.contents
            ParsedResultType.URI -> encodeBuild.encodeContents = build.contents
            ParsedResultType.TEXT -> encodeBuild.encodeContents = build.contents
            ParsedResultType.EMAIL_ADDRESS -> encodeBuild.encodeContents =
                "mailto:" + build.contents
            ParsedResultType.TEL -> encodeBuild.encodeContents = "tel:" + build.contents
            ParsedResultType.SMS -> encodeBuild.encodeContents = "sms:" + build.contents
            ParsedResultType.ADDRESSBOOK -> {
                var contactBundle: Bundle? = null
                //uri解析
                val addressBookUri = build.addressBookUri
                if (addressBookUri != null) contactBundle =
                    ParserUriToVCard().parserUri(context, addressBookUri)
                //Bundle解析
                if (contactBundle != null && contactBundle.isEmpty || contactBundle == null) contactBundle =
                    build.bundle
                if (contactBundle != null) {
                    val name = contactBundle.getString(ContactsContract.Intents.Insert.NAME)
                    val organization = contactBundle
                        .getString(ContactsContract.Intents.Insert.COMPANY)
                    val address = contactBundle.getString(ContactsContract.Intents.Insert.POSTAL)
                    val phones = getAllBundleValues(contactBundle, ParserUriToVCard.PHONE_KEYS)
                    val phoneTypes =
                        getAllBundleValues(contactBundle, ParserUriToVCard.PHONE_TYPE_KEYS)
                    val emails = getAllBundleValues(contactBundle, ParserUriToVCard.EMAIL_KEYS)
                    val url = contactBundle.getString(ParserUriToVCard.URL_KEY)
                    val urls: List<String?>? = url?.let { listOf(it) }
                    val note = contactBundle.getString(ParserUriToVCard.NOTE_KEY)
                    val encoder =
                        if (build.isUseVCard) VCardContactEncoder() else MECARDContactEncoder()
                    val encoded = encoder.encode(
                        listOf(name),
                        organization,
                        listOf(address),
                        phones,
                        phoneTypes,
                        emails,
                        urls,
                        note
                    )
                    // Make sure we've encoded at least one field.
                    if (!encoded!![1]!!.isEmpty()) {
                        encodeBuild.encodeContents = encoded[0]
                    }
                }
            }
            ParsedResultType.GEO -> {
                val locationBundle = build.bundle
                if (locationBundle != null) {
                    val latitude = locationBundle.getFloat("LAT", Float.MAX_VALUE)
                    val longitude = locationBundle.getFloat("LONG", Float.MAX_VALUE)
                    if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
                        encodeBuild.encodeContents = "geo:$latitude,$longitude"
                    }
                }
            }
        }
    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(): Bitmap? {
        val content = encodeBuild.encodeContents
        val barcodeFormat = encodeBuild.barcodeFormat
        val qrColor = encodeBuild.color
        val size = encodeBuild.size
        val logoBitmap = encodeBuild.logoBitmap
        return if (logoBitmap != null) encodeAsBitmap(
            content, barcodeFormat, qrColor, size,
            logoBitmap, encodeBuild.logoSize
        ) else encodeAsBitmap(content, barcodeFormat, qrColor, size)
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(
        content: String?, barcodeFormat: BarcodeFormat?, qrColor: Int,
        size: Int
    ): Bitmap? {
        if (content == null || barcodeFormat == null) {
            return null
        }
        val hints: MutableMap<EncodeHintType, Any?> = EnumMap(
            EncodeHintType::class.java
        )
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = encodeBuild.margin
        val result: BitMatrix = try {
            MultiFormatWriter().encode(content, barcodeFormat, size, size, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                // 处理二维码颜色
                if (result[x, y]) {
                    val colors = encodeBuild.colors
                    if (colors != null) {
                        if (x < size / 2 && y < size / 2) {
                            pixels[y * size + x] = colors[0] // 左上
                        } else if (x < size / 2 && y > size / 2) {
                            pixels[y * size + x] = colors[1] // 左下
                        } else if (x > size / 2 && y > size / 2) {
                            pixels[y * size + x] = colors[2] // 右下
                        } else {
                            pixels[y * size + x] = colors[3] // 右上
                        }
                    } else {
                        pixels[offset + x] = qrColor
                    }
                } else {
                    pixels[offset + x] = WHITE
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(
        content: String?, barcodeFormat: BarcodeFormat?, qrColor: Int,
        size: Int, logoBitmap: Bitmap, logoSize: Int
    ): Bitmap? {
        if (content == null || barcodeFormat == null) {
            return null
        }
        val hints: MutableMap<EncodeHintType, Any?> = EnumMap(
            EncodeHintType::class.java
        )
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // 容错率
        hints[EncodeHintType.MARGIN] = encodeBuild.margin // default is 4
        val result: BitMatrix = try {
            MultiFormatWriter().encode(content, barcodeFormat, size, size, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val width = result.width
        val height = result.height
        val halfW = width / 2
        val halfH = height / 2
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                if (x > halfW - logoSize && x < halfW + logoSize && y > halfH - logoSize && y < halfH + logoSize) {
                    pixels[y * width + x] = logoBitmap.getPixel(
                        x - halfW + logoSize, y - halfH
                                + logoSize
                    )
                } else {
                    // 处理二维码颜色
                    if (result[x, y]) {
                        val colors = encodeBuild.colors
                        if (colors != null) {
                            if (x < size / 2 && y < size / 2) {
                                pixels[y * size + x] = colors[0] // 左上
                            } else if (x < size / 2 && y > size / 2) {
                                pixels[y * size + x] = colors[1] // 左下
                            } else if (x > size / 2 && y > size / 2) {
                                pixels[y * size + x] = colors[2] // 右下
                            } else {
                                pixels[y * size + x] = colors[3] // 右上
                            }
                        } else {
                            pixels[offset + x] = qrColor
                        }
                    } else {
                        pixels[offset + x] = WHITE
                    }
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return if (encodeBuild.qrBackground != null) {
            addBackground(bitmap, encodeBuild.qrBackground!!)
        } else bitmap
    }

    companion object {
        private const val WHITE = -0x1
        private const val BLACK = -0x1000000
        private fun getAllBundleValues(bundle: Bundle, keys: Array<String>): List<String?> {
            val values: MutableList<String?> = ArrayList(keys.size)
            for (key in keys) {
                val value = bundle[key]
                values.add(value?.toString())
            }
            return values
        }

        private fun getSmallerDimension(context: Context): Int {
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val displaySize = Point()
            display.getSize(displaySize)
            val width = displaySize.x
            val height = displaySize.y
            var smallerDimension = if (width < height) width else height
            smallerDimension = smallerDimension * 7 / 8
            return smallerDimension
        }

        private fun addBackground(qrBitmap: Bitmap, background: Bitmap): Bitmap {
            val bgWidth = background.width
            val bgHeight = background.height
            val fgWidth = qrBitmap.width
            val fgHeight = qrBitmap.height
            val bitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(background, 0f, 0f, null)
            //二维码在背景图中间
            val left = ((bgWidth - fgWidth) / 2).toFloat()
            val top = ((bgHeight - fgHeight) / 2).toFloat()
            canvas.drawBitmap(qrBitmap, left, top, null)
            canvas.save()
            canvas.restore()
            return bitmap
        }
    }

    init {
        if (encodeBuild.color == 0) encodeBuild.color = BLACK

        // This assumes the view is full screen, which is a good assumption
        if (encodeBuild.size == 0) {
            val smallerDimension = getSmallerDimension(
                context.applicationContext
            )
            encodeBuild.size = smallerDimension
        }
        val logoBitmap = encodeBuild.logoBitmap
        if (logoBitmap != null) {
            var logoSize = Math.min(logoBitmap.width, logoBitmap.height) / 2
            if (encodeBuild.logoSize > 0 && encodeBuild.logoSize < logoSize) {
                logoSize = encodeBuild.logoSize
            }
            encodeBuild.setLogoBitmap(logoBitmap, logoSize)
        }
        encodeContentsFromZXing(encodeBuild)
    }
}