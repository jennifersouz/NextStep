package com.example.nextstep.ui.screens.student

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun StudentApplicationScreen(
    offerId: String,
    onBackClick: () -> Unit,
    viewModel: StudentApplicationViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    val motivationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Alguns providers não dão permissão persistente. A leitura imediata continua a funcionar.
            }

            viewModel.onMotivationLetterSelected(
                uri = it,
                fileName = getFileName(context, it)
            )
        }
    }

    val cvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Alguns providers não dão permissão persistente. A leitura imediata continua a funcionar.
            }

            viewModel.onCvSelected(
                uri = it,
                fileName = getFileName(context, it)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        when {
            state.isLoading -> {
                ApplicationLoadingState()
            }

            state.errorMessageRes != null -> {
                val errorRes = state.errorMessageRes

                if (errorRes != null) {
                    ApplicationErrorState(
                        message = stringResource(errorRes),
                        onBackClick = onBackClick
                    )
                }
            }

            state.offer != null -> {
                val offer = state.offer

                if (offer != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 28.dp, vertical = 24.dp)
                    ) {
                        ApplicationHeader(
                            title = stringResource(R.string.apply_screen_title),
                            onBackClick = onBackClick
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ApplicationCompanyLogo(
                                companyName = offer.companyName
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = offer.companyName,
                                    fontSize = 18.sp,
                                    color = Color(0xFF8A8A8A)
                                )

                                Text(
                                    text = offer.title,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    lineHeight = 30.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = stringResource(R.string.application_documents_title),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ApplicationDocumentField(
                            label = stringResource(R.string.motivation_letter),
                            fileName = state.motivationLetterName.ifBlank {
                                stringResource(R.string.motivation_letter_placeholder)
                            },
                            onUploadClick = {
                                motivationLauncher.launch(arrayOf("application/pdf"))
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ApplicationDocumentField(
                            label = stringResource(R.string.cv),
                            fileName = state.cvName.ifBlank {
                                stringResource(R.string.cv_placeholder)
                            },
                            onUploadClick = {
                                cvLauncher.launch(arrayOf("application/pdf"))
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        state.submitErrorRes?.let { errorRes ->
                            Text(
                                text = stringResource(errorRes),
                                color = Color(0xFFB00020),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        state.submitSuccessRes?.let { successRes ->
                            Text(
                                text = stringResource(successRes),
                                color = Color(0xFF2E7D32),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.submitApplication(context)
                            },
                            enabled = !state.isSubmitting && state.submitSuccessRes == null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFDFA52),
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xFFE5E5A0),
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = when {
                                    state.isSubmitting -> stringResource(R.string.submitting_application)
                                    state.submitSuccessRes != null -> stringResource(R.string.applied_button)
                                    else -> stringResource(R.string.submit_application)
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ApplicationDocumentField(
    label: String,
    fileName: String,
    onUploadClick: () -> Unit
) {
    Text(
        text = label,
        fontSize = 17.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fileName,
            color = Color(0xFF8A8A8A),
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onUploadClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(R.string.upload),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ApplicationCompanyLogo(
    companyName: String
) {
    val initials = companyName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(Color(0xFFFDFA52))
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ApplicationLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Black
        )
    }
}

@Composable
fun ApplicationErrorState(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
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
        }

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFFB00020),
            textAlign = TextAlign.Center
        )
    }
}

fun getFileName(
    context: Context,
    uri: Uri
): String {
    var fileName = "documento.pdf"

    context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

        if (cursor.moveToFirst() && nameIndex >= 0) {
            fileName = cursor.getString(nameIndex)
        }
    }

    return fileName
}