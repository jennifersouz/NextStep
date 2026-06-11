package com.example.nextstep.ui.screens.advisor

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorActivityDto
import com.example.nextstep.data.model.AdvisorAssignedStudentDto
import com.example.nextstep.ui.utils.DateFormatUtils


@Composable
fun AdvisorSectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF555555),
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
fun AdvisorSummaryCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, AdvisorUiColors.BorderGray, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AdvisorUiColors.YellowLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF8D6E00),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = label,
            fontSize = 11.sp,
            color = AdvisorUiColors.TextGray,
            maxLines = 2
        )
    }
}

@Composable
fun AdvisorStudentPreviewCard(
    student: AdvisorAssignedStudentDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp) // Adicionado padding horizontal para alinhar com o header
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, AdvisorUiColors.BorderGray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdvisorStudentAvatar(studentName = student.studentName)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = student.studentName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            student.offerTitle?.takeIf { it.isNotBlank() }?.let { offer ->
                Text(
                    text = offer,
                    fontSize = 13.sp,
                    color = AdvisorUiColors.TextDarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        AdvisorStatusBadge(status = student.status)

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AdvisorUiColors.BorderGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AdvisorStudentAvatar(studentName: String) {
    val initials = studentName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFF2B2B2B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdvisorStatusBadge(status: String?) {
    val (label, bgColor, textColor) = when {
        status == "accepted" || status == "active" -> Triple(
            stringResource(R.string.status_accepted),
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32)
        )
        status == "pending" -> Triple(
            stringResource(R.string.status_pending),
            Color(0xFFFFF8E1),
            Color(0xFFF57F17)
        )
        status == "rejected" -> Triple(
            stringResource(R.string.status_rejected),
            Color(0xFFFFEBEE),
            Color(0xFFC62828)
        )
        else -> Triple(
            status.orEmpty(),
            Color(0xFFF5F5F5),
            AdvisorUiColors.TextGray
        )
    }

    if (label.isBlank()) return

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun AdvisorActivityItem(activity: AdvisorActivityDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Bolinha cinza discreta em vez de amarela (Ponto 6)
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = 6.dp)
                    .clip(CircleShape)
                    .background(AdvisorUiColors.BorderGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = activity.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                activity.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = AdvisorUiColors.TextGray,
                        maxLines = 1, // Reduzido para 1 linha para manter limpo
                        overflow = TextOverflow.Ellipsis
                    )
                }

                activity.createdAt?.takeIf { it.isNotBlank() }?.let { date ->
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = DateFormatUtils.formatDateTimeForUi(date), // Formatação de data (Ponto 1 e 8)
                        fontSize = 11.sp,
                        color = AdvisorUiColors.TextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp)) // Aumentado espaçamento entre itens
    }
}