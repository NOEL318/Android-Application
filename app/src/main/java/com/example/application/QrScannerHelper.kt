package com.example.application

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrHelper {
    
    // Función para Alumno: Genera QR con su ID y la materia actual
    fun generateQRForAttendance(studentId: String, subjectId: String): Bitmap? {
        val content = "$studentId|$subjectId|${System.currentTimeMillis()}"
        return try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400, 400)
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Función para Profesor: Parsea el contenido del QR
    // Retorna Pair<StudentId, SubjectId>
    fun parseAttendanceQR(qrContent: String): Pair<String, String>? {
        val parts = qrContent.split("|")
        if (parts.size >= 2) {
            val studentId = parts[0]
            val subjectId = parts[1]
            // El tercer parámetro es el timestamp, que se podría usar para validar 
            // que el código no haya caducado (ej. expira en 5 mins)
            val timestamp = parts.getOrNull(2)?.toLongOrNull() ?: 0L
            val currentTime = System.currentTimeMillis()
            
            // Validar que el QR fue generado hace menos de 5 minutos
            val fiveMinutesInMillis = 5 * 60 * 1000L
            if (currentTime - timestamp > fiveMinutesInMillis) {
                 // QR expirado
                 return null
            }

            return Pair(studentId, subjectId)
        }
        return null
    }
}
