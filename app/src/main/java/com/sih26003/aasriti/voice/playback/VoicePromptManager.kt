package com.sih26003.aasriti.voice.playback

import android.content.Context
import android.speech.tts.TextToSpeech
import com.google.gson.Gson
import com.sih26003.aasriti.voice.packs.LanguagePack
import java.io.InputStreamReader
import java.util.Locale

/**
 * Common Shared Voice Application Layer.
 * NOT a game. Provides voice guidance, reads prompts, and delivers calm encouragement.
 * 100% offline using bundled local language packs and on-device TextToSpeech.
 */
class VoicePromptManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var currentLanguagePack: LanguagePack? = null
    private var currentLanguageCode: String = "as"

    init {
        tts = TextToSpeech(context, this)
        loadLanguagePack("as")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            applyLocaleForLanguage(currentLanguageCode)
        }
    }

    fun setLanguage(langCode: String) {
        currentLanguageCode = langCode
        loadLanguagePack(langCode)
        if (isTtsReady) {
            applyLocaleForLanguage(langCode)
        }
    }

    private fun applyLocaleForLanguage(langCode: String) {
        val locale = when (langCode) {
            "as" -> Locale("as", "IN")
            "mn" -> Locale("mni", "IN")
            "kha" -> Locale("en", "IN") // Khasi standard Latin script fallback
            "br" -> Locale("brx", "IN")
            "hi" -> Locale("hi", "IN")
            else -> Locale("en", "IN")
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to Indian English if regional TTS voice data is not installed on device
            tts?.setLanguage(Locale("en", "IN"))
        }
        tts?.setSpeechRate(0.85f) // Calibrated slower pace for elderly cognition
        tts?.setPitch(1.0f)
    }

    private fun loadLanguagePack(langCode: String) {
        val filename = "language-packs/${langCode}_prompts.json"
        try {
            context.assets.open(filename).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    currentLanguagePack = Gson().fromJson(reader, LanguagePack::class.java)
                }
            }
        } catch (e: Exception) {
            // Fallback to English if regional pack missing
            try {
                context.assets.open("language-packs/en_prompts.json").use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        currentLanguagePack = Gson().fromJson(reader, LanguagePack::class.java)
                    }
                }
            } catch (err: Exception) {
                currentLanguagePack = null
            }
        }
    }

    fun getPromptText(key: String, fallback: String): String {
        return currentLanguagePack?.prompts?.get(key) ?: fallback
    }

    fun speak(text: String) {
        if (isTtsReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "aasriti_prompt_${System.currentTimeMillis()}")
        }
    }

    fun speakPromptKey(key: String, fallback: String) {
        val promptText = getPromptText(key, fallback)
        speak(promptText)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
