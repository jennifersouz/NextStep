package com.example.nextstep.ui.screens.advisor

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.nextstep.data.model.AdvisorTaskListItemDto

@Composable
fun AdvisorTasksScreen(
    onTaskClick: (String) -> Unit = {},
    viewModel: AdvisorTasksViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredTasks = remember(state.tasks, state.selectedFilter) {
        viewModel.getFilteredTasks()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Text(
            text = stringResource(R.string.tasks),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AdvisorTaskFilterChips(
            selectedFilter = state.selectedFilter,
            onFilterSelected = { viewModel.onFilterSelected(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessage != null -> {
                AdvisorTaskEmptyContent(
                    text = state.errorMessage ?: ""
                )
            }

            filteredTasks.isEmpty() && state.tasks.isEmpty() -> {
                AdvisorTaskEmptyContent(
                    text = stringResource(R.string.no_tasks)
                )
            }

            filteredTasks.isEmpty() -> {
                AdvisorTaskEmptyContent(
                    text = stringResource(R.string.no_tasks)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredTasks,
                        key = { it.id }
                    ) { task ->
                        AdvisorTaskListItem(
                            task = task,
                            onClick = { onTaskClick(task.applicationId) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvisorTaskFilterChips(
    selectedFilter: AdvisorTaskFilter,
    onFilterSelected: (AdvisorTaskFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AdvisorTaskFilter.entries.forEach { filter ->
            val label = when (filter) {
                AdvisorTaskFilter.ALL -> stringResource(R.string.all)
                AdvisorTaskFilter.PENDING -> stringResource(R.string.pending)
                AdvisorTaskFilter.COMPLETED -> stringResource(R.string.completed)
            }
            val isSelected = filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFF2B2B2B) else Color(0xFFF5F5F5))
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
private fun AdvisorTaskListItem(
    task: AdvisorTaskListItemDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isDone = task.status.lowercase() in listOf("completed", "done", "concluido", "concluída")

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDone) Color(0xFF2E7D32) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Text(text = "✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(text = "!", color = Color(0xFF999999), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = task.studentName,
                fontSize = 13.sp,
                color = AdvisorUiColors.TextDarkGray,
                modifier = Modifier.padding(top = 2.dp)
            )

            task.offerTitle?.takeIf { it.isNotBlank() }?.let { offer ->
                Text(
                    text = offer,
                    fontSize = 12.sp,
                    color = AdvisorUiColors.TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            task.dueDate?.takeIf { it.isNotBlank() }?.let { date ->
                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = AdvisorUiColors.TextGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AdvisorUiColors.BorderGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AdvisorTaskEmptyContent(text: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 15.sp, color = AdvisorUiColors.TextGray)
    }
}