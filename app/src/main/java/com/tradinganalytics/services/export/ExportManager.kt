package com.tradinganalytics.services.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.google.gson.GsonBuilder
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.storage.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class ExportFormat {
    PDF, CSV, JSON
}

@Singleton
class ExportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storageManager: StorageManager
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
    private val displayDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun exportReport(data: ReportData, format: ExportFormat): Result<File> {
        return try {
            val exportDir = storageManager.getDirectory(StorageManager.StorageDirectory.Reports)
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = when (format) {
                ExportFormat.CSV -> exportToCsv(data.entries.map { entry ->
                    EntryEntity(
                        userId = 0,
                        date = entry.date,
                        result = entry.result,
                        amount = entry.amount,
                        notes = entry.notes,
                        patternId = entry.pattern
                    )
                })
                ExportFormat.JSON -> exportToJson(data)
                ExportFormat.PDF -> exportToPdf(data)
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportToCsv(entries: List<EntryEntity>): File {
        val exportDir = storageManager.getDirectory(StorageManager.StorageDirectory.Exports)
        if (!exportDir.exists()) exportDir.mkdirs()
        val fileName = "entries_${dateFormat.format(Date())}.csv"
        val file = File(exportDir, fileName)

        val sb = StringBuilder()
        sb.appendLine("ID,Date,Result,Amount,Notes,Pattern")
        for (entry in entries) {
            sb.appendLine(
                "${entry.id}," +
                    "${displayDateFormat.format(entry.date)}," +
                    "${entry.result}," +
                    "${entry.amount}," +
                    "\"${entry.notes?.replace("\"", "\"\"") ?: ""}\"," +
                    "\"${entry.patternId ?: ""}\""
            )
        }
        file.writeText(sb.toString())
        return file
    }

    fun exportToJson(data: Any): File {
        val exportDir = storageManager.getDirectory(StorageManager.StorageDirectory.Exports)
        if (!exportDir.exists()) exportDir.mkdirs()
        val fileName = "export_${dateFormat.format(Date())}.json"
        val file = File(exportDir, fileName)
        file.writeText(gson.toJson(data))
        return file
    }

    fun exportToPdf(report: ReportData): File {
        val exportDir = storageManager.getDirectory(StorageManager.StorageDirectory.Reports)
        if (!exportDir.exists()) exportDir.mkdirs()
        val fileName = "report_${dateFormat.format(Date())}.pdf"
        val file = File(exportDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 24f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#1A237E")
        }
        val headerPaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#333333")
        }
        val bodyPaint = Paint().apply {
            textSize = 12f
            color = android.graphics.Color.parseColor("#555555")
        }
        val dividerPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#CCCCCC")
            strokeWidth = 1f
        }

        var y = 48f
        val margin = 48f
        val pageWidth = 612f
        val contentWidth = pageWidth - 2 * margin

        canvas.drawText(report.title, margin, y, titlePaint)
        y += 32f
        canvas.drawText("Generated: ${displayDateFormat.format(report.generatedAt)}", margin, y, bodyPaint)
        y += 20f

        if (report.periodStart != null && report.periodEnd != null) {
            canvas.drawText(
                "Period: ${displayDateFormat.format(report.periodStart)} - ${displayDateFormat.format(report.periodEnd)}",
                margin, y, bodyPaint
            )
            y += 20f
        }

        y += 8f
        canvas.drawLine(margin, y, margin + contentWidth, y, dividerPaint)
        y += 24f

        canvas.drawText("Summary", margin, y, headerPaint)
        y += 24f

        val summaryLines = listOf(
            "Total Trades: ${report.totalTrades}",
            "Winning Trades: ${report.winningTrades}",
            "Losing Trades: ${report.losingTrades}",
            "Win Rate: ${"%.1f".format(report.winRate)}%",
            "Total P&L: ${"%.2f".format(report.totalProfitLoss)}",
            "Profit Factor: ${"%.2f".format(report.profitFactor)}",
            "Max Drawdown: ${"%.2f".format(report.maxDrawdown)}%",
            "Sharpe Ratio: ${"%.2f".format(report.sharpeRatio)}",
            "Starting Balance: ${"%.2f".format(report.startingBalance)}",
            "Ending Balance: ${"%.2f".format(report.endingBalance)}",
            "Return: ${"%.2f".format(report.returnPercentage)}%"
        )

        for (line in summaryLines) {
            canvas.drawText(line, margin, y, bodyPaint)
            y += 18f
        }

        if (report.notes.isNotBlank()) {
            y += 8f
            canvas.drawLine(margin, y, margin + contentWidth, y, dividerPaint)
            y += 24f
            canvas.drawText("Notes", margin, y, headerPaint)
            y += 24f
            for (noteLine in report.notes.split("\n")) {
                canvas.drawText(noteLine.trim(), margin, y, bodyPaint)
                y += 16f
            }
        }

        document.finishPage(page)

        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
        } finally {
            document.close()
        }

        return file
    }

    fun exportDataToFile(data: String, fileName: String, directory: StorageManager.StorageDirectory): File {
        val dir = storageManager.getDirectory(directory)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        file.writeText(data)
        return file
    }

    fun getExportUri(file: File): Uri {
        return Uri.fromFile(file)
    }

    fun getFileSize(file: File): Long = file.length()
}
