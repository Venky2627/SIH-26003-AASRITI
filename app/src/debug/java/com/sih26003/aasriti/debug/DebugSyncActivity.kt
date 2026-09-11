package com.sih26003.aasriti.debug

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.sih26003.aasriti.AasritiApplication
import kotlinx.coroutines.launch

/**
 * DEBUG-ONLY Activity for manually triggering opportunistic Cloud Firestore sync.
 *
 * This Activity exists ONLY in the app/src/debug source set and is never packaged in release builds.
 */
class DebugSyncActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as AasritiApplication

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val titleView = TextView(this).apply {
            text = "⚡ AASRITI Debug Firebase Sync Tester"
            textSize = 20f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(titleView)

        val statusView = TextView(this).apply {
            text = "Ready to trigger SyncManager.processPendingBatch()"
            textSize = 16f
            setPadding(0, 0, 0, 32)
        }

        val syncButton = Button(this).apply {
            text = "TRIGGER PROCESS PENDING BATCH"
            setOnClickListener {
                isEnabled = false
                statusView.text = "Syncing with Cloud Firestore..."

                lifecycleScope.launch {
                    try {
                        val count = app.syncManager.processPendingBatch()
                        if (count > 0) {
                            statusView.text = "SUCCESS: Remotely acknowledged and synced $count items."
                        } else {
                            statusView.text = "RESULT: 0 items synced. Check Logcat tag 'FirestoreSyncAdapter' for details (e.g. offline, auth failure, or 0 pending items in sync_queue)."
                        }
                    } catch (e: Exception) {
                        statusView.text = "FAILURE: Sync failed with exception: ${e.message}"
                    } finally {
                        isEnabled = true
                    }
                }
            }
        }
        layout.addView(syncButton)
        layout.addView(statusView)

        setContentView(layout)
    }
}
