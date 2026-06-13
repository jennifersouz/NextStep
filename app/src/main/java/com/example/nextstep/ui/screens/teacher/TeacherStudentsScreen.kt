package com.example.nextstep.ui.screens.teacher

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.nextstep.data.model.TeacherStudentDto

@Composable
fun TeacherStudentsScreen(
    onStudentClick: (TeacherStudentDto) -> Unit = {},
    viewModel: TeacherStudentsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredStudents = remember(state.students, state.selectedFilter, state.searchQuery) {
        viewModel.getFilteredStudents()
    }

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title
        Text(
            text = stringResource(R.string.students),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search field
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_student_placeholder),
                    color = Color(0xFF777777),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = Color(0xFF777777),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCCCCCC),
                unfocusedBorderColor = Color(0xFFEDEDED),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val filters = TeacherStudentsFilter.entries
            items(filters) { filter ->
                val label = when (filter) {
                    TeacherStudentsFilter.ALL -> stringResource(R.string.all)
                    TeacherStudentsFilter.ACTIVE -> stringResource(R.string.active)
                    TeacherStudentsFilter.TO_EVALUATE -> stringResource(R.string.to_evaluate)
                    TeacherStudentsFilter.COMPLETED -> stringResource(R.string.completed)
                }
                val isSelected = filter == state.selectedFilter

                Surface(
                    color = if (isSelected) Color(0xFF2B2B2B) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { viewModel.onFilterSelected(filter) }
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF333333)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
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
                TeacherStudentsErrorContent(
                    message = state.errorMessage ?: "",
                    onRetry = { viewModel.loadStudents() }
                )
            }

            filteredStudents.isEmpty() && state.students.isEmpty() -> {
                TeacherStudentsEmptyContent(
                    text = stringResource(R.string.no_students_yet)
                )
            }

            filteredStudents.isEmpty() -> {
                TeacherStudentsEmptyContent(
                    text = stringResource(R.string.no_students_found)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredStudents,
                        key = { it.applicationId }
                    ) { student ->
                        TeacherStudentCard(
                            student = student,
                            onClick = {
                                Log.d("TeacherStudentsScreen", "Opening detail applicationId=${student.applicationId}, offer=${student.offerTitle}")
                                onStudentClick(student)
                            }
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
private fun TeacherStudentCard(
    student: TeacherStudentDto,
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
        // Avatar
        TeacherStudentCardAvatar(studentName = student.studentName)

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = student.studentName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                // Evaluation status badge
                if (student.hasPendingEvaluation) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFF9C4))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.to_evaluate),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8D6E00)
                        )
                    }
                }
            }

            student.offerTitle?.takeIf { it.isNotBlank() }?.let { offer ->
                Text(
                    text = offer,
                    fontSize = 13.sp,
                    color = Color(0xFF555555),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            student.companyName?.takeIf { it.isNotBlank() }?.let { company ->
                Text(
                    text = company,
                    fontSize = 12.sp,
                    color = Color(0xFF777777),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Status badge
        TeacherStudentStatusBadge(status = student.status)

        Spacer(modifier = Modifier.width(4.dp))

        // Chevron
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFEDEDED),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TeacherStudentCardAvatar(studentName: String) {
    val initials = studentName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF2B2B2B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TeacherStudentStatusBadge(status: String?) {
    val (labelRes, bgColor, textColor) = when {
        status == "accepted" || status == "active" || status == "ativo" || status == "aceite" -> Triple(
            R.string.teacher_status_active, Color(0xFFE8F5E9), Color(0xFF2E7D32)
        )
        status == "in_progress" || status == "em_curso" || status == "em curso" -> Triple(
            R.string.teacher_status_in_progress, Color(0xFFE3F2FD), Color(0xFF1565C0)
        )
        status == "completed" || status == "concluido" || status == "concluído" -> Triple(
            R.string.teacher_status_completed, Color(0xFFE8F5E9), Color(0xFF2E7D32)
        )
        status == "pending" || status == "pendente" -> Triple(
            R.string.teacher_status_pending, Color(0xFFFFF9C4), Color(0xFF8D6E00)
        )
        else -> Triple(
            null, Color(0xFFF5F5F5), Color(0xFF777777)
        )
    }

    val label = if (labelRes != null) {
        stringResource(labelRes)
    } else {
        status?.replaceFirstChar { it.uppercase() } ?: ""
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
private fun TeacherStudentsEmptyContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color(0xFF777777)
        )
    }
}

@Composable
private fun TeacherStudentsErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = Color(0xFFB00020),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.try_again),
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}
