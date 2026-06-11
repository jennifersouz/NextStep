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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.nextstep.data.model.AdvisorAssignedStudentDto

private val YellowBadge = Color(0xFFFFF9C4)
private val YellowDark = Color(0xFF8D6E00)

@Composable
fun AdvisorStudentsScreen(
    onStudentClick: (String) -> Unit = {},
    viewModel: AdvisorStudentsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredStudents = remember(state.students, state.selectedFilter, state.searchQuery) {
        viewModel.getFilteredStudents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title
        Text(
            text = stringResource(R.string.my_students),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search field
        SearchBar(
            query = state.searchQuery,
            onQueryChange = { viewModel.onSearchChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        FilterChipsRow(
            selectedFilter = state.selectedFilter,
            onFilterSelected = { viewModel.onFilterSelected(it) }
        )

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
                AdvisorStudentsErrorContent(
                    message = state.errorMessage ?: "",
                    onRetry = { viewModel.loadStudents() }
                )
            }

            filteredStudents.isEmpty() && state.students.isEmpty() -> {
                AdvisorStudentsEmptyContent(
                    text = stringResource(R.string.no_assigned_students)
                )
            }

            filteredStudents.isEmpty() -> {
                AdvisorStudentsEmptyContent(
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
                        AdvisorStudentCard(
                            student = student,
                            onClick = { onStudentClick(student.applicationId) }
                        )
                    }

                    // Help card at bottom
                    item {
                        AdvisorHelpCard()
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = AdvisorUiColors.TextGray,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = AdvisorUiColors.TextGray,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = AdvisorUiColors.TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFCCCCCC),
            unfocusedBorderColor = AdvisorUiColors.BorderGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun FilterChipsRow(
    selectedFilter: AdvisorStudentsFilter,
    onFilterSelected: (AdvisorStudentsFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AdvisorStudentsFilter.entries.forEach { filter ->
            val label = when (filter) {
                AdvisorStudentsFilter.ALL -> stringResource(R.string.all)
                AdvisorStudentsFilter.ACTIVE -> stringResource(R.string.active)
                AdvisorStudentsFilter.TO_COMPLETE -> stringResource(R.string.to_complete)
                AdvisorStudentsFilter.COMPLETED -> stringResource(R.string.completed)
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
private fun AdvisorStudentCard(
    student: AdvisorAssignedStudentDto,
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
        AdvisorStudentCardAvatar(studentName = student.studentName)

        Spacer(modifier = Modifier.width(12.dp))

        // Info
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
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Status badge
        AdvisorStudentStatusBadge(status = student.status)

        Spacer(modifier = Modifier.width(4.dp))

        // Chevron
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AdvisorUiColors.BorderGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AdvisorStudentCardAvatar(studentName: String) {
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
private fun AdvisorStudentStatusBadge(status: String?) {
    val (label, bgColor, textColor) = AdvisorStudentStatusStyle(status)

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

private fun AdvisorStudentStatusStyle(status: String?): Triple<String, Color, Color> {
    return when {
        status == "accepted" || status == "active" || status == "ativo" || status == "aceite" -> Triple(
            "Ativo", Color(0xFFE8F5E9), Color(0xFF2E7D32)
        )
        status == "to_complete" || status == "por_concluir" || status == "por concluir" -> Triple(
            "Por concluir", Color(0xFFFFF8E1), Color(0xFFF57F17)
        )
        status == "completed" || status == "concluido" || status == "concluído" -> Triple(
            "Concluído", Color(0xFFE3F2FD), Color(0xFF1565C0)
        )
        status == "pending" || status == "pendente" -> Triple(
            "Pendente", YellowBadge, YellowDark
        )
        status == "rejected" || status == "recusada" || status == "recusado" -> Triple(
            "Recusado", Color(0xFFFFEBEE), Color(0xFFC62828)
        )
        else -> Triple(
            status?.replaceFirstChar { it.uppercase() } ?: "",
            Color(0xFFF5F5F5), AdvisorUiColors.TextGray
        )
    }
}

@Composable
private fun AdvisorHelpCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF9C4))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            tint = YellowDark,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.need_help),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037)
            )

            Text(
                text = stringResource(R.string.advisor_help_text),
                fontSize = 12.sp,
                color = Color(0xFF8D6E00)
            )
        }
    }
}

@Composable
private fun AdvisorStudentsEmptyContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = AdvisorUiColors.TextGray
        )
    }
}

@Composable
private fun AdvisorStudentsErrorContent(
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