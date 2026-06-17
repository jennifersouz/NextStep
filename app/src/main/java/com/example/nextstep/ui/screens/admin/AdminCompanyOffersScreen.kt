package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyOfferDto
import com.example.nextstep.ui.utils.Formatters

@Composable
fun AdminCompanyOffersScreen(
    companyProfileId: String,
    companyName: String,
    onBackClick: () -> Unit,
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
        // Header
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
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = Color(0xFFB00020),
                        fontSize = 15.sp
                    )
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
                            text = stringResource(R.string.admin_company_no_offers),
                            fontSize = 15.sp,
                            color = Color(0xFF777777)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.offers) { offer ->
                        AdminOfferListItem(offer = offer)
                    }
                    item { Spacer(modifier = Modifier.height(96.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AdminOfferListItem(offer: AdminCompanyOfferDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Title row + active badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = offer.title ?: stringResource(R.string.offer_no_title),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            val (label, fg, bg) = if (offer.isActive == true) {
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
                Text(text = label, fontSize = 11.sp, color = fg, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Meta row: area · location · work_mode
        val meta = listOfNotNull(
            offer.area?.let { Formatters.formatOfferArea(it) },
            offer.location,
            offer.workMode?.let { Formatters.formatWorkMode(it) }
        ).joinToString(" · ")
        if (meta.isNotBlank()) {
            Text(
                text = meta,
                fontSize = 13.sp,
                color = Color(0xFF777777),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duration + vacancies
        val secondary = listOfNotNull(
            offer.duration?.let { stringResource(R.string.duration_with_value, it) },
            offer.vacancies?.let { stringResource(R.string.offer_vacancies_value, it) }
        ).joinToString("  ·  ")
        if (secondary.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = secondary,
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFF0F0F0))
        )
    }
}
