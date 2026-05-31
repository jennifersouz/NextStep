package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.ui.components.ProfileResponsiveLayout
import com.example.nextstep.ui.components.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyEditProfileScreen(
    onBackClick: () -> Unit,
    onProfileUpdated: () -> Unit,
    viewModel: CompanyEditProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val landscape = isLandscape()

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                // Back button — sempre no topo, fora do layout responsivo
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.Black
                    )
                }

                if (landscape) {
                    ProfileResponsiveLayout(
                        modifier = Modifier.weight(1f),
                        headerContent = {
                            CompanyEditProfileLandscapeHeader(
                                companyName = state.companyName
                            )
                        },
                        bodyContent = {
                            CompanyEditProfileFormContent(
                                state = state,
                                viewModel = viewModel,
                                onProfileUpdated = onProfileUpdated
                            )
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                            .padding(horizontal = 28.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.edit_company_profile),
                            color = Color.Black,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        CompanyEditProfileFormContent(
                            state = state,
                            viewModel = viewModel,
                            onProfileUpdated = onProfileUpdated
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyEditProfileLandscapeHeader(
    companyName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo com as iniciais da empresa
        val initials = companyName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "?" }

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8392A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.edit_company_profile),
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyEditProfileFormContent(
    state: CompanyEditProfileUiState,
    viewModel: CompanyEditProfileViewModel,
    onProfileUpdated: () -> Unit
) {
    CompanyEditTextField(
        value = state.companyName,
        onValueChange = viewModel::onCompanyNameChange,
        label = stringResource(R.string.company_name),
        errorText = state.companyNameErrorRes?.let { stringResource(it) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    CompanyEditTextField(
        value = state.businessArea,
        onValueChange = viewModel::onBusinessAreaChange,
        label = stringResource(R.string.business_area)
    )

    Spacer(modifier = Modifier.height(16.dp))

    CompanyEditTextField(
        value = state.location,
        onValueChange = viewModel::onLocationChange,
        label = stringResource(R.string.location)
    )

    Spacer(modifier = Modifier.height(16.dp))

    CompanyEditTextField(
        value = state.phone,
        onValueChange = viewModel::onPhoneChange,
        label = stringResource(R.string.phone),
        errorText = state.phoneErrorRes?.let { stringResource(it) },
        keyboardType = KeyboardType.Phone
    )

    Spacer(modifier = Modifier.height(16.dp))

    CompanyEditTextField(
        value = state.description,
        onValueChange = viewModel::onDescriptionChange,
        label = stringResource(R.string.description),
        minLines = 5,
        maxLines = 8
    )

    if (state.errorMessageRes != null) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(state.errorMessageRes!!),
            color = Color(0xFFB00020),
            fontSize = 14.sp
        )
    }

    Spacer(modifier = Modifier.height(30.dp))

    Button(
        onClick = {
            viewModel.saveProfile(onSuccess = onProfileUpdated)
        },
        enabled = !state.isSaving,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFDFA52),
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFFEAEAEA),
            disabledContentColor = Color(0xFF8A8A8A)
        )
    ) {
        if (state.isSaving) {
            CircularProgressIndicator(
                color = Color.Black,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.save),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(modifier = Modifier.height(40.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF8A8A8A),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = errorText != null,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE1E1E1),
                errorBorderColor = Color(0xFFB00020),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        if (errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = errorText,
                color = Color(0xFFB00020),
                fontSize = 12.sp
            )
        }
    }
}
