package com.example.nextstep.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.ui.utils.Formatters

@Composable
fun InternshipOfferCard(
    offer: OfferDto,
    onClick: () -> Unit,
    onCompanyClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            CompanyLogo(
                companyName = offer.companyName,
                size = 44,
                fontSize = 14
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = offer.companyName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(enabled = onCompanyClick != null) {
                        onCompanyClick?.invoke()
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = offer.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LabelChip(text = offer.location)

                    Text(
                        text = "·",
                        color = Color(0xFFD1D5DB),
                        fontSize = 14.sp
                    )

                    LabelChip(text = Formatters.formatWorkMode(offer.workMode))

                    if (!offer.duration.isNullOrBlank()) {
                        Text(
                            text = "·",
                            color = Color(0xFFD1D5DB),
                            fontSize = 14.sp
                        )

                        LabelChip(text = offer.duration)
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelChip(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color(0xFF6B7280),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun CompanyLogo(
    companyName: String,
    modifier: Modifier = Modifier,
    size: Int = 76,
    fontSize: Int = 18
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
        modifier = modifier
            .size(size.dp)
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
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


