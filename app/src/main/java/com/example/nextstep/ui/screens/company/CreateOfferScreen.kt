package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun CreateOfferScreen(
    onBackClick: () -> Unit = {},
    onOfferCreated: () -> Unit = {},
    viewModel: CreateOfferViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 28.dp, vertical = 32.dp)
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
                    tint = Color.Black
                )
            }

            Text(
                text = stringResource(R.string.create_offer_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        CreateOfferTextField(
            label = stringResource(R.string.offer_position_required),
            value = state.title,
            onValueChange = viewModel::onTitleChange,
            placeholder = stringResource(R.string.offer_position_placeholder),
            errorMessageRes = state.titleError
        )

        CreateOfferTextField(
            label = stringResource(R.string.offer_description_required),
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            placeholder = stringResource(R.string.offer_description_placeholder),
            errorMessageRes = state.descriptionError,
            minLines = 3
        )

        CreateOfferDropdownField(
            label = stringResource(R.string.offer_area_required),
            placeholder = stringResource(R.string.select),
            selectedText = state.selectedArea?.let { stringResource(it.labelRes) },
            errorMessageRes = state.areaError,
            items = OfferArea.entries,
            itemLabel = { area -> stringResource(area.labelRes) },
            onItemSelected = viewModel::onAreaChange
        )

        CreateOfferTextField(
            label = stringResource(R.string.offer_location_required),
            value = state.location,
            onValueChange = viewModel::onLocationChange,
            placeholder = stringResource(R.string.offer_location_placeholder),
            errorMessageRes = state.locationError
        )

        CreateOfferDropdownField(
            label = stringResource(R.string.offer_work_mode_required),
            placeholder = stringResource(R.string.select),
            selectedText = state.selectedWorkMode?.let { stringResource(it.labelRes) },
            errorMessageRes = state.workModeError,
            items = WorkMode.entries,
            itemLabel = { workMode -> stringResource(workMode.labelRes) },
            onItemSelected = viewModel::onWorkModeChange
        )

        CreateOfferTextField(
            label = stringResource(R.string.offer_duration_required),
            value = state.duration,
            onValueChange = viewModel::onDurationChange,
            placeholder = stringResource(R.string.offer_duration_placeholder),
            errorMessageRes = state.durationError
        )

        CreateOfferTextField(
            label = stringResource(R.string.offer_vacancies_required),
            value = state.vacancies,
            onValueChange = viewModel::onVacanciesChange,
            placeholder = stringResource(R.string.offer_vacancies_placeholder),
            errorMessageRes = state.vacanciesError,
            keyboardType = KeyboardType.Number
        )

        CreateOfferTextField(
            label = stringResource(R.string.offer_requirements_required),
            value = state.requirements,
            onValueChange = viewModel::onRequirementsChange,
            placeholder = stringResource(R.string.offer_requirements_placeholder),
            errorMessageRes = state.requirementsError,
            minLines = 3
        )

        state.generalError?.let { errorRes ->
            Text(
                text = stringResource(errorRes),
                color = Color(0xFFB00020),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        state.successMessage?.let { successRes ->
            Text(
                text = stringResource(successRes),
                color = Color(0xFF2E7D32),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.createOffer(
                    onSuccess = onOfferCreated
                )
            },
            enabled = !state.isLoading,
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
                text = if (state.isLoading) {
                    stringResource(R.string.creating)
                } else {
                    stringResource(R.string.create)
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CreateOfferTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessageRes: Int? = null,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(
        text = label,
        fontSize = 17.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF8A8A8A)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = minLines == 1,
        minLines = minLines,
        isError = errorMessageRes != null,
        supportingText = {
            errorMessageRes?.let {
                Text(
                    text = stringResource(it),
                    color = Color(0xFFB00020)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD9D9D9),
            unfocusedBorderColor = Color(0xFFD9D9D9),
            errorBorderColor = Color(0xFFB00020),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(22.dp))
}

@Composable
fun <T> CreateOfferDropdownField(
    label: String,
    placeholder: String,
    selectedText: String?,
    errorMessageRes: Int?,
    items: List<T>,
    itemLabel: @Composable (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = label,
        fontSize = 17.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column {
        OutlinedTextField(
            value = selectedText ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF8A8A8A)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                },
            shape = RoundedCornerShape(10.dp),
            isError = errorMessageRes != null,
            supportingText = {
                errorMessageRes?.let {
                    Text(
                        text = stringResource(it),
                        color = Color(0xFFB00020)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD9D9D9),
                unfocusedBorderColor = Color(0xFFD9D9D9),
                errorBorderColor = Color(0xFFB00020),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemLabel(item),
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(22.dp))
}