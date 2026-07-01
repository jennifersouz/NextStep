package com.example.nextstep.ui.screens.company

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.ui.utils.Formatters

@Composable
fun CompanyOfferDetailScreen(
    offerId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    offerUpdated: Boolean = false,
    viewModel: CompanyOfferDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(offerUpdated) {
        if (offerUpdated) {
            viewModel.loadOffer(offerId)
        }
    }

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showActivateDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text(text = stringResource(R.string.deactivate_offer)) },
            text = { Text(text = stringResource(R.string.deactivate_offer_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeactivateDialog = false
                        viewModel.deactivateOffer(offerId = offerId, onSuccess = onBackClick)
                    }
                ) {
                    Text(text = stringResource(R.string.deactivate), color = Color(0xFFB00020))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showActivateDialog) {
        AlertDialog(
            onDismissRequest = { showActivateDialog = false },
            title = { Text(text = stringResource(R.string.activate_offer)) },
            text = { Text(text = stringResource(R.string.activate_offer_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showActivateDialog = false
                        viewModel.activateOffer(offerId = offerId)
                    }
                ) {
                    Text(text = stringResource(R.string.activate), color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showActivateDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text(text = stringResource(R.string.archive_offer)) },
            text = { Text(text = stringResource(R.string.archive_offer_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showArchiveDialog = false
                        viewModel.archiveOffer(offerId = offerId, reason = null)
                    }
                ) {
                    Text(text = stringResource(R.string.archive), color = Color(0xFFB00020))
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (state.successMessage != null) {
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            containerColor = Color(0xFF138A36)
        ) {
            Text(text = state.successMessage!!, color = Color.White)
        }
        LaunchedEffect(state.successMessage) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccessMessage()
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

        state.errorMessageRes != null && state.offer == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    textAlign = TextAlign.Center
                )
            }
        }

        state.offer != null -> {
            CompanyOfferDetailContent(
                offer = state.offer!!,
                isUpdating = state.isActionLoading,
                onBackClick = onBackClick,
                onEditClick = { onEditClick(offerId) },
                onDeactivateClick = { showDeactivateDialog = true },
                onActivateClick = { showActivateDialog = true },
                onArchiveClick = { showArchiveDialog = true }
            )
        }
    }
}

@Composable
private fun CompanyOfferDetailContent(
    offer: OfferDto,
    isUpdating: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    onActivateClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    val isArchived = offer.archivedAt != null
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    OfferDetailHeader(
                        title = offer.title,
                        isArchived = isArchived,
                        isActive = offer.isActive
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OfferDetailSectionCard(title = stringResource(R.string.general_information)) {
                        OfferInfoItem(
                            icon = Icons.Default.Business,
                            label = stringResource(R.string.company),
                            value = offer.companyName
                        )
                        OfferInfoItem(
                            icon = Icons.Default.Category,
                            label = stringResource(R.string.area),
                            value = Formatters.formatOfferArea(offer.area)
                        )
                        OfferInfoItem(
                            icon = Icons.Default.LocationOn,
                            label = stringResource(R.string.location),
                            value = offer.location
                        )
                        OfferInfoItem(
                            icon = Icons.Default.Schedule,
                            label = stringResource(R.string.work_mode),
                            value = Formatters.formatWorkMode(offer.workMode)
                        )
                        OfferInfoItem(
                            icon = Icons.Default.DateRange,
                            label = stringResource(R.string.duration),
                            value = formatDuration(offer.duration)
                        )
                        OfferInfoItem(
                            icon = Icons.Default.People,
                            label = stringResource(R.string.vacancies),
                            value = formatVacancies(offer.vacancies)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OfferActions(
                        isArchived = isArchived,
                        isActive = offer.isActive,
                        isUpdating = isUpdating,
                        onEditClick = onEditClick,
                        onDeactivateClick = onDeactivateClick,
                        onActivateClick = onActivateClick,
                        onArchiveClick = onArchiveClick
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    OfferDetailSectionCard(title = stringResource(R.string.description)) {
                        Text(
                            text = offer.description?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.no_description),
                            fontSize = 15.sp,
                            color = if (offer.description?.isNotBlank() == true) Color.Black
                            else Color(0xFF8A8A8A),
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OfferDetailSectionCard(title = stringResource(R.string.requirements)) {
                        Text(
                            text = offer.requirements?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.no_requirements),
                            fontSize = 15.sp,
                            color = if (offer.requirements?.isNotBlank() == true) Color.Black
                            else Color(0xFF8A8A8A),
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                OfferDetailHeader(
                    title = offer.title,
                    isArchived = isArchived,
                    isActive = offer.isActive
                )

                Spacer(modifier = Modifier.height(24.dp))

                OfferDetailSectionCard(title = stringResource(R.string.general_information)) {
                    OfferInfoItem(
                        icon = Icons.Default.Business,
                        label = stringResource(R.string.company),
                        value = offer.companyName
                    )
                    OfferInfoItem(
                        icon = Icons.Default.Category,
                        label = stringResource(R.string.area),
                        value = Formatters.formatOfferArea(offer.area)
                    )
                    OfferInfoItem(
                        icon = Icons.Default.LocationOn,
                        label = stringResource(R.string.location),
                        value = offer.location
                    )
                    OfferInfoItem(
                        icon = Icons.Default.Schedule,
                        label = stringResource(R.string.work_mode),
                        value = Formatters.formatWorkMode(offer.workMode)
                    )
                    OfferInfoItem(
                        icon = Icons.Default.DateRange,
                        label = stringResource(R.string.duration),
                        value = formatDuration(offer.duration)
                    )
                    OfferInfoItem(
                        icon = Icons.Default.People,
                        label = stringResource(R.string.vacancies),
                        value = formatVacancies(offer.vacancies)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OfferDetailSectionCard(title = stringResource(R.string.description)) {
                    Text(
                        text = offer.description?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.no_description),
                        fontSize = 15.sp,
                        color = if (offer.description?.isNotBlank() == true) Color.Black
                        else Color(0xFF8A8A8A),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OfferDetailSectionCard(title = stringResource(R.string.requirements)) {
                    Text(
                        text = offer.requirements?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.no_requirements),
                        fontSize = 15.sp,
                        color = if (offer.requirements?.isNotBlank() == true) Color.Black
                        else Color(0xFF8A8A8A),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OfferActions(
                    isArchived = isArchived,
                    isActive = offer.isActive,
                    isUpdating = isUpdating,
                    onEditClick = onEditClick,
                    onDeactivateClick = onDeactivateClick,
                    onActivateClick = onActivateClick,
                    onArchiveClick = onArchiveClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun OfferDetailHeader(
    title: String,
    isArchived: Boolean,
    isActive: Boolean
) {
    Text(
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(8.dp))

    val (badgeText, bgColor, textColor) = when {
        isArchived -> Triple(
            stringResource(R.string.archived_offer),
            Color(0xFFF3F3F3),
            Color(0xFF8A8A8A)
        )
        isActive -> Triple(
            stringResource(R.string.active_offer),
            Color(0xFFE8F5E9),
            Color(0xFF138A36)
        )
        else -> Triple(
            stringResource(R.string.inactive_offer),
            Color(0xFFFBE9E7),
            Color(0xFFB00020)
        )
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = badgeText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun OfferDetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun OfferInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8A8A8A),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A)
            )

            Text(
                text = value.ifBlank { stringResource(R.string.not_available) },
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun formatDuration(duration: String?): String {
    if (duration.isNullOrBlank()) return stringResource(R.string.not_available)
    val numeric = duration.toIntOrNull()
    return if (numeric != null) {
        if (numeric == 1) stringResource(R.string.duration_month)
        else stringResource(R.string.duration_months, numeric)
    } else {
        duration
    }
}

@Composable
private fun formatVacancies(count: Int): String {
    return if (count == 1) stringResource(R.string.vacancy_count)
    else stringResource(R.string.vacancies_count, count)
}

@Composable
private fun OfferActions(
    isArchived: Boolean,
    isActive: Boolean,
    isUpdating: Boolean,
    onEditClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    onActivateClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    if (isArchived) {
        Text(
            text = stringResource(R.string.archived_offer),
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Button(
            onClick = onEditClick,
            enabled = !isUpdating,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(R.string.edit_offer),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isActive) {
            OutlinedButton(
                onClick = onDeactivateClick,
                enabled = !isUpdating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.deactivate_offer),
                    color = Color(0xFFB00020),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            OutlinedButton(
                onClick = onActivateClick,
                enabled = !isUpdating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.activate_offer),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onArchiveClick,
            enabled = !isUpdating,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = stringResource(R.string.archive_offer),
                color = Color(0xFF8A8A8A),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
