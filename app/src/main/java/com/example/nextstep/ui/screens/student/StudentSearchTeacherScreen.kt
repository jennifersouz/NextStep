package com.example.nextstep.ui.screens.student

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun StudentSearchTeacherScreen(
    internshipId: String,
    onBackClick: () -> Unit,
    viewModel: StudentSearchAdvisorViewModel = viewModel()
) {
    StudentSearchAdvisorScreen(
        internshipId = internshipId,
        onBackClick = onBackClick,
        viewModel = viewModel,
        titleRes = R.string.search_teacher_title
    )
}
