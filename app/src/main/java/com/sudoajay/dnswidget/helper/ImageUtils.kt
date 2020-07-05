package com.sudoajay.dnswidget.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

object ImageUtils{


        fun createBitmapFromString(speed: String, units: String) : Bitmap{
            val unit = "$units/s"

            val paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = 55F
            paint.textAlign = Paint.Align.CENTER

            val unitsPaint = Paint()
            unitsPaint.isAntiAlias = true
            unitsPaint.textSize = 40F
            unitsPaint.textAlign = Paint.Align.CENTER

            val speedBounds = Rect()
            paint.getTextBounds(speed,0,speed.length,speedBounds)

            val unitsBounds = Rect()
            unitsPaint.getTextBounds(unit,0,unit.length,unitsBounds)

            val width = if(speedBounds.width() > unitsBounds.width()){
                speedBounds.width()
            }else{
                unitsBounds.width()
            }

            val bitmap = Bitmap.createBitmap(width + 10, 90,
                Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)
            canvas.drawText(speed, (width/2F + 5), 50F, paint)
            canvas.drawText(unit, width/2F, 90F, unitsPaint)

            return bitmap
        }

}