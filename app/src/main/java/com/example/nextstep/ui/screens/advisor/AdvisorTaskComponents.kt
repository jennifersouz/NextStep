package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.ui.components.AppFilterChipsRow
import com.example.nextstep.ui.utils.DateFormatUtils
import com.example.nextstep.ui.utils.localizedPriority

@Composable
fun AdvisorTasksList(
    tasks: List<AdvisorTaskListItemDto>,
    onTaskClick: (AdvisorTaskListItemDto) -> Unit,
    onStatusChange: (String, String) -> Unit,
    showStudentInfo: Boolean,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        AdvisorTaskEmptyContent(
            title = stringResource(R.string.no_tasks),
            subtitle = stringResource(R.string.no_tasks_description),
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                AdvisorTaskListItem(
                    task = task,
                    onClick = { onTaskClick(task) },
                    onStatusChange = { nextStatus -> onStatusChange(task.id, nextStatus) },
                    showStudentInfo = showStudentInfo
                )
            }
        }
    }
}

@Composable
fun AdvisorTaskListItem(
    task: AdvisorTaskListItemDto,
    onClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    showStudentInfo: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AdvisorUiColors.BorderGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (statusIcon, statusColor) = when (task.status) {
                "completed" -> Icons.Default.CheckCircle to AdvisorUiColors.CompletedText
                "in_progress" -> Icons.Default.Schedule to AdvisorUiColors.InProgressText
                else -> Icons.Default.RadioButtonUnchecked to AdvisorUiColors.TextGray
            }

            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val nextStatus = when (task.status) {
                            "pending" -> "in_progress"
                            "in_progress" -> "completed"
                            else -> "pending"
                        }
                        onStatusChange(nextStatus)
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                if (showStudentInfo) {
                    val displayStudentName = task.studentName?.takeIf { it.isNotBlank() }
                        ?: "Aluno não identificado"
                    Text(
                        text = "Aluno: $displayStudentName",
                        fontSize = 13.sp,
                        color = AdvisorUiColors.TextDarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Estágio: ${task.offerTitle ?: "—"}",
                        fontSize = 13.sp,
                        color = AdvisorUiColors.TextDarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    task.dueDate?.takeIf { it.isNotBlank() }?.let { date ->
                        Text(
                            text = DateFormatUtils.formatDateForUi(date),
                            fontSize = 11.sp,
                            color = AdvisorUiColors.TextGray
                        )
                    }
                    
                    PriorityBadge(task.priority ?: "medium")
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AdvisorUiColors.BorderGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "high" -> AdvisorUiColors.PriorityHighBg to AdvisorUiColors.PriorityHighText
        "low" -> AdvisorUiColors.PriorityLowBg to AdvisorUiColors.PriorityLowText
        else -> AdvisorUiColors.PriorityMediumBg to AdvisorUiColors.PriorityMediumText
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = localizedPriority(priority), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

@Composable
fun AdvisorTaskFilterChips(
    selectedFilter: AdvisorTaskFilter,
    onFilterSelected: (AdvisorTaskFilter) -> Unit
) {
    AppFilterChipsRow(
        filters = AdvisorTaskFilter.entries,
        selectedFilter = selectedFilter,
        labelProvider = { filter ->
            when (filter) {
                AdvisorTaskFilter.ALL -> stringResource(R.string.all)
                AdvisorTaskFilter.PENDING -> stringResource(R.string.status_pending)
                AdvisorTaskFilter.IN_PROGRESS -> stringResource(R.string.status_in_progress)
                AdvisorTaskFilter.COMPLETED -> stringResource(R.string.status_completed)
            }
        },
        onFilterSelected = { onFilterSelected(it) }
    )
}

@Composable
fun AdvisorTaskEmptyContent(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = AdvisorUiColors.TextGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
