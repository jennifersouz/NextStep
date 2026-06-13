package com.example.nextstep.ui.screens.company

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyProfileDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.ui.components.CompanyLogo
import com.example.nextstep.ui.components.InternshipOfferCard

@Composable
fun CompanyProfileScreen(
    companyProfileId: String,
    onBackClick: () -> Unit,
    onOfferClick: (String) -> Unit,
    viewModel: CompanyProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Log.d("CompanyProfileDebug", "ID recebido na Screen: $companyProfileId")

    LaunchedEffect(companyProfileId) {
        viewModel.loadCompanyProfile(companyProfileId)
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
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessageRes != null -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(state.errorMessageRes!!),
                        color = Color(0xFFB00020),
                        textAlign = TextAlign.Center
                    )
                }
            }

            state.company != null -> {
                CompanyProfileScrollContent(
                    company = state.company!!,
                    offers = state.offers,
                    onOfferClick = onOfferClick
                )
            }
        }
    }
}

@Composable
private fun CompanyProfileScrollContent(
    company: CompanyProfileDto,
    offers: List<OfferDto>,
    onOfferClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompanyLogo(
                companyName = company.companyName,
                size = 140,
                fontSize = 32
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = company.companyName.lowercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        company.description?.let {
            Text(
                text = it,
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        CompanyInfoSection(
            title = stringResource(R.string.location),
            content = {
                Text(
                    text = company.location ?: "-",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        CompanyInfoSection(
            title = stringResource(R.string.contacts),
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = company.phone ?: "-",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.tab_internships),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB3B3B3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (offers.isEmpty()) {
            Text(
                text = stringResource(R.string.no_offers_title),
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            offers.forEach { offer ->
                InternshipOfferCard(
                    offer = offer,
                    onClick = { onOfferClick(offer.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CompanyInfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB3B3B3)
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}
