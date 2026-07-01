package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditProfileScreen(
    onBackClick: () -> Unit,
    onProfileUpdated: () -> Unit,
    viewModel: StudentEditProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        StudentEditProfileHeader(
            onBackClick = onBackClick
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    StudentEditProfileField(
                        label = stringResource(R.string.email),
                        value = state.email,
                        onValueChange = {},
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    StudentEditProfileField(
                        label = stringResource(R.string.first_name),
                        value = state.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        errorRes = state.firstNameErrorRes,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    StudentEditProfileField(
                        label = stringResource(R.string.last_name),
                        value = state.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        errorRes = state.lastNameErrorRes,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── Instituição de Ensino (dropdown) ─────────────────────
                    var institutionMenuExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = stringResource(R.string.select_education_institution),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.isLoadingInstitutions) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                    } else if (state.availableInstitutions.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_institutions_available),
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        val selectedInstitution = state.availableInstitutions.find {
                            it.id == state.selectedInstitutionId
                        }
                        val institutionLabel = selectedInstitution?.displayName
                            ?: state.selectedInstitutionName

                        ExposedDropdownMenuBox(
                            expanded = institutionMenuExpanded,
                            onExpandedChange = { institutionMenuExpanded = !institutionMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = institutionLabel,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = institutionMenuExpanded) },
                                isError = state.educationInstitutionErrorRes != null,
                                supportingText = state.educationInstitutionErrorRes?.let { resId ->
                                    { Text(stringResource(resId), color = Color(0xFFB00020)) }
                                },
                                placeholder = { Text(stringResource(R.string.select_institution_placeholder)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedTextColor = Color.Black,
                                    unfocusedBorderColor = if (state.educationInstitutionErrorRes != null)
                                        Color(0xFFB00020) else Color(0xFFD9D9D9),
                                    unfocusedTrailingIconColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = institutionMenuExpanded,
                                onDismissRequest = { institutionMenuExpanded = false },
                                modifier = Modifier.heightIn(max = 240.dp)
                            ) {
                                state.availableInstitutions.forEach { institution ->
                                    DropdownMenuItem(
                                        text = { Text(institution.displayName) },
                                        onClick = {
                                            viewModel.onInstitutionSelected(institution.id, institution.displayName)
                                            institutionMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        state.educationInstitutionErrorRes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(it),
                                color = Color(0xFFB00020),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    state.errorMessageRes?.let { errorRes ->
                        Text(
                            text = stringResource(errorRes),
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    state.successMessageRes?.let { successRes ->
                        Text(
                            text = stringResource(successRes),
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.saveProfile(
                                onSuccess = onBackClick
                            )
                        },
                        enabled = !state.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDFA52),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFFE5E5A0),
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (state.isSaving) {
                                stringResource(R.string.saving)
                            } else {
                                stringResource(R.string.save_changes)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !state.isSaving
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StudentEditProfileHeader(
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
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

        Text(
            text = stringResource(R.string.edit_profile),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun StudentEditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    errorRes: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = errorRes != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFD9D9D9),
                disabledBorderColor = Color(0xFFD9D9D9),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color(0xFF8A8A8A),
                cursorColor = Color.Black
            )
        )

        errorRes?.let {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(it),
                color = Color(0xFFB00020),
                fontSize = 12.sp
            )
        }
    }
}