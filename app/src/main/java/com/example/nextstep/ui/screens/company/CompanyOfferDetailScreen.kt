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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.OfferDto

@Composable
fun CompanyOfferDetailScreen(
    offerId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    offerUpdated: Boolean = false,
    viewModel: CompanyOfferDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Reload offer when returning from edit
    LaunchedEffect(offerUpdated) {
        if (offerUpdated) {
            viewModel.loadOffer(offerId)
        }
    }

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    // States for dialogs
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showActivateDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    // Deactivate confirmation dialog
    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = {
                Text(text = stringResource(R.string.deactivate_offer))
            },
            text = {
                Text(text = stringResource(R.string.deactivate_offer_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeactivateDialog = false
                        viewModel.deactivateOffer(offerId = offerId, onSuccess = onBackClick)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.deactivate),
                        color = Color(0xFFB00020)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    // Activate confirmation dialog
    if (showActivateDialog) {
        AlertDialog(
            onDismissRequest = { showActivateDialog = false },
            title = {
                Text(text = stringResource(R.string.activate_offer))
            },
            text = {
                Text(text = stringResource(R.string.activate_offer_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showActivateDialog = false
                        viewModel.activateOffer(offerId = offerId)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.activate),
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showActivateDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    // Archive confirmation dialog
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = {
                Text(text = stringResource(R.string.archive_offer))
            },
            text = {
                Text(text = stringResource(R.string.archive_offer_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showArchiveDialog = false
                        viewModel.archiveOffer(offerId = offerId, reason = null)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.archive),
                        color = Color(0xFFB00020)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    // Success snackbar
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
    val isActive = offer.isActive && !isArchived

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
                text = offer.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status badge
            when {
                isArchived -> {
                    OfferDetailBadge(
                        text = "Arquivada",
                        bgColor = Color(0xFFF3F3F3),
                        textColor = Color(0xFF8A8A8A)
                    )
                }
                offer.isActive -> {
                    OfferDetailBadge(
                        text = stringResource(R.string.offer_active),
                        bgColor = Color(0xFFE8F5E9),
                        textColor = Color(0xFF138A36)
                    )
                }
                else -> {
                    OfferDetailBadge(
                        text = stringResource(R.string.offer_inactive),
                        bgColor = Color(0xFFFBE9E7),
                        textColor = Color(0xFFB00020)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            CompanyOfferInfo(title = stringResource(R.string.company), value = offer.companyName)
            CompanyOfferInfo(title = stringResource(R.string.area), value = offer.area)
            CompanyOfferInfo(title = stringResource(R.string.location), value = offer.location)
            CompanyOfferInfo(title = stringResource(R.string.work_mode), value = offer.workMode)
            CompanyOfferInfo(title = stringResource(R.string.duration), value = offer.duration)
            CompanyOfferInfo(title = stringResource(R.string.vacancies), value = offer.vacancies.toString())

            Spacer(modifier = Modifier.height(18.dp))

            CompanyOfferInfo(
                title = stringResource(R.string.description),
                value = offer.description
            )

            Spacer(modifier = Modifier.height(18.dp))

            CompanyOfferInfo(
                title = stringResource(R.string.requirements),
                value = offer.requirements
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isArchived) {
                // Archived offer - show info text
                Text(
                    text = "Esta oferta foi removida da lista principal. O histórico foi mantido.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Non-archived offer - show action buttons
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

                if (offer.isActive) {
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

                // Archive button (only for non-archived)
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

            Spacer(modifier = Modifier.height(42.dp))
        }
    }
}

@Composable
private fun OfferDetailBadge(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun CompanyOfferInfo(
    title: String,
    value: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color(0xFF8A8A8A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value.orEmpty().ifBlank {
                stringResource(R.string.not_available)
            },
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}