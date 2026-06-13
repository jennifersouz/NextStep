package com.example.nextstep.ui.screens.institution

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.nextstep.data.model.InstitutionStudentDto
import com.example.nextstep.data.repository.InstitutionRepository

@Composable
fun InstitutionStudentsScreen(
    onStudentClick: (String) -> Unit,
    viewModel: InstitutionStudentsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.students),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        StudentFilters(
            selectedFilter = state.filter,
            onFilterSelected = { viewModel.loadStudents(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                InstitutionStudentsErrorState(
                    message = state.errorMessage!!,
                    onRetryClick = { viewModel.loadStudents() }
                )
            }

            state.students.isEmpty() -> {
                InstitutionStudentsEmptyState(filter = state.filter)
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.students) { student ->
                        InstitutionStudentCard(
                            student = student,
                            onClick = { onStudentClick(student.profileId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentFilters(
    selectedFilter: InstitutionRepository.ArchiveFilter,
    onFilterSelected: (InstitutionRepository.ArchiveFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InstitutionRepository.ArchiveFilter.entries.forEach { filter ->
            val label = when (filter) {
                InstitutionRepository.ArchiveFilter.ACTIVE -> "Ativos"
                InstitutionRepository.ArchiveFilter.ARCHIVED -> "Arquivados"
                InstitutionRepository.ArchiveFilter.ALL -> "Todos"
            }
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFDFA52),
                    selectedLabelColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun InstitutionStudentCard(
    student: InstitutionStudentDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F9F9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color(0xFFEFEFEF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getStudentInitials(student),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${student.firstName} ${student.lastName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = student.email,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )

                Row {
                    if (student.studentNumber.isNotBlank()) {
                        Text(
                            text = "N.º ${student.studentNumber}",
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                    if (student.course.isNotBlank()) {
                        if (student.studentNumber.isNotBlank()) {
                            Text(
                                text = " · ",
                                fontSize = 13.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Text(
                            text = student.course,
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                    if (student.academicYear != null) {
                        Text(
                            text = " · ${student.academicYear}º ano",
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (student.institutionArchivedAt != null) {
                InstitutionStudentStatusBadge(label = "Arquivado", containerColor = Color(0xFFF3F4F6), textColor = Color(0xFF6B7280))
            } else {
                InstitutionStudentStatusBadge(
                    label = if (student.isActive) stringResource(R.string.active_status) else stringResource(R.string.inactive_status),
                    containerColor = if (student.isActive) Color(0xFFE7F7EC) else Color(0xFFFFEBEE),
                    textColor = if (student.isActive) Color(0xFF1B7F3A) else Color(0xFFC62828)
                )
            }
        }
    }
}

@Composable
private fun InstitutionStudentStatusBadge(label: String, containerColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun InstitutionStudentsEmptyState(filter: InstitutionRepository.ArchiveFilter) {
    val message = when (filter) {
        InstitutionRepository.ArchiveFilter.ACTIVE -> stringResource(R.string.no_students_label)
        InstitutionRepository.ArchiveFilter.ARCHIVED -> "Nenhum aluno arquivado."
        InstitutionRepository.ArchiveFilter.ALL -> stringResource(R.string.no_students_label)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            if (filter == InstitutionRepository.ArchiveFilter.ACTIVE) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_students_description),
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun InstitutionStudentsErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                color = Color(0xFFB00020),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(text = stringResource(R.string.try_again))
            }
        }
    }
}

private fun getStudentInitials(student: InstitutionStudentDto): String {
    val first = student.firstName.firstOrNull()?.uppercase() ?: ""
    val last = student.lastName.firstOrNull()?.uppercase() ?: ""
    return if (first.isNotBlank() || last.isNotBlank()) "$first$last" else "?"
}
