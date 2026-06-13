package com.example.nextstep.ui.screens.company

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyAdvisorEvaluationDto
import com.example.nextstep.data.model.CompanyInternStudentProfileDto
import com.example.nextstep.data.model.CompanyStudentActivityDto
import com.example.nextstep.ui.utils.Formatters

@Composable
fun CompanyInternStudentProfileScreen(
    applicationId: String,
    onBackClick: () -> Unit,
    onStatusChanged: () -> Unit = {},
    viewModel: CompanyInternStudentProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(applicationId) {
        viewModel.loadProfile(applicationId)
    }

    val documentUrl = state.documentUrlToOpen

    LaunchedEffect(documentUrl) {
        if (!documentUrl.isNullOrBlank()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(documentUrl))
                context.startActivity(intent)
                viewModel.consumeDocumentUrl()
            } catch (_: Exception) {
                viewModel.onDocumentOpenFailed()
            }
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
        state.errorMessageRes != null -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White).padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        state.profile != null -> {
            CompanyInternStudentProfileContent(
                profile = state.profile!!,
                isOpeningDocument = state.isOpeningDocument,
                documentErrorRes = state.documentErrorRes,
                onBackClick = onBackClick,
                onOpenCv = viewModel::openCv,
                onOpenMotivationLetter = viewModel::openMotivationLetter,
                selectedTab = state.selectedTab,
                onTabChange = viewModel::onTabChange,
                activities = viewModel.getFilteredActivities(),
                isLoadingActivities = state.isLoadingActivities,
                activitiesErrorRes = state.activitiesErrorRes,
                selectedActivityFilter = state.selectedActivityFilter,
                onActivityFilterChange = viewModel::onActivityFilterChange,
                advisorEvaluation = state.advisorEvaluation,
                isLoadingEvaluation = state.isLoadingEvaluation,
                evaluationErrorRes = state.evaluationErrorRes
            )
        }
    }
}

@Composable
private fun CompanyInternStudentProfileContent(
    profile: CompanyInternStudentProfileDto,
    isOpeningDocument: Boolean,
    documentErrorRes: Int?,
    onBackClick: () -> Unit,
    onOpenCv: () -> Unit,
    onOpenMotivationLetter: () -> Unit,
    selectedTab: CompanyInternStudentTab,
    onTabChange: (CompanyInternStudentTab) -> Unit,
    activities: List<CompanyStudentActivityDto>,
    isLoadingActivities: Boolean,
    activitiesErrorRes: Int?,
    selectedActivityFilter: CompanyActivityFilter,
    onActivityFilterChange: (CompanyActivityFilter) -> Unit,
    advisorEvaluation: CompanyAdvisorEvaluationDto?,
    isLoadingEvaluation: Boolean,
    evaluationErrorRes: Int?
) {
    val fullName = profile.studentName.orEmpty().ifBlank { stringResource(R.string.student) }
    val initials = fullName.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifBlank { "?" }
    
    val statusText = Formatters.formatInternshipStatus(profile.internshipStatus)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val statusColor = when (profile.internshipStatus?.trim()?.lowercase()) {
        "accepted" -> Color(0xFF138A36)
        "active" -> Color(0xFF1565C0)
        "in_progress" -> Color(0xFFE65100)
        "completed" -> Color(0xFF2E7D32)
        else -> Color(0xFF777777)
    }
    val statusBg = when (profile.internshipStatus?.trim()?.lowercase()) {
        "accepted" -> Color(0xFFE8F5E9)
        "active" -> Color(0xFFE3F2FD)
        "in_progress" -> Color(0xFFFFF3E0)
        "completed" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF3F3F3)
    }

    val tabs = listOf(
        CompanyInternStudentTab.SUMMARY,
        CompanyInternStudentTab.ACTIVITIES,
        CompanyInternStudentTab.EVALUATION
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).statusBarsPadding().imePadding()
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.Black)
        }

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Column: Header and Tabs
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF2B2B2B)), contentAlignment = Alignment.Center) {
                        Text(text = initials, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = profile.studentEmail.orEmpty().ifBlank { stringResource(R.string.not_available) }, fontSize = 13.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.background(statusBg, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = statusText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Vertical Tabs for Landscape
                    tabs.forEach { tab ->
                        val label = when (tab) {
                            CompanyInternStudentTab.SUMMARY -> stringResource(R.string.company_intern_tab_summary)
                            CompanyInternStudentTab.ACTIVITIES -> stringResource(R.string.company_intern_tab_activities)
                            CompanyInternStudentTab.EVALUATION -> stringResource(R.string.company_intern_tab_evaluation)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedTab == tab) Color(0xFFFDFA52).copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onTabChange(tab) }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == tab) Color.Black else Color(0xFF8A8A8A)
                            )
                        }
                    }
                }

                // Right Column: Tab Content
                Column(modifier = Modifier.weight(2f).fillMaxHeight()) {
                    when (selectedTab) {
                        CompanyInternStudentTab.SUMMARY -> SummaryTab(profile, statusText, isOpeningDocument, documentErrorRes, onOpenCv, onOpenMotivationLetter)
                        CompanyInternStudentTab.ACTIVITIES -> ActivitiesTab(activities, isLoadingActivities, activitiesErrorRes, selectedActivityFilter, onActivityFilterChange)
                        CompanyInternStudentTab.EVALUATION -> EvaluationTab(advisorEvaluation, isLoadingEvaluation, evaluationErrorRes)
                    }
                }
            }
        } else {
            // Header
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFF2B2B2B)), contentAlignment = Alignment.Center) {
                    Text(text = initials, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = fullName, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = profile.studentEmail.orEmpty().ifBlank { stringResource(R.string.not_available) }, fontSize = 14.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(text = statusText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tab Row
            TabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]), color = Color(0xFFFDFA52)) },
                divider = {}
            ) {
                tabs.forEachIndexed { _, tab ->
                    val label = when (tab) {
                        CompanyInternStudentTab.SUMMARY -> stringResource(R.string.company_intern_tab_summary)
                        CompanyInternStudentTab.ACTIVITIES -> stringResource(R.string.company_intern_tab_activities)
                        CompanyInternStudentTab.EVALUATION -> stringResource(R.string.company_intern_tab_evaluation)
                    }
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { onTabChange(tab) },
                        text = {
                            Text(text = label, fontSize = 14.sp, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == tab) Color.Black else Color(0xFF8A8A8A))
                        },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color(0xFF8A8A8A)
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                CompanyInternStudentTab.SUMMARY -> SummaryTab(profile, statusText, isOpeningDocument, documentErrorRes, onOpenCv, onOpenMotivationLetter)
                CompanyInternStudentTab.ACTIVITIES -> ActivitiesTab(activities, isLoadingActivities, activitiesErrorRes, selectedActivityFilter, onActivityFilterChange)
                CompanyInternStudentTab.EVALUATION -> EvaluationTab(advisorEvaluation, isLoadingEvaluation, evaluationErrorRes)
            }
        }
    }
}

