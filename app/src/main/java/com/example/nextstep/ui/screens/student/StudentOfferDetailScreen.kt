package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.OfferDto

@Composable
fun StudentOfferDetailScreen(
    offerId: String,
    onBackClick: () -> Unit,
    onApplyClick: (String) -> Unit,
    viewModel: StudentOfferDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading -> {
                    OfferDetailLoadingState()
                }

                state.errorMessageRes != null -> {
                    val errorRes = state.errorMessageRes

                    if (errorRes != null) {
                        OfferDetailErrorState(
                            message = stringResource(errorRes),
                            onBackClick = onBackClick
                        )
                    }
                }

                state.offer != null -> {
                    val offer = state.offer

                    if (offer != null) {
                        OfferDetailContent(
                            offer = offer,
                            onBackClick = onBackClick
                        )
                    }
                }
            }
        }

        state.offer?.let { offer ->
            Button(
                onClick = {
                    if (!state.hasApplied) {
                        onApplyClick(offer.id)
                    }
                },
                enabled = !state.hasApplied && !state.isCheckingApplication,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 14.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFFE5E5A0),
                    disabledContentColor = Color.Black
                )
            ) {
                Text(
                    text = when {
                        state.isCheckingApplication -> stringResource(R.string.checking_application)
                        state.hasApplied -> stringResource(R.string.already_applied_button)
                        else -> stringResource(R.string.apply_button)
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        StudentBottomBar(
            currentRoute = StudentBottomRoutes.HOME,
            onItemClick = {
                // Depois ligamos a navegação real da bottom bar.
            }
        )
    }
}

@Composable
fun OfferDetailContent(
    offer: OfferDto,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OfferDetailCompanyLogo(
                companyName = offer.companyName
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = offer.companyName,
                    fontSize = 18.sp,
                    color = Color(0xFF8A8A8A)
                )

                Text(
                    text = offer.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 30.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = stringResource(R.string.save_offer),
                tint = Color.Black,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(44.dp))

        OfferDetailSectionTitle(
            text = stringResource(R.string.offer_location)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = offer.location,
                fontSize = 17.sp,
                color = Color.Black,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        offer.description
            ?.takeIf { it.isNotBlank() }
            ?.let {
                OfferDetailSection(
                    title = stringResource(R.string.offer_description),
                    value = it
                )
            }

        val areaValue = offer.area
            ?.takeIf { it.isNotBlank() }
            ?.let { offerAreaLabel(it) }

        val workModeValue = offer.workMode
            ?.takeIf { it.isNotBlank() }
            ?.let { workModeLabel(it) }

        if (areaValue != null || workModeValue != null) {
            OfferDetailTwoColumnRow(
                leftTitle = stringResource(R.string.offer_area),
                leftValue = areaValue ?: "-",
                rightTitle = stringResource(R.string.offer_work_mode),
                rightValue = workModeValue ?: "-"
            )
        }

        val durationValue = offer.duration
            ?.takeIf { it.isNotBlank() }

        OfferDetailTwoColumnRow(
            leftTitle = stringResource(R.string.offer_duration),
            leftValue = durationValue ?: "-",
            rightTitle = stringResource(R.string.offer_vacancies),
            rightValue = stringResource(R.string.offer_vacancies_value, offer.vacancies)
        )

        offer.requirements
            ?.takeIf { it.isNotBlank() }
            ?.let {
                OfferDetailSection(
                    title = stringResource(R.string.offer_requirements),
                    value = it
                )
            }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun OfferDetailSection(
    title: String,
    value: String
) {
    OfferDetailSectionTitle(text = title)

    Text(
        text = value,
        fontSize = 17.sp,
        color = Color.Black,
        lineHeight = 26.sp
    )

    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
fun OfferDetailSectionTitle(
    text: String
) {
    Text(
        text = text,
        fontSize = 17.sp,
        color = Color(0xFF8A8A8A),
        fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun OfferDetailTwoColumnRow(
    leftTitle: String,
    leftValue: String,
    rightTitle: String,
    rightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        OfferDetailInfoItem(
            title = leftTitle,
            value = leftValue,
            modifier = Modifier.weight(1f)
        )

        OfferDetailInfoItem(
            title = rightTitle,
            value = rightValue,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
fun OfferDetailInfoItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            color = Color(0xFF8A8A8A),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = value,
            fontSize = 17.sp,
            color = Color.Black,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun OfferDetailCompanyLogo(
    companyName: String
) {
    val initials = companyName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFFFDFA52))
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OfferDetailLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Black
        )
    }
}

@Composable
fun OfferDetailErrorState(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
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
        }

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFFB00020),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun offerAreaLabel(area: String): String {
    return when (area) {
        "mobile" -> stringResource(R.string.offer_area_mobile)
        "web" -> stringResource(R.string.offer_area_web)
        "ai" -> stringResource(R.string.offer_area_ai)
        "cybersecurity" -> stringResource(R.string.offer_area_cybersecurity)
        "data" -> stringResource(R.string.offer_area_data)
        "design" -> stringResource(R.string.offer_area_design)
        "management" -> stringResource(R.string.offer_area_management)
        else -> stringResource(R.string.offer_area_other)
    }
}

@Composable
fun workModeLabel(workMode: String): String {
    return when (workMode) {
        "remote" -> stringResource(R.string.work_mode_remote)
        "onsite" -> stringResource(R.string.work_mode_onsite)
        "hybrid" -> stringResource(R.string.work_mode_hybrid)
        else -> workMode
    }
}