package com.wolttracker.app.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.regex.Pattern
import kotlin.coroutines.resume

data class ParsedWoltShift(
    val startHour: Int = 10,
    val startMinute: Int = 0,
    val endHour: Int = 15,
    val endMinute: Int = 30,
    val orders: Int = 0,
    val income: Double = 0.0,
    val tips: Double = 0.0
)

object WoltScreenshotParser {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun parseFromUri(context: Context, imageUri: Uri): ParsedWoltShift {
        val fullText = recognizeText(context, imageUri)
        return extractShiftData(fullText)
    }

    private suspend fun recognizeText(context: Context, uri: Uri): String =
        suspendCancellableCoroutine { continuation ->
            try {
                val image = InputImage.fromFilePath(context, uri)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        continuation.resume(visionText.text)
                    }
                    .addOnFailureListener {
                        continuation.resume("")
                    }
            } catch (e: Exception) {
                continuation.resume("")
            }
        }

    fun extractShiftData(rawText: String): ParsedWoltShift {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        var startHour = 10
        var startMinute = 0
        var endHour = 15
        var endMinute = 30
        var foundTime = false

        // Pattern για ώρα π.χ. 11:30 - 16:45 ή 11.30 - 16.45
        val timeRangePattern = Pattern.compile("(\\d{1,2})[:.](\\d{2})\\s*[-–—]\\s*(\\d{1,2})[:.](\\d{2})")
        for (line in lines) {
            val matcher = timeRangePattern.matcher(line)
            if (matcher.find()) {
                startHour = matcher.group(1)?.toIntOrNull() ?: 10
                startMinute = matcher.group(2)?.toIntOrNull() ?: 0
                endHour = matcher.group(3)?.toIntOrNull() ?: 15
                endMinute = matcher.group(4)?.toIntOrNull() ?: 30
                foundTime = true
                break
            }
        }

        var orders = 0
        // Pattern για παραγγελίες: "16 deliveries", "16 tasks", "16 orders", "16 παραγγελίες", "16"
        val ordersPattern = Pattern.compile("(\\d+)\\s*(deliveries|delivery|orders|order|tasks|task|παραγγελ|παραδ)", Pattern.CASE_INSENSITIVE)
        for (line in lines) {
            val matcher = ordersPattern.matcher(line)
            if (matcher.find()) {
                orders = matcher.group(1)?.toIntOrNull() ?: 0
                if (orders > 0) break
            }
        }

        var income = 0.0
        var tips = 0.0

        // Ψάχνουμε γραμμές για Tips: π.χ. "Tips 125,50" ή "Drikkepenge 125 kr."
        val tipsKeywords = listOf("tip", "tips", "drikkepenge", "φιλοδωρ")
        for (i in lines.indices) {
            val line = lines[i].lowercase()
            if (tipsKeywords.any { line.contains(it) }) {
                // Έλεγξε την ίδια γραμμή ή την επόμενη για ποσό
                val amountInLine = extractAmount(lines[i])
                if (amountInLine > 0) {
                    tips = amountInLine
                } else if (i + 1 < lines.size) {
                    val amountNextLine = extractAmount(lines[i + 1])
                    if (amountNextLine > 0) {
                        tips = amountNextLine
                    }
                }
            }
        }

        // Ψάχνουμε γραμμές για Total / Earnings / Base pay / Gross:
        val incomeKeywords = listOf("total", "earnings", "gross", "udbetaling", "indtjening", "αμοιβ", "εσoδ", "έσοδ")
        for (i in lines.indices) {
            val line = lines[i].lowercase()
            if (incomeKeywords.any { line.contains(it) } && !tipsKeywords.any { line.contains(it) }) {
                val amountInLine = extractAmount(lines[i])
                if (amountInLine > 0 && amountInLine != tips) {
                    income = amountInLine
                } else if (i + 1 < lines.size) {
                    val amountNextLine = extractAmount(lines[i + 1])
                    if (amountNextLine > 0 && amountNextLine != tips) {
                        income = amountNextLine
                    }
                }
            }
        }

        // Αν δεν βρέθηκε μέσω keyword, πάρε τα μεγαλύτερα 2 νούμερα kr.
        if (income == 0.0) {
            val allAmounts = lines.mapNotNull { extractAmount(it).takeIf { a -> a > 10.0 } }
            if (allAmounts.isNotEmpty()) {
                val sorted = allAmounts.sortedDescending()
                income = sorted[0]
                if (tips == 0.0 && sorted.size > 1 && sorted[1] < income * 0.5) {
                    tips = sorted[1]
                }
            }
        }

        return ParsedWoltShift(
            startHour = startHour,
            startMinute = startMinute,
            endHour = endHour,
            endMinute = endMinute,
            orders = orders,
            income = income,
            tips = tips
        )
    }

    private fun extractAmount(text: String): Double {
        // Καθαρισμός για δεκαδικά με κόμμα ή τελεία π.χ. "650,50 kr" -> 650.50
        val pattern = Pattern.compile("(\\d{1,4}(?:[.,]\\d{2})?)")
        val matcher = pattern.matcher(text.replace(" ", ""))
        var maxAmount = 0.0
        while (matcher.find()) {
            val cleanStr = matcher.group(1)?.replace(",", ".") ?: ""
            val parsed = cleanStr.toDoubleOrNull() ?: 0.0
            if (parsed > maxAmount) {
                maxAmount = parsed
            }
        }
        return maxAmount
    }
}
