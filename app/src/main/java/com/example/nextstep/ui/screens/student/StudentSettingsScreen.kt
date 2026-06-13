package com.example.nextstep.ui.screens.student

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.nextstep.R

enum class AppLanguage(val tag: String) {
    PORTUGUESE(tag = "pt"),
    ENGLISH(tag = "en")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSettingsScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit = {}
) {
    var showLanguageSheet by remember {
        mutableStateOf(false)
    }

    val currentLanguage = getCurrentAppLanguage()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        StudentSettingsHeader(
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        StudentSettingsSimpleItem(
            title = stringResource(R.string.edit_profile),
            onClick = onEditProfileClick
        )

        StudentSettingsLanguageItem(
            title = stringResource(R.string.change_language),
            currentLanguage = when (currentLanguage) {
                AppLanguage.PORTUGUESE -> stringResource(R.string.language_portuguese)
                AppLanguage.ENGLISH -> stringResource(R.string.language_english)
            },
            onClick = {
                showLanguageSheet = true
            }
        )
    }

    if (showLanguageSheet) {
        StudentLanguageBottomSheet(
            currentLanguage = currentLanguage,
            onDismiss = {
                showLanguageSheet = false
            },
            onSave = { selectedLanguage ->
                setAppLanguage(selectedLanguage)
                showLanguageSheet = false
            }
        )
    }
}

@Composable
fun StudentSettingsHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp,
                end = 16.dp,
                top = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black
            )
        }

        Text(
            text = stringResource(R.string.settings),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun StudentSettingsSimpleItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun StudentSettingsLanguageItem(
    title: String,
    currentLanguage: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = currentLanguage,
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLanguageBottomSheet(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (AppLanguage) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var selectedLanguage by remember {
        mutableStateOf(currentLanguage)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 22.dp,
                    bottom = 28.dp
                )
        ) {
            Text(
                text = stringResource(R.string.available_languages),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            StudentLanguageOption(
                label = stringResource(R.string.language_portuguese),
                selected = selectedLanguage == AppLanguage.PORTUGUESE,
                onClick = {
                    selectedLanguage = AppLanguage.PORTUGUESE
                }
            )

            StudentLanguageOption(
                label = stringResource(R.string.language_english),
                selected = selectedLanguage == AppLanguage.ENGLISH,
                onClick = {
                    selectedLanguage = AppLanguage.ENGLISH
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    onSave(selectedLanguage)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StudentLanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Black,
                unselectedColor = Color(0xFFD9D9D9)
            )
        )
    }
}

fun getCurrentAppLanguage(): AppLanguage {
    val languageTags = AppCompatDelegate
        .getApplicationLocales()
        .toLanguageTags()

    return if (languageTags.startsWith("en")) {
        AppLanguage.ENGLISH
    } else {
        AppLanguage.PORTUGUESE
    }
}

fun setAppLanguage(language: AppLanguage) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(language.tag)
    )
}