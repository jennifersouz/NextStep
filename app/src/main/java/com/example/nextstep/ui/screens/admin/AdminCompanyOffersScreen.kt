package com.example.nextstep.ui.screens.admin

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyOfferDto
import com.example.nextstep.ui.utils.Formatters

@Composable
fun AdminCompanyOffersScreen(
    companyProfileId: String,
    companyName: String,
    onBackClick: () -> Unit,
    onOfferClick: ((String) -> Unit)? = null,
    viewModel: AdminCompanyOffersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(companyProfileId) {
        viewModel.loadOffers(companyProfileId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.company_offers_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (companyName.isNotBlank()) {
                    Text(
                        text = companyName,
                        fontSize = 13.sp,
                        color = Color(0xFF777777),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.Black)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.loading),
                            fontSize = 14.sp,
                            color = Color(0xFF777777)
                        )
                    }
                }
            }

            state.errorMessageRes != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "😕",
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(state.errorMessageRes!!),
                            color = Color(0xFFB00020),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            state.offers.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📋",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.admin_company_no_offers),
                            fontSize = 15.sp,
                            color = Color(0xFF777777)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        start = 24.dp, end = 24.dp, top = 12.dp, bottom = 96.dp
                    )
                ) {
                    items(state.offers, key = { it.id }) { offer ->
                        AdminOfferCard(
                            offer = offer,
                            onClick = if (onOfferClick != null) {
                                { onOfferClick(offer.id) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOfferCard(
    offer: AdminCompanyOfferDto,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = offer.title ?: stringResource(R.string.offer_no_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                StatusBadge(
                    isActive = offer.isActive ?: false
                )
            }

            val meta = listOfNotNull(
                offer.area?.let { Formatters.formatOfferArea(it) },
                offer.location,
                offer.workMode?.let { Formatters.formatWorkMode(it) }
            ).joinToString(" · ")
            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val durationText = offer.duration?.let { raw ->
                    val numeric = raw.toIntOrNull()
                    if (numeric != null) {
                        stringResource(R.string.duration_months, numeric)
                    } else {
                        stringResource(R.string.duration_with_value, raw)
                    }
                }
                if (durationText != null) {
                    InfoChip(text = durationText)
                }

                val vacanciesText = offer.vacancies?.let { count ->
                    if (count == 1) {
                        stringResource(R.string.vacancy_count, count)
                    } else {
                        stringResource(R.string.offer_vacancies_value, count)
                    }
                }
                if (vacanciesText != null) {
                    InfoChip(text = vacanciesText)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    val (label, fg, bg) = if (isActive) {
        Triple(stringResource(R.string.badge_active), Color(0xFF2E7D32), Color(0xFFE8F5E9))
    } else {
        Triple(stringResource(R.string.badge_inactive), Color(0xFFC62828), Color(0xFFFFEBEE))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = fg,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}
