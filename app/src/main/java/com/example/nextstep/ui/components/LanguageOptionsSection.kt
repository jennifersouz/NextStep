package com.example.nextstep.ui.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageOptionsSection(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val currentLanguage = getCurrentLanguageCode()
    val normalizedLanguage = normalizeLanguageCode(currentLanguage)

    val selectedLanguageLabel = if (normalizedLanguage == "pt") {
        "\uD83C\uDDF5\uD83C\uDDF9 " + stringResource(R.string.language_portuguese)
    } else {
        "\uD83C\uDDEC\uD83C\uDDE7 " + stringResource(R.string.language_english)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.language_options),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLanguageLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("\uD83C\uDDF5\uD83C\uDDF9 " + stringResource(R.string.language_portuguese))
                    },
                    onClick = {
                        expanded = false
                        onLanguageSelected("pt")
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("\uD83C\uDDEC\uD83C\uDDE7 " + stringResource(R.string.language_english))
                    },
                    onClick = {
                        expanded = false
                        onLanguageSelected("en")
                    }
                )
            }
        }
    }
}

private fun getCurrentLanguageCode(): String {
    val locales = AppCompatDelegate.getApplicationLocales()
    val languageTag = if (locales.isEmpty) {
        java.util.Locale.getDefault().toLanguageTag()
    } else {
        locales[0]?.toLanguageTag() ?: java.util.Locale.getDefault().toLanguageTag()
    }
    return languageTag
}

private fun normalizeLanguageCode(languageCode: String): String {
    return when {
        languageCode.lowercase().startsWith("pt") -> "pt"
        languageCode.lowercase().startsWith("en") -> "en"
        else -> "pt"
    }
}