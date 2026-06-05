package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun AddCompanyAdvisorScreen(
    onBackClick: () -> Unit,
    onAdvisorCreated: () -> Unit,
    viewModel: AddCompanyAdvisorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
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
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.register_advisor),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.advisor_invite_description),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(26.dp))

            AdvisorInviteTextField(
                label = stringResource(R.string.advisor_name),
                value = state.name,
                onValueChange = viewModel::onNameChange,
                errorText = state.nameErrorRes?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdvisorInviteTextField(
                label = stringResource(R.string.advisor_email),
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                errorText = state.emailErrorRes?.let { stringResource(it) },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdvisorInviteTextField(
                label = stringResource(R.string.advisor_phone),
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdvisorInviteTextField(
                label = stringResource(R.string.advisor_department),
                value = state.department,
                onValueChange = viewModel::onDepartmentChange
            )

            if (state.errorMessageRes != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    viewModel.createAdvisor(
                        onSuccess = onAdvisorCreated
                    )
                },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
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
                        text = stringResource(R.string.send_advisor_invite),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AdvisorInviteTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = errorText != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE1E1E1),
                errorBorderColor = Color(0xFFB00020),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White
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