package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyProfileDto
import com.example.nextstep.data.model.OfferDto

@Composable
fun CompanyProfileScreen(
    onOfferClick: (String) -> Unit = {},
    viewModel: CompanyProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadCompanyProfile()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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

        state.errorMessageRes != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        state.company != null -> {
            CompanyProfileContent(
                company = state.company!!,
                offers = state.offers,
                onOfferClick = onOfferClick
            )
        }
    }
}

@Composable
private fun CompanyProfileContent(
    company: CompanyProfileDto,
    offers: List<OfferDto>,
    onOfferClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = 28.dp,
            end = 28.dp,
            top = 30.dp,
            bottom = 110.dp
        )
    ) {
        item {
            CompanyProfileHeader(company = company)

            Spacer(modifier = Modifier.height(28.dp))

            CompanyProfileSectionTitle(
                title = stringResource(R.string.company_profile_about)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = company.description.orEmpty()
                    .ifBlank { stringResource(R.string.company_profile_no_description) },
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            CompanyProfileSectionTitle(
                title = stringResource(R.string.location)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = company.location.orEmpty()
                    .ifBlank { stringResource(R.string.not_available) },
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(26.dp))

            CompanyProfileSectionTitle(
                title = stringResource(R.string.contacts)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CompanyContactRow(
                phone = company.phone.orEmpty()
                    .ifBlank { stringResource(R.string.not_available) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            CompanyProfileSectionTitle(
                title = stringResource(R.string.internships)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (offers.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.company_profile_no_offers),
                    color = Color(0xFF8A8A8A),
                    fontSize = 14.sp
                )
            }
        } else {
            items(
                items = offers,
                key = { offer -> offer.id }
            ) { offer ->
                CompanyProfileOfferCard(
                    company = company,
                    offer = offer,
                    onClick = {
                        onOfferClick(offer.id)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CompanyProfileHeader(
    company: CompanyProfileDto
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompanyProfileLogo(
            companyName = company.companyName,
            size = 116
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = company.companyName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        if (!company.businessArea.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = company.businessArea,
                fontSize = 13.sp,
                color = Color(0xFF8A8A8A),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompanyProfileLogo(
    companyName: String,
    size: Int
) {
    val initials = companyName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFFE8392A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = if (size >= 100) 28.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CompanyProfileSectionTitle(
    title: String
) {
    Text(
        text = title,
        color = Color(0xFF8A8A8A),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun CompanyContactRow(
    phone: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Phone,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = phone,
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun CompanyProfileOfferCard(
    company: CompanyProfileDto,
    offer: OfferDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFE1E1E1),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompanyProfileLogo(
            companyName = company.companyName,
            size = 42
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = company.companyName,
                color = Color(0xFF8A8A8A),
                fontSize = 12.sp
            )

            Text(
                text = offer.title,
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(15.dp)
                )

                Text(
                    text = offer.location,
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}