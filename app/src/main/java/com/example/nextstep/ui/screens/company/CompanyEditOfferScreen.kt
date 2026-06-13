package com.example.nextstep.ui.screens.company

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun CompanyEditOfferScreen(
    offerId: String,
    onBackClick: () -> Unit,
    onOfferUpdated: () -> Unit,
    viewModel: CompanyEditOfferViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            onOfferUpdated()
        }
    }

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
                        .padding(horizontal = 28.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(R.string.edit_offer),
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                CompanyEditOfferOutlinedTextField(
                                    value = state.title,
                                    onValueChange = viewModel::onTitleChange,
                                    label = stringResource(R.string.offer_title),
                                    isError = state.titleError != null,
                                    supportingText = state.titleError
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferOutlinedTextField(
                                    value = state.area,
                                    onValueChange = viewModel::onAreaChange,
                                    label = stringResource(R.string.offer_area),
                                    isError = state.areaError != null,
                                    supportingText = state.areaError
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferOutlinedTextField(
                                    value = state.location,
                                    onValueChange = viewModel::onLocationChange,
                                    label = stringResource(R.string.offer_location),
                                    isError = state.locationError != null,
                                    supportingText = state.locationError
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferDropdownField(
                                    label = stringResource(R.string.offer_work_mode),
                                    selectedValue = state.workMode,
                                    onValueChange = viewModel::onWorkModeChange,
                                    items = listOf("Presencial", "Remoto", "Híbrido"),
                                    isError = state.workModeError != null,
                                    supportingText = state.workModeError
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferOutlinedTextField(
                                    value = state.duration,
                                    onValueChange = viewModel::onDurationChange,
                                    label = stringResource(R.string.offer_duration),
                                    isError = state.durationError != null,
                                    supportingText = state.durationError
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferOutlinedTextField(
                                    value = state.vacancies,
                                    onValueChange = viewModel::onVacanciesChange,
                                    label = stringResource(R.string.offer_vacancies),
                                    isError = state.vacanciesError != null,
                                    supportingText = state.vacanciesError,
                                    keyboardType = KeyboardType.Number
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                CompanyEditOfferOutlinedTextField(
                                    value = state.description,
                                    onValueChange = viewModel::onDescriptionChange,
                                    label = stringResource(R.string.offer_description),
                                    isError = state.descriptionError != null,
                                    supportingText = state.descriptionError,
                                    minLines = 4,
                                    maxLines = 7
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferOutlinedTextField(
                                    value = state.requirements,
                                    onValueChange = viewModel::onRequirementsChange,
                                    label = stringResource(R.string.offer_requirements),
                                    minLines = 4,
                                    maxLines = 7
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                CompanyEditOfferStatusAndButton(state = state, viewModel = viewModel)
                            }
                        }
                    } else {
                        CompanyEditOfferOutlinedTextField(
                            value = state.title,
                            onValueChange = viewModel::onTitleChange,
                            label = stringResource(R.string.offer_title),
                            isError = state.titleError != null,
                            supportingText = state.titleError
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferOutlinedTextField(
                            value = state.description,
                            onValueChange = viewModel::onDescriptionChange,
                            label = stringResource(R.string.offer_description),
                            isError = state.descriptionError != null,
                            supportingText = state.descriptionError,
                            minLines = 4,
                            maxLines = 7
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferOutlinedTextField(
                            value = state.area,
                            onValueChange = viewModel::onAreaChange,
                            label = stringResource(R.string.offer_area),
                            isError = state.areaError != null,
                            supportingText = state.areaError
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferOutlinedTextField(
                            value = state.location,
                            onValueChange = viewModel::onLocationChange,
                            label = stringResource(R.string.offer_location),
                            isError = state.locationError != null,
                            supportingText = state.locationError
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferDropdownField(
                            label = stringResource(R.string.offer_work_mode),
                            selectedValue = state.workMode,
                            onValueChange = viewModel::onWorkModeChange,
                            items = listOf("Presencial", "Remoto", "Híbrido"),
                            isError = state.workModeError != null,
                            supportingText = state.workModeError
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferOutlinedTextField(
                            value = state.duration,
                            onValueChange = viewModel::onDurationChange,
                            label = stringResource(R.string.offer_duration),
                            isError = state.durationError != null,
                            supportingText = state.durationError
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferOutlinedTextField(
                            value = state.vacancies,
                            onValueChange = viewModel::onVacanciesChange,
                            label = stringResource(R.string.offer_vacancies),
                            isError = state.vacanciesError != null,
                            supportingText = state.vacanciesError,
                            keyboardType = KeyboardType.Number
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferOutlinedTextField(
                            value = state.requirements,
                            onValueChange = viewModel::onRequirementsChange,
                            label = stringResource(R.string.offer_requirements),
                            minLines = 4,
                            maxLines = 7
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CompanyEditOfferStatusAndButton(state = state, viewModel = viewModel)
                    }

                    Spacer(modifier = Modifier.height(42.dp))
                }
            }
        }
    }
}

@Composable
private fun CompanyEditOfferStatusAndButton(
    state: CompanyEditOfferUiState,
    viewModel: CompanyEditOfferViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = state.isActive,
            onCheckedChange = viewModel::onActiveChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Black,
                uncheckedColor = Color(0xFF8A8A8A),
                checkmarkColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Oferta ativa",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    if (state.errorMessage != null) {
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = state.errorMessage!!,
            color = Color(0xFFB00020),
            fontSize = 14.sp
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { viewModel.saveOffer() },
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
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Guardar alterações",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CompanyEditOfferOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    supportingText: String? = null,
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
            isError = isError,
            supportingText = supportingText?.let { { Text(it, color = Color(0xFFB00020)) } },
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
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}

@Composable
private fun CompanyEditOfferDropdownField(
    label: String,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    isError: Boolean = false,
    supportingText: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            color = Color(0xFF8A8A8A),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                isError = isError,
                supportingText = supportingText?.let { { Text(it, color = Color(0xFFB00020)) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color(0xFFE1E1E1),
                    errorBorderColor = Color(0xFFB00020),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                        },
                        onClick = {
                            onValueChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}