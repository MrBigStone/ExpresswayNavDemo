package com.bigstone.expresswaynav.ext.code

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.bigstone.expresswaynav.ext.code.encode.QREncode
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*
import kotlin.math.max

/**
 * @Description: 条形码/二维码相关工具，基于东大扫描库
 * @Author: yangxinlei
 * @CreateDate:  2021/10/8 4:43 下午
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 */
object CodeUtil {


    /**
     * 生成条形码
     * @param contents   内容
     * @param desiredWidth 目标宽度
     * @param desiredHeight 目标高度
     * @param format  编码格式 可选取值：[BarcodeFormat.CODE_128] [BarcodeFormat.CODE_93] [BarcodeFormat.CODE_39]
     * @param intervalColor  条码间隔颜色
     * @param contentColor 条码颜色
     * */
    fun encodeBarCodeAsBitmap(
        contents: String,
        desiredWidth: Int,
        desiredHeight: Int,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
        @ColorInt intervalColor: Long = 0xFFFFFFFF,
        @ColorInt contentColor: Long = 0xFF000000
    ): Bitmap? {

        val writer = MultiFormatWriter()
        var result: BitMatrix? = null
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // 容错率
        hints[EncodeHintType.MARGIN] = 0 // 无白边，还需要对宽度重新计算才会生效

        try {
            result = writer.encode(
                contents,
                format,
                getNewWidth(desiredWidth, contents),
                desiredHeight,
                hints
            )
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        val width = result?.width ?: 0
        val height = result?.height ?: 0

        val pixels = IntArray(size = width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result?.get(x, y) == true) contentColor.toInt() else intervalColor.toInt()
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * 根据视图宽度和内容，获取新的宽度，解决条形码两侧留白问题
     * */
    private fun getNewWidth(width: Int, contents: String): Int {
        val writer = Code128Writer()
        val code = writer.encode(contents)

        val inputWidth = code.size
        val outputWidth = max(inputWidth, width)
        val remain = outputWidth % inputWidth   //留白的宽度
        //减去留白宽度，是为了保证最终的宽度和inputWidth成整数比例，才不会有留白
        return outputWidth - remain
    }


    /**
     * 生成二维码
     * @param context 上下文
     * @param size 宽高尺寸
     * @param contents 内容
     * @param margin 边距
     * @param parsedResultType 数据格式
     * */
    fun encodeQRCodeAsBitmap(
        context: Context,
        contents: String,
        @IntRange(from = 0)
        size: Int,
        @IntRange(from = 0)
        margin: Int = 0,
        parsedResultType: ParsedResultType = ParsedResultType.TEXT
    ): Bitmap? {
        val qrEncode = QREncode.Builder(context)
            .setSize(size)
            .setContents(contents)
            .setMargin(margin)
            .setParsedResultType(parsedResultType)
            .build()
        try {
            return qrEncode.encodeAsBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}