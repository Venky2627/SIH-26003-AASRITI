package com.sih26003.aasriti.feature.doctor

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.sih26003.aasriti.data.local.entities.CareLogEntity
import com.sih26003.aasriti.data.local.entities.GameSessionEntity
import com.sih26003.aasriti.demo.AasritiDemoData
import com.sih26003.aasriti.demo.DemoPatientConfig
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 100% Offline Clinician Summary Dossier PDF Generator.
 * Directly reads Room SQLite GameSession and CareLog records.
 * Uses Android native android.graphics.pdf.PdfDocument.
 * Strictly adheres to non-diagnostic observational language.
 */
object PdfReportGenerator {

    data class ReportData(
        val patientId: String = DemoPatientConfig.PATIENT_ID,
        val patientName: String = "আইতা বৰা (Aita Borah)",
        val pseudonymCode: String = DemoPatientConfig.PSEUDONYM_CODE,
        val age: Int = 68,
        val gender: String = "Female",
        val villageLocation: String = "Kamrup Rural, Assam",
        val cognitiveStage: String = "Mild Cognitive Impairment (MCI)",
        val clinicianName: String = "Dr. N. Barua, MD (Neurology)",
        val reportingPeriod: String = "Last 30 Days",
        val carePriorityStatus: String = "NORMAL",
        val clinicianNotes: String = "Preserve bilingual cognitive cues and daily hydration routines. Routine review in 4 weeks.",
        val sessions: List<GameSessionEntity> = emptyList(),
        val careLogs: List<CareLogEntity> = emptyList(),
        val includeDemographics: Boolean = true,
        val includeTelemetry: Boolean = true,
        val includeCareLogs: Boolean = true,
        val includePriority: Boolean = true,
        val includeGuidance: Boolean = true
    )

