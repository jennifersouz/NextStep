package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.nextstep.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

data class InternshipCardUi(
    val id: String,
    val title: String,
    val companyName: String,
    val advisorName: String?,
    val teacherName: String?,
    val teacherStatus: String?,
    val completed: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInternshipsScreen(
    viewModel: StudentInternshipsViewModel = viewModel(),
    onInternshipClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                LoadingState()
            }

            state.errorMessage != null -> {
                ErrorState(
                    message = state.errorMessage!!,
                    onRetryClick = { viewModel.loadInternships() }
                )
            }

            else -> {
                StudentInternshipsContent(
                    internships = state.internships,
                    onInternshipClick = onInternshipClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInternshipsContent(
    internships: List<InternshipCardUi>,
    onInternshipClick: (String) -> Unit
) {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    val filteredItems = internships.filter {
        if (selectedTab == 0) !it.completed
        else it.completed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.tab_internships),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabButton(
                text = stringResource(R.string.company_pending),
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            )
            TabButton(
                text = stringResource(R.string.completed),
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            items(filteredItems) { internship ->

                InternshipCard(
                    internship = internship,
                    onClick = {
                        onInternshipClick(internship.id)
                    }
                )
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color(0xFFFDFA52) else Color(0xFFF4F4F4),
        modifier = Modifier.height(44.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun InternshipCard(
    internship: InternshipCardUi,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E5E5)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F4F4))
                    .border(1.dp, Color(0xFFE5E5E5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = internship.companyName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = internship.companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A)
                )

                Text(
                    text = internship.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.supervision_section_title),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                val advisorDisplay = if (!internship.advisorName.isNullOrBlank()) {
                    "Orientador: ${internship.advisorName}"
                } else {
                    stringResource(R.string.advisor_not_assigned_fallback)
                }

                Text(
                    text = advisorDisplay,
                    fontSize = 13.sp,
                    color = Color(0xFF4A4A4A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val isTeacherAccepted = internship.teacherStatus == "accepted"
                val teacherDisplay = if (isTeacherAccepted && !internship.teacherName.isNullOrBlank()) {
                    "Docente: ${internship.teacherName}"
                } else {
                    stringResource(R.string.teacher_not_assigned_fallback)
                }

                Text(
                    text = teacherDisplay,
                    fontSize = 13.sp,
                    color = Color(0xFF4A4A4A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStudentInternships() {

    StudentInternshipsContent(
        internships = listOf(
            InternshipCardUi(
                "1",
                "Sistemas Interativos",
                "Worten",
                "Catarina Ferreira",
                "Prof. Ana Silva",
                "accepted",
                false
            ),
            InternshipCardUi(
                "2",
                "Sistemas Web",
                "Tech World",
                "Catarina Ferreira",
                null,
                null,
                false
            ),
            InternshipCardUi(
                "3",
                "Programação Móvel",
                "Worten",
                null,
                "Prof. Ana Silva",
                "accepted",
                true
            )
        ),
        onInternshipClick = {}
    )
}
