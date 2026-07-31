package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.entity.ExamResultEntity
import com.example.data.entity.FeeRecordEntity
import com.example.data.entity.StudentEntity
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    // Standard A4 page size in points (1 pt = 1/72 inch) -> 595 x 842
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun generateFeeReceiptPdf(context: Context, fee: FeeRecordEntity): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()

        // Background
        canvas.drawColor(Color.WHITE)

        // Outer Double Border
        paint.color = Color.rgb(2, 67, 182) // #0243B6
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        canvas.drawRoundRect(RectF(18f, 18f, PAGE_WIDTH - 18f, PAGE_HEIGHT - 18f), 10f, 10f, paint)

        paint.strokeWidth = 1f
        canvas.drawRoundRect(RectF(22f, 22f, PAGE_WIDTH - 22f, PAGE_HEIGHT - 22f), 8f, 8f, paint)

        // School Header Banner
        titlePaint.color = Color.rgb(2, 67, 182)
        titlePaint.textSize = 20f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("GAYATRI BAL VIDHYA NIKETAN", PAGE_WIDTH / 2f, 60f, titlePaint)

        titlePaint.textSize = 11f
        titlePaint.color = Color.DKGRAY
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Shahnagar, District Panna (M.P.) - 488448", PAGE_WIDTH / 2f, 78f, titlePaint)

        titlePaint.textSize = 13f
        titlePaint.color = Color.rgb(255, 143, 0) // Amber Accent
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("OFFICIAL FEE RECEIPT", PAGE_WIDTH / 2f, 102f, titlePaint)

        // Divider
        paint.color = Color.LTGRAY
        paint.strokeWidth = 1f
        canvas.drawLine(35f, 115f, PAGE_WIDTH - 35f, 115f, paint)

        // Details Grid
        val textPaint = Paint().apply {
            textSize = 11f
            color = Color.BLACK
        }

        val boldPaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }

        // Receipt No & Date Row
        canvas.drawText("Receipt No:", 45f, 140f, boldPaint)
        canvas.drawText(fee.receiptNo, 120f, 140f, textPaint)

        canvas.drawText("Date:", PAGE_WIDTH - 180f, 140f, boldPaint)
        canvas.drawText(fee.paymentDate, PAGE_WIDTH - 130f, 140f, textPaint)

        // Student Info Box
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(RectF(35f, 155f, PAGE_WIDTH - 35f, 240f), 8f, 8f, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRoundRect(RectF(35f, 155f, PAGE_WIDTH - 35f, 240f), 8f, 8f, paint)

        canvas.drawText("Student Name:", 50f, 180f, boldPaint)
        canvas.drawText(fee.studentName, 140f, 180f, textPaint)

        canvas.drawText("Class & Section:", 50f, 202f, boldPaint)
        canvas.drawText(fee.className, 140f, 202f, textPaint)

        canvas.drawText("Fee Description:", 50f, 224f, boldPaint)
        canvas.drawText("${fee.feeType} (${fee.month} ${fee.year})", 140f, 224f, textPaint)

        // Fee Particulars Table
        var yPos = 265f
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(2, 67, 182)
        canvas.drawRect(RectF(35f, yPos, PAGE_WIDTH - 35f, yPos + 24f), paint)

        boldPaint.color = Color.WHITE
        canvas.drawText("Particulars", 50f, yPos + 16f, boldPaint)
        canvas.drawText("Amount (INR)", PAGE_WIDTH - 140f, yPos + 16f, boldPaint)

        yPos += 28f

        val particulars = listOf(
            "Tuition & Annual Academic Fee" to "₹ ${fee.totalAmount}",
            "Concession / Special Discount" to "- ₹ ${fee.discount}",
            "Net Payable Fee" to "₹ ${fee.totalAmount - fee.discount}",
            "Amount Paid (Received)" to "₹ ${fee.paidAmount}",
            "Remaining Balance Due" to "₹ ${fee.dueAmount}"
        )

        particulars.forEach { (label, amt) ->
            if (label.contains("Received")) {
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(239, 246, 255)
                canvas.drawRect(RectF(35f, yPos - 12f, PAGE_WIDTH - 35f, yPos + 10f), paint)
                boldPaint.color = Color.rgb(2, 67, 182)
                textPaint.color = Color.rgb(2, 67, 182)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            } else if (label.contains("Remaining")) {
                boldPaint.color = Color.rgb(220, 38, 38)
                textPaint.color = Color.rgb(220, 38, 38)
            } else {
                boldPaint.color = Color.BLACK
                textPaint.color = Color.BLACK
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            canvas.drawText(label, 50f, yPos, if (label.contains("Received") || label.contains("Net")) boldPaint else textPaint)
            canvas.drawText(amt, PAGE_WIDTH - 140f, yPos, if (label.contains("Received") || label.contains("Net")) boldPaint else textPaint)

            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(35f, yPos + 12f, PAGE_WIDTH - 35f, yPos + 12f, paint)

            yPos += 26f
        }

        // Payment Details
        yPos += 15f
        boldPaint.color = Color.BLACK
        canvas.drawText("Payment Mode: ${fee.paymentMode}", 50f, yPos, boldPaint)

        // Seal & Signatures
        yPos += 70f
        paint.style = Paint.Style.STROKE
        paint.color = Color.DKGRAY
        canvas.drawLine(PAGE_WIDTH - 170f, yPos, PAGE_WIDTH - 45f, yPos, paint)

        textPaint.color = Color.DKGRAY
        textPaint.textSize = 10f
        canvas.drawText("Authorized Signatory", PAGE_WIDTH - 150f, yPos + 14f, textPaint)
        canvas.drawText("Gayatri Bal Vidhya Niketan", PAGE_WIDTH - 160f, yPos + 26f, textPaint)

        // Watermark Note
        titlePaint.textSize = 9f
        titlePaint.color = Color.GRAY
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("System generated fee payment receipt. Valid across Gayatri Bal Vidhya Niketan school records.", PAGE_WIDTH / 2f, PAGE_HEIGHT - 35f, titlePaint)

        pdfDocument.finishPage(page)

        val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Receipt_${fee.receiptNo.replace("/", "_")}.pdf")
        return try {
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun generateReportCardPdf(
        context: Context,
        student: StudentEntity,
        results: List<ExamResultEntity>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()

        canvas.drawColor(Color.WHITE)

        // Border
        paint.color = Color.rgb(2, 67, 182)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        canvas.drawRoundRect(RectF(18f, 18f, PAGE_WIDTH - 18f, PAGE_HEIGHT - 18f), 10f, 10f, paint)

        // School Header
        titlePaint.color = Color.rgb(2, 67, 182)
        titlePaint.textSize = 20f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("GAYATRI BAL VIDHYA NIKETAN", PAGE_WIDTH / 2f, 60f, titlePaint)

        titlePaint.textSize = 11f
        titlePaint.color = Color.DKGRAY
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Shahnagar, District Panna (M.P.) - 488448", PAGE_WIDTH / 2f, 78f, titlePaint)

        titlePaint.textSize = 13f
        titlePaint.color = Color.rgb(255, 143, 0)
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ACADEMIC PROGRESS REPORT CARD", PAGE_WIDTH / 2f, 102f, titlePaint)

        paint.color = Color.LTGRAY
        paint.strokeWidth = 1f
        canvas.drawLine(35f, 115f, PAGE_WIDTH - 35f, 115f, paint)

        // Student Info
        val boldPaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }
        val textPaint = Paint().apply {
            textSize = 11f
            color = Color.BLACK
        }

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(RectF(35f, 130f, PAGE_WIDTH - 35f, 205f), 6f, 6f, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRoundRect(RectF(35f, 130f, PAGE_WIDTH - 35f, 205f), 6f, 6f, paint)

        canvas.drawText("Student Name:", 50f, 152f, boldPaint)
        canvas.drawText(student.name, 140f, 152f, textPaint)

        canvas.drawText("Roll Number:", PAGE_WIDTH - 200f, 152f, boldPaint)
        canvas.drawText("${student.rollNo}", PAGE_WIDTH - 120f, 152f, textPaint)

        canvas.drawText("Class & Section:", 50f, 174f, boldPaint)
        canvas.drawText("${student.className} (${student.section})", 140f, 174f, textPaint)

        canvas.drawText("Father's Name:", PAGE_WIDTH - 200f, 174f, boldPaint)
        canvas.drawText(student.parentName, PAGE_WIDTH - 120f, 174f, textPaint)

        canvas.drawText("Academic Session:", 50f, 196f, boldPaint)
        canvas.drawText("2026 - 2027", 140f, 196f, textPaint)

        // Marks Table
        var yPos = 230f
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(2, 67, 182)
        canvas.drawRect(RectF(35f, yPos, PAGE_WIDTH - 35f, yPos + 24f), paint)

        boldPaint.color = Color.WHITE
        canvas.drawText("Subject", 50f, yPos + 16f, boldPaint)
        canvas.drawText("Marks Obtained", 220f, yPos + 16f, boldPaint)
        canvas.drawText("Maximum Marks", 340f, yPos + 16f, boldPaint)
        canvas.drawText("Grade", PAGE_WIDTH - 90f, yPos + 16f, boldPaint)

        yPos += 26f
        boldPaint.color = Color.BLACK

        var totalObtained = 0
        var totalMax = 0

        if (results.isEmpty()) {
            textPaint.color = Color.GRAY
            canvas.drawText("No examination records found.", 50f, yPos + 15f, textPaint)
            yPos += 26f
        } else {
            results.forEach { res ->
                totalObtained += res.marksObtained
                totalMax += res.maxMarks

                canvas.drawText(res.subject, 50f, yPos, textPaint)
                canvas.drawText("${res.marksObtained}", 220f, yPos, textPaint)
                canvas.drawText("${res.maxMarks}", 340f, yPos, textPaint)

                boldPaint.color = if (res.grade.startsWith("A")) Color.rgb(0, 168, 107) else Color.rgb(2, 67, 182)
                canvas.drawText(res.grade, PAGE_WIDTH - 90f, yPos, boldPaint)
                boldPaint.color = Color.BLACK

                paint.style = Paint.Style.STROKE
                paint.color = Color.rgb(226, 232, 240)
                canvas.drawLine(35f, yPos + 8f, PAGE_WIDTH - 35f, yPos + 8f, paint)

                yPos += 24f
            }
        }

        // Percentage & Result Summary
        yPos += 10f
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(255, 248, 225)
        canvas.drawRoundRect(RectF(35f, yPos, PAGE_WIDTH - 35f, yPos + 42f), 6f, 6f, paint)

        val overallPct = if (totalMax > 0) (totalObtained.toDouble() / totalMax) * 100 else 0.0
        val resultStatus = if (overallPct >= 33.0) "PASSED (FIRST DIVISION)" else "NEEDS IMPROVEMENT"

        boldPaint.color = Color.rgb(2, 67, 182)
        boldPaint.textSize = 11f
        canvas.drawText("TOTAL MARKS: $totalObtained / $totalMax", 50f, yPos + 18f, boldPaint)
        canvas.drawText("PERCENTAGE: ${"%.1f".format(overallPct)}%", 240f, yPos + 18f, boldPaint)

        boldPaint.color = if (overallPct >= 33.0) Color.rgb(0, 168, 107) else Color.RED
        canvas.drawText("QUALIFYING STATUS: $resultStatus", 50f, yPos + 34f, boldPaint)

        // Signatures
        yPos += 110f
        paint.style = Paint.Style.STROKE
        paint.color = Color.DKGRAY

        canvas.drawLine(50f, yPos, 170f, yPos, paint)
        canvas.drawLine(PAGE_WIDTH - 170f, yPos, PAGE_WIDTH - 50f, yPos, paint)

        textPaint.color = Color.DKGRAY
        textPaint.textSize = 10f
        canvas.drawText("Class Teacher", 70f, yPos + 14f, textPaint)
        canvas.drawText("Principal Stamp & Signature", PAGE_WIDTH - 165f, yPos + 14f, textPaint)

        // Footer
        titlePaint.textSize = 9f
        titlePaint.color = Color.GRAY
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Gayatri Bal Vidhya Niketan, Shahnagar, Panna (M.P.). Official Academic Record.", PAGE_WIDTH / 2f, PAGE_HEIGHT - 35f, titlePaint)

        pdfDocument.finishPage(page)

        val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ReportCard_${student.rollNo}_${student.name.replace(" ", "_")}.pdf")
        return try {
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun generateStudentIdCardPdf(
        context: Context,
        student: StudentEntity
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(350, 520, 1).create() // Standard ID card proportion
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()

        canvas.drawColor(Color.WHITE)

        // Outer Border
        paint.color = Color.rgb(2, 67, 182)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawRoundRect(RectF(10f, 10f, 340f, 510f), 12f, 12f, paint)

        // Header Background
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(RectF(12f, 12f, 338f, 85f), 10f, 10f, paint)

        titlePaint.color = Color.WHITE
        titlePaint.textSize = 13f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("GAYATRI BAL VIDHYA NIKETAN", 175f, 38f, titlePaint)

        titlePaint.textSize = 9f
        titlePaint.color = Color.rgb(255, 213, 79)
        canvas.drawText("Shahnagar, Panna (M.P.) | Session 2026-27", 175f, 56f, titlePaint)

        titlePaint.color = Color.WHITE
        titlePaint.textSize = 8f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("STUDENT IDENTITY CARD", 175f, 72f, titlePaint)

        // Student Box & Info
        val textPaint = Paint().apply {
            textSize = 10f
            color = Color.BLACK
        }
        val boldPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }

        canvas.drawText("Name:", 25f, 120f, boldPaint)
        canvas.drawText(student.name, 110f, 120f, boldPaint)

        canvas.drawText("Roll No:", 25f, 142f, boldPaint)
        canvas.drawText(student.rollNo, 110f, 142f, textPaint)

        canvas.drawText("Class & Sec:", 25f, 164f, boldPaint)
        canvas.drawText("${student.className} (${student.section})", 110f, 164f, textPaint)

        canvas.drawText("Father Name:", 25f, 186f, boldPaint)
        canvas.drawText(student.parentName, 110f, 186f, textPaint)

        canvas.drawText("Contact No:", 25f, 208f, boldPaint)
        canvas.drawText(student.parentPhone, 110f, 208f, textPaint)

        canvas.drawText("Transport:", 25f, 230f, boldPaint)
        canvas.drawText(if (student.busRouteId != null) "School Bus" else "Self", 110f, 230f, textPaint)

        // Divider
        paint.color = Color.LTGRAY
        paint.strokeWidth = 1f
        canvas.drawLine(25f, 250f, 325f, 250f, paint)

        // QR Box
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(RectF(110f, 265f, 240f, 395f), 8f, 8f, paint)

        titlePaint.color = Color.rgb(2, 67, 182)
        titlePaint.textSize = 9f
        canvas.drawText("Official Verification QR", 175f, 415f, titlePaint)

        // Bottom Seal
        paint.style = Paint.Style.STROKE
        paint.color = Color.DKGRAY
        canvas.drawLine(210f, 465f, 320f, 465f, paint)

        titlePaint.textSize = 8f
        titlePaint.color = Color.DKGRAY
        canvas.drawText("Principal Signature", 265f, 478f, titlePaint)

        pdfDocument.finishPage(page)

        val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "IDCard_${student.rollNo}_${student.name.replace(" ", "_")}.pdf")
        return try {
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun openOrSharePdf(context: Context, file: File, title: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "Saved PDF to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}
