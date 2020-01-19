package es.utopik.wimc

import android.graphics.Bitmap

/**
 * @fecha 17.01.2020
 * @source https://gist.github.com/nesquena/3885707fd3773c09f1bb
 */
object BitmapScaler {
    // scale and keep aspect ratio
    fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap {
        val factor = width / b.width.toFloat()
        return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
    }

    // scale and keep aspect ratio
    fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap {
        val factor = height / b.height.toFloat()
        return Bitmap.createScaledBitmap(b, (b.width * factor).toInt(), height, true)
    }

    // scale and keep aspect ratio
    fun scaleToFill(b: Bitmap, width: Int, height: Int): Bitmap {
        val factorH = height / b.width.toFloat()
        val factorW = width / b.width.toFloat()
        val factorToUse = if (factorH > factorW) factorW else factorH
        return Bitmap.createScaledBitmap(
            b, (b.width * factorToUse).toInt(),
            (b.height * factorToUse).toInt(), true
        )
    }

    // scale and don't keep aspect ratio
    fun strechToFill(b: Bitmap, width: Int, height: Int): Bitmap {
        val factorH = height / b.height.toFloat()
        val factorW = width / b.width.toFloat()
        return Bitmap.createScaledBitmap(
            b, (b.width * factorW).toInt(),
            (b.height * factorH).toInt(), true
        )
    }
}