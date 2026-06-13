package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorTaskListItemDto

@Composable
fun AdvisorTasksScreen(
    onTaskClick: (AdvisorTaskListItemDto) -> Unit = {},
    onNewTaskClick: () -> Unit = {},
    viewModel: AdvisorTasksViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredTasks = remember(state.tasks, state.selectedFilter) {
        viewModel.getFilteredTasks()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.tasks),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
            )

            Text(
                text = stringResource(R.string.tasks_subtitle),
                fontSize = 14.sp,
                color = AdvisorUiColors.TextGray,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Cards
            TaskSummarySection(state.tasks)

            Spacer(modifier = Modifier.height(24.dp))

            // Filter Chips
            AdvisorTaskFilterChips(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.onFilterSelected(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                state.errorMessage != null && state.tasks.isEmpty() -> {
                    AdvisorTaskEmptyContent(title = state.errorMessage ?: "")
                }

                state.tasks.isEmpty() -> {
                    AdvisorTaskEmptyContent(
                        title = stringResource(R.string.no_tasks),
                        subtitle = stringResource(R.string.no_tasks_description)
                    )
                }

                else -> {
                    AdvisorTasksList(
                        tasks = filteredTasks,
                        onTaskClick = onTaskClick,
                        onStatusChange = { taskId, status -> viewModel.updateTaskStatus(taskId, status) },
                        showStudentInfo = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // FAB for New Task
        FloatingActionButton(
            onClick = onNewTaskClick,
            containerColor = AdvisorUiColors.YellowAccent,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_task))
        }
    }
}

@Composable
private fun TaskSummarySection(tasks: List<AdvisorTaskListItemDto>) {
    val total = tasks.size
    val pending = tasks.count { it.status == "pending" }
    val inProgress = tasks.count { it.status == "in_progress" }
    val completed = tasks.count { it.status == "completed" }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SummaryCard(label = stringResource(R.string.total), count = total, color = Color.Black)
        }
        item {
            SummaryCard(label = stringResource(R.string.status_pending), count = pending, color = AdvisorUiColors.PendingText)
        }
        item {
            SummaryCard(label = stringResource(R.string.status_in_progress), count = inProgress, color = AdvisorUiColors.InProgressText)
        }
        item {
            SummaryCard(label = stringResource(R.string.status_completed), count = completed, color = AdvisorUiColors.CompletedText)
        }
    }
}

@Composable
private fun SummaryCard(label: String, count: Int, color: Color) {
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AdvisorUiColors.BorderGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 11.sp, color = AdvisorUiColors.TextGray)
        }
    }
}