package com.sih26003.smritisetu.voice.packs

import com.google.gson.annotations.SerializedName

data class LanguagePack(
    @SerializedName("language")
    val language: String,
    
    @SerializedName("language_name")
    val languageName: String,
    
    @SerializedName("prompts")
    val prompts: Map<String, String>
)
