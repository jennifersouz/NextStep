package com.example.nextstep.ui.screens.institution

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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionEditProfileScreen(
    onBackClick: () -> Unit,
    onProfileUpdated: () -> Unit,
    viewModel: InstitutionEditProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

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
                    .padding(start = 28.dp, end = 28.dp)
            ) {
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.edit_profile),
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    InstitutionEditTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.institution_name),
                        errorText = state.nameErrorRes?.let { stringResource(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InstitutionEditTextField(
                        value = state.nif,
                        onValueChange = viewModel::onNifChange,
                        label = stringResource(R.string.nif),
                        errorText = state.nifErrorRes?.let { stringResource(it) },
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InstitutionEditTextField(
                        value = state.locality,
                        onValueChange = viewModel::onLocalityChange,
                        label = stringResource(R.string.locality)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InstitutionEditTextField(
                        value = state.address,
                        onValueChange = viewModel::onAddressChange,
                        label = stringResource(R.string.address)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InstitutionEditTextField(
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = stringResource(R.string.phone),
                        keyboardType = KeyboardType.Phone
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstitutionEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
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
            singleLine = true,
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