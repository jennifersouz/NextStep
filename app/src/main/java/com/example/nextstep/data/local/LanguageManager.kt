package com.example.nextstep.data.local

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageManager {

    val currentLanguage: String
        get() = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            .ifBlank { "pt" }

    fun changeLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}