    fun generateClinicianPdf(context: Context, data: ReportData): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595x842 pt)
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val generatedDateStr = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())

        // Background
        paint.color = Color.parseColor("#FAF4ED") // WarmIvory / Parchment
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        var y = 30f

        // Top Header Accent Band (Crimson / Terracotta)
        paint.color = Color.parseColor("#720227")
        canvas.drawRect(30f, y, 565f, y + 60f, paint)

        paint.color = Color.parseColor("#FAF4ED")
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("AASRITI — Consultation & Care Summary Dossier", 45f, y + 26f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.parseColor("#CE9042") // Amber Gold
        canvas.drawText("100% Offline • On-Device Encrypted Storage • Date: $generatedDateStr", 45f, y + 46f, paint)

        y += 75f

        // Patient Profile Container (Demographics)
        if (data.includeDemographics) {
            paint.color = Color.parseColor("#F5EFE6") // SoftCream
            canvas.drawRoundRect(30f, y, 565f, y + 80f, 8f, 8f, paint)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#D4C3AC") // WarmStoneBorder
            canvas.drawRoundRect(30f, y, 565f, y + 80f, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 13f
            paint.color = Color.parseColor("#2A1D15")
            canvas.drawText("PATIENT PROFILE", 45f, y + 20f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 10.5f
            paint.color = Color.parseColor("#4A525A")
            canvas.drawText("Name: ${data.patientName}", 45f, y + 40f, paint)
            canvas.drawText("Canonical ID: ${data.patientId} (Pseudonym: ${data.pseudonymCode})", 45f, y + 55f, paint)
            canvas.drawText("Demographics: ${data.age} Yrs • ${data.gender} • Loc: ${data.villageLocation}", 45f, y + 70f, paint)

            canvas.drawText("Attending: ${data.clinicianName}", 320f, y + 40f, paint)
            canvas.drawText("Stage: ${data.cognitiveStage}", 320f, y + 55f, paint)
            canvas.drawText("Reporting Period: ${data.reportingPeriod}", 320f, y + 70f, paint)

            y += 95f
        }

        // SECTION 1: Longitudinal Cognitive Telemetry
        if (data.includeTelemetry) {
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 12f
            paint.color = Color.parseColor("#245C45") // Forest Green
            canvas.drawText("1. LONGITUDINAL INTERACTION TELEMETRY", 30f, y + 14f, paint)
            y += 24f

            val avgAccuracy = if (data.sessions.isNotEmpty()) (data.sessions.map { it.accuracy }.average() * 100).toInt() else 0
            val avgLatency = if (data.sessions.isNotEmpty()) data.sessions.map { it.reactionTimeMs }.average().toInt() else 0
            val totalHesitations = data.sessions.sumOf { it.hesitationCount }

            // Metrics Stat Cards Row
            val cardWidth = 125f
            val statMetrics = listOf(
                Pair("Total Sessions", "${data.sessions.size} sessions"),
                Pair("Avg Accuracy", if (data.sessions.isNotEmpty()) "$avgAccuracy%" else "No data"),
                Pair("Avg Latency", if (data.sessions.isNotEmpty()) "$avgLatency ms" else "No data"),
                Pair("Hesitations (>3.5s)", "$totalHesitations events")
            )

            for (i in statMetrics.indices) {
                val cx = 30f + i * (cardWidth + 8f)
                paint.color = Color.parseColor("#F5EFE6")
                canvas.drawRoundRect(cx, y, cx + cardWidth, y + 42f, 6f, 6f, paint)
                paint.style = Paint.Style.STROKE
                paint.color = Color.parseColor("#D4C3AC")
                canvas.drawRoundRect(cx, y, cx + cardWidth, y + 42f, 6f, 6f, paint)
                paint.style = Paint.Style.FILL

                paint.textSize = 9f
                paint.color = Color.parseColor("#4A525A")
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                canvas.drawText(statMetrics[i].first, cx + 8f, y + 16f, paint)

                paint.textSize = 12f
                paint.color = Color.parseColor("#2A1D15")
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                canvas.drawText(statMetrics[i].second, cx + 8f, y + 34f, paint)
            }
            y += 52f

            // Table of Recent Game Sessions
            paint.color = Color.parseColor("#EDE0D0")
            canvas.drawRect(30f, y, 565f, y + 18f, paint)
            paint.color = Color.parseColor("#2A1D15")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            canvas.drawText("DATE / TIME", 35f, y + 13f, paint)
            canvas.drawText("GAME ID", 150f, y + 13f, paint)
            canvas.drawText("LEVEL", 280f, y + 13f, paint)
            canvas.drawText("ACCURACY", 340f, y + 13f, paint)
            canvas.drawText("LATENCY", 420f, y + 13f, paint)
            canvas.drawText("ADAPTATION", 490f, y + 13f, paint)
            y += 20f

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            if (data.sessions.isEmpty()) {
                paint.color = Color.parseColor("#4A525A")
                canvas.drawText("No completed game sessions recorded yet.", 35f, y + 14f, paint)
                y += 20f
            } else {
                val recentSessions = data.sessions.takeLast(4).reversed()
                for (s in recentSessions) {
                    paint.color = Color.parseColor("#2A1D15")
                    canvas.drawText(dateFormat.format(Date(s.timestamp)), 35f, y + 12f, paint)
                    canvas.drawText(s.gameId.replace("_", " "), 150f, y + 12f, paint)
                    canvas.drawText("L${s.difficultyLevel}", 280f, y + 12f, paint)
                    canvas.drawText("${(s.accuracy * 100).toInt()}%", 340f, y + 12f, paint)
                    canvas.drawText("${s.reactionTimeMs} ms", 420f, y + 12f, paint)
                    canvas.drawText("Rec: L${s.adaptationDecision}", 490f, y + 12f, paint)
                    y += 16f
                }
            }
            y += 12f
        }

        // SECTION 2: Caregiver & ASHA Field Observations
        if (data.includeCareLogs) {
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 12f
            paint.color = Color.parseColor("#245C45")
            canvas.drawText("2. CAREGIVER & ASHA FIELD OBSERVATIONS", 30f, y + 14f, paint)
            y += 24f

            // Priority Status Pill
            if (data.includePriority) {
                paint.color = when (data.carePriorityStatus) {
                    "PRIORITY" -> Color.parseColor("#8B183F")
                    "WATCH" -> Color.parseColor("#B45309")
                    else -> Color.parseColor("#245C45")
                }
                canvas.drawRoundRect(30f, y, 220f, y + 24f, 6f, 6f, paint)
                paint.color = Color.WHITE
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                canvas.drawText("TODAY'S CARE PRIORITY: ${data.carePriorityStatus}", 40f, y + 16f, paint)
                y += 32f
            }

            // Table of Recent Care Logs
            paint.color = Color.parseColor("#EDE0D0")
            canvas.drawRect(30f, y, 565f, y + 18f, paint)
            paint.color = Color.parseColor("#2A1D15")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            canvas.drawText("TIMESTAMP", 35f, y + 13f, paint)
            canvas.drawText("ROLE", 140f, y + 13f, paint)
            canvas.drawText("CATEGORY", 210f, y + 13f, paint)
            canvas.drawText("SEVERITY", 300f, y + 13f, paint)
            canvas.drawText("OBSERVATION NOTES", 380f, y + 13f, paint)
            y += 20f

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            if (data.careLogs.isEmpty()) {
                paint.color = Color.parseColor("#4A525A")
                canvas.drawText("No caregiver or field visit logs recorded yet.", 35f, y + 14f, paint)
                y += 20f
            } else {
                val recentLogs = data.careLogs.takeLast(4).reversed()
                for (l in recentLogs) {
                    paint.color = Color.parseColor("#2A1D15")
                    canvas.drawText(dateFormat.format(Date(l.timestamp)), 35f, y + 12f, paint)
                    canvas.drawText(l.authorRole, 140f, y + 12f, paint)
                    canvas.drawText(l.category, 210f, y + 12f, paint)
                    canvas.drawText(l.severity, 300f, y + 12f, paint)
                    val cleanNote = if (l.notes.length > 32) l.notes.substring(0, 30) + "..." else l.notes
                    canvas.drawText(cleanNote, 380f, y + 12f, paint)
                    y += 16f
                }
            }
            y += 12f
        }

        // SECTION 3: Clinician Guidance & Care Plan
        if (data.includeGuidance) {
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 12f
            paint.color = Color.parseColor("#245C45")
            canvas.drawText("3. CLINICIAN GUIDANCE & REVIEW NOTES", 30f, y + 14f, paint)
            y += 22f

            paint.color = Color.parseColor("#F5EFE6")
            canvas.drawRoundRect(30f, y, 565f, y + 45f, 6f, 6f, paint)
            paint.style = Paint.Style.STROKE
            paint.color = Color.parseColor("#D4C3AC")
            canvas.drawRoundRect(30f, y, 565f, y + 45f, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.parseColor("#2A1D15")
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            canvas.drawText("Care Guidance: ${data.clinicianNotes}", 40f, y + 20f, paint)
            canvas.drawText("Follow-up Action: Review scheduled in 4 weeks. Continue daily reminders & reminiscence engagement.", 40f, y + 36f, paint)
            y += 58f
        }

        // SECTION 4: Mandatory Legal / Medical Disclaimer Box
        paint.color = Color.parseColor("#EFE7DA")
        canvas.drawRoundRect(30f, y, 565f, y + 50f, 6f, 6f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#CB8067")
        canvas.drawRoundRect(30f, y, 565f, y + 50f, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.parseColor("#720227")
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("STATUTORY HEALTHCARE NOTICE & CLINICAL DISCLAIMER:", 40f, y + 16f, paint)

        paint.color = Color.parseColor("#4A525A")
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("This summary dossier is generated strictly from informal cognitive gameplay participation and observational logs", 40f, y + 28f, paint)
        canvas.drawText("recorded by family caregivers and community ASHA health workers. It does NOT constitute a standalone medical", 40f, y + 38f, paint)
        canvas.drawText("diagnosis, dementia staging, or prognostic determination. Compliant with DPDPA 2023 patient privacy norms.", 40f, y + 48f, paint)

        document.finishPage(page)

        // Write to application documents directory
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "AASRITI_Consultation_Summary_${data.patientId}.pdf")
        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()
        return file
    }

    fun createShareIntent(context: Context, pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "AASRITI Consultation Summary Dossier")
            putExtra(Intent.EXTRA_TEXT, "Attached is the offline AASRITI consultation and care summary dossier for review.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun createViewIntent(context: Context, pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