// ==================== SUMMARY TAB ====================

@Composable
private fun SummaryTab(
    profile: CompanyInternStudentProfileDto,
    statusText: String,
    isOpeningDocument: Boolean,
    documentErrorRes: Int?,
    onOpenCv: () -> Unit,
    onOpenMotivationLetter: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // RF25: Responsible Advisor Card
        item {
            InternProfileSectionCard(title = stringResource(R.string.responsible_advisor)) {
                if (profile.hasAdvisor == true) {
                    InternProfileInfoRow(label = stringResource(R.string.name_required).replace(" *", ""), value = profile.advisorName)
                    InternProfileInfoRow(label = stringResource(R.string.email), value = profile.advisorEmail)
                    if (!profile.advisorPhone.isNullOrBlank()) {
                        InternProfileInfoRow(label = stringResource(R.string.phone), value = profile.advisorPhone)
                    }
                    if (!profile.advisorAssignedAt.isNullOrBlank()) {
                        InternProfileInfoRow(label = stringResource(R.string.advisor_assigned_at), value = Formatters.formatDateTime(profile.advisorAssignedAt))
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.no_advisor_assigned),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.advisor_empty_state_description),
                            fontSize = 14.sp,
                            color = Color(0xFF8A8A8A),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        item {
            InternProfileSectionCard(title = stringResource(R.string.company_intern_profile_personal_data)) {
                InternProfileInfoRow(label = stringResource(R.string.first_name), value = profile.studentName)
                InternProfileInfoRow(label = stringResource(R.string.email), value = profile.studentEmail)
                InternProfileInfoRow(label = stringResource(R.string.phone), value = profile.studentPhone)
            }
        }
        item {
            InternProfileSectionCard(title = stringResource(R.string.company_intern_profile_academic_data)) {
                InternProfileInfoRow(label = stringResource(R.string.student_number), value = profile.studentNumber)
                InternProfileInfoRow(label = stringResource(R.string.course), value = profile.course)
                InternProfileInfoRow(label = stringResource(R.string.year), value = profile.academicYear?.toString())
                InternProfileInfoRow(label = stringResource(R.string.education_institution), value = profile.educationInstitution)
            }
        }
        item {
            InternProfileSectionCard(title = stringResource(R.string.company_intern_profile_internship)) {
                InternProfileInfoRow(label = stringResource(R.string.offer), value = profile.offerTitle)
                InternProfileInfoRow(label = stringResource(R.string.offer_area), value = profile.offerArea)
                InternProfileInfoRow(label = stringResource(R.string.location), value = profile.offerLocation)
                InternProfileInfoRow(label = stringResource(R.string.offer_work_mode), value = Formatters.formatWorkMode(profile.offerWorkMode))
                InternProfileInfoRow(label = stringResource(R.string.status), value = statusText)
                InternProfileInfoRow(label = stringResource(R.string.company_intern_profile_application_date), value = Formatters.formatDateTime(profile.applicationCreatedAt))
            }
        }
        item {
            InternProfileSectionCard(title = stringResource(R.string.application_documents_title)) {
                Text(text = stringResource(R.string.cv), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                if (profile.cvPath.isNullOrBlank()) {
                    Text(text = stringResource(R.string.company_candidate_cv_not_available), fontSize = 14.sp, color = Color(0xFF8A8A8A))
                } else {
                    Button(onClick = onOpenCv, enabled = !isOpeningDocument, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black, disabledContainerColor = Color(0xFFF0F0F0), disabledContentColor = Color(0xFF8A8A8A))) {
                        Text(text = stringResource(R.string.open_document), fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = stringResource(R.string.motivation_letter), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                if (profile.motivationLetterPath.isNullOrBlank()) {
                    Text(text = stringResource(R.string.company_candidate_letter_not_available), fontSize = 14.sp, color = Color(0xFF8A8A8A))
                } else {
                    Button(onClick = onOpenMotivationLetter, enabled = !isOpeningDocument, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black, disabledContainerColor = Color(0xFFF0F0F0), disabledContentColor = Color(0xFF8A8A8A))) {
                        Text(text = stringResource(R.string.open_document), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        if (isOpeningDocument) {
            item { Spacer(modifier = Modifier.height(12.dp)); Text(text = stringResource(R.string.company_application_opening_document), color = Color(0xFF8A8A8A), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
        }
        documentErrorRes?.let { errorRes ->
            item { Spacer(modifier = Modifier.height(12.dp)); Text(text = stringResource(errorRes), color = Color(0xFFB00020), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
        }
        item { Spacer(modifier = Modifier.height(42.dp)) }
    }
}

// ==================== ACTIVITIES TAB (RF27) ====================

@Composable
private fun ActivitiesTab(
    activities: List<CompanyStudentActivityDto>,
    isLoadingActivities: Boolean,
    activitiesErrorRes: Int?,
    selectedActivityFilter: CompanyActivityFilter,
    onActivityFilterChange: (CompanyActivityFilter) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.company_intern_activities_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf(
                CompanyActivityFilter.ALL to stringResource(R.string.company_intern_filter_all),
                CompanyActivityFilter.PENDING to stringResource(R.string.company_intern_filter_pending),
                CompanyActivityFilter.IN_PROGRESS to stringResource(R.string.company_intern_filter_in_progress),
                CompanyActivityFilter.COMPLETED to stringResource(R.string.company_intern_filter_completed)
            )
            filters.forEach { (filter, label) ->
                FilterChip(selected = selectedActivityFilter == filter, onClick = { onActivityFilterChange(filter) }, label = { Text(text = label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFDFA52), selectedLabelColor = Color.Black, containerColor = Color(0xFFF0F0F0), labelColor = Color(0xFF555555)))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoadingActivities -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.Black) }
            }
            activitiesErrorRes != null -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { Text(text = stringResource(activitiesErrorRes), color = Color(0xFFB00020), fontSize = 15.sp, textAlign = TextAlign.Center) }
            }
            activities.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.company_intern_activities_empty), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.company_intern_activities_empty_subtitle), fontSize = 14.sp, color = Color(0xFF8A8A8A), textAlign = TextAlign.Center)
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    items(activities) { activity -> ActivityCard(activity = activity) }
                    item { Spacer(modifier = Modifier.height(42.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: CompanyStudentActivityDto) {
    val statusText = translateActivityStatus(activity.status)
    val statusColor = when (activity.status?.trim()?.lowercase()) {
        "pending" -> Color(0xFF8D6E00)
        "in_progress" -> Color(0xFF1565C0)
        "completed" -> Color(0xFF2E7D32)
        "cancelled" -> Color(0xFFC62828)
        else -> Color(0xFF777777)
    }
    val statusBg = when (activity.status?.trim()?.lowercase()) {
        "pending" -> Color(0xFFFFF9C4)
        "in_progress" -> Color(0xFFE3F2FD)
        "completed" -> Color(0xFFE8F5E9)
        "cancelled" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF3F3F3)
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = activity.title.orEmpty().ifBlank { stringResource(R.string.not_available) }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }
            if (!activity.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = activity.description, fontSize = 14.sp, color = Color(0xFF555555), lineHeight = 20.sp)
            }
            if (!activity.dueDate.isNullOrBlank() || !activity.completedAt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                if (!activity.dueDate.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.company_intern_due_date) + ": ", fontSize = 13.sp, color = Color(0xFF8A8A8A))
                        Text(text = Formatters.formatDateTime(activity.dueDate), fontSize = 13.sp, color = Color.Black)
                    }
                }
                if (!activity.completedAt.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.company_intern_completed_at) + ": ", fontSize = 13.sp, color = Color(0xFF8A8A8A))
                        Text(text = Formatters.formatDateTime(activity.completedAt), fontSize = 13.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}

// ==================== EVALUATION TAB (RF28) ====================

@Composable
private fun EvaluationTab(
    advisorEvaluation: CompanyAdvisorEvaluationDto?,
    isLoadingEvaluation: Boolean,
    evaluationErrorRes: Int?
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.company_intern_evaluation_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoadingEvaluation -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.Black) }
            }
            evaluationErrorRes != null -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { Text(text = stringResource(evaluationErrorRes), color = Color(0xFFB00020), fontSize = 15.sp, textAlign = TextAlign.Center) }
            }
            advisorEvaluation == null -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.company_intern_evaluation_empty), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.company_intern_evaluation_empty_subtitle), fontSize = 14.sp, color = Color(0xFF8A8A8A), textAlign = TextAlign.Center)
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    // Grade
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(text = stringResource(R.string.company_intern_evaluation_grade), fontSize = 13.sp, color = Color(0xFF8A8A8A))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = advisorEvaluation.grade?.toString() ?: stringResource(R.string.not_available), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                    // Status + Date
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                InternProfileInfoRow(label = stringResource(R.string.company_intern_evaluation_status), value = translateEvaluationStatus(advisorEvaluation.status))
                                InternProfileInfoRow(label = stringResource(R.string.company_intern_evaluation_date), value = Formatters.formatDateTime(advisorEvaluation.updatedAt ?: advisorEvaluation.createdAt))
                            }
                        }
                    }
                    // Feedback
                    if (!advisorEvaluation.qualitativeFeedback.isNullOrBlank()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(text = stringResource(R.string.company_intern_evaluation_feedback), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = advisorEvaluation.qualitativeFeedback, fontSize = 15.sp, color = Color(0xFF555555), lineHeight = 21.sp)
                                }
                            }
                        }
                    }
                    // Strengths
                    if (!advisorEvaluation.strengths.isNullOrBlank()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(text = stringResource(R.string.company_intern_evaluation_strengths), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = advisorEvaluation.strengths, fontSize = 15.sp, color = Color(0xFF555555), lineHeight = 21.sp)
                                }
                            }
                        }
                    }
                    // Improvements
                    if (!advisorEvaluation.improvements.isNullOrBlank()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(text = stringResource(R.string.company_intern_evaluation_improvements), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = advisorEvaluation.improvements, fontSize = 15.sp, color = Color(0xFF555555), lineHeight = 21.sp)
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(42.dp)) }
                }
            }
        }
    }
}

// ==================== SHARED COMPONENTS ====================

@Composable
private fun InternProfileSectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun InternProfileInfoRow(label: String, value: String?) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF8A8A8A))
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value.orEmpty().ifBlank { stringResource(R.string.not_available) }, fontSize = 15.sp, color = Color.Black, lineHeight = 21.sp)
    }
}

private fun translateInternshipStatus(status: String?): String {
    return Formatters.formatInternshipStatus(status)
}

private fun translateActivityStatus(status: String?): String {
    return when (status?.trim()?.lowercase()) {
        "pending" -> "Pendente"
        "in_progress" -> "Em progresso"
        "completed" -> "Concluída"
        "cancelled" -> "Cancelada"
        else -> status.orEmpty()
    }
}

private fun translateEvaluationStatus(status: String?): String {
    return when (status?.trim()?.lowercase()) {
        "draft" -> "Rascunho"
        "submitted" -> "Submetida"
        "final" -> "Final"
        else -> status.orEmpty().ifBlank { "Não disponível" }
    }
}