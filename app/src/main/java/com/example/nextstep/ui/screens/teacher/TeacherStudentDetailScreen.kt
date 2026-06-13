package com.example.nextstep.ui.screens.teacher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.ApplicationTaskDto
import com.example.nextstep.data.model.TeacherStudentDetailNonSerializable
import com.example.nextstep.ui.utils.localizedPriority

@Composable
fun TeacherStudentDetailScreen(
    applicationId: String,
    studentProfileId: String,
    initialStudentName: String,
    initialOfferTitle: String?,
    initialCompanyName: String?,
    status: String?,
    onBackClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    viewModel: TeacherStudentDetailViewModel = viewModel()
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(applicationId) {
        Log.d("TeacherStudentDetail", "Loaded with applicationId=$applicationId")
        viewModel.loadStudentDetail(applicationId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
            Text(
                text = "Detalhe do aluno",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        // Content
        TeacherDetailContent(
            applicationId = applicationId,
            initialName = initialStudentName,
            initialOffer = initialOfferTitle,
            initialCompany = initialCompanyName,
            initialStatus = status,
            detailState = detailState,
            onMessageClick = onMessageClick,
            viewModel = viewModel
        )
    }
}

@Composable
private fun TeacherDetailContent(
    applicationId: String,
    initialName: String,
    initialOffer: String?,
    initialCompany: String?,
    initialStatus: String?,
    detailState: TeacherStudentDetailState,
    onMessageClick: () -> Unit,
    viewModel: TeacherStudentDetailViewModel
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.summary),
        stringResource(R.string.tasks),
        "Docs",
        "Avaliação"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with initial data or loaded data
        TeacherDetailHeaderCard(
            name = detailState.detail?.studentName ?: initialName,
            offerTitle = detailState.detail?.offerTitle ?: initialOffer,
            companyName = detailState.detail?.companyName ?: initialCompany,
            status = detailState.detail?.status ?: initialStatus
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black,
            divider = {
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFEDEDED))
            },
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(3.dp)
                            .background(Color(0xFF2B2B2B))
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color.Black else Color(0xFF777777),
                            maxLines = 1
                        )
                    }
                )
            }
        }

        if (detailState.isLoading && detailState.detail == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else if (detailState.errorMessage != null && detailState.detail == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = detailState.errorMessage ?: "", color = Color(0xFFB00020), fontSize = 15.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.try_again),
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.loadStudentDetail(applicationId) }
                    )
                }
            }
        } else {
            when (selectedTabIndex) {
                0 -> TeacherSummaryTab(detail = detailState.detail, onMessageClick = onMessageClick)
                1 -> TeacherTasksTab(viewModel = viewModel)
                2 -> TeacherDocumentsTab(applicationId = applicationId, detail = detailState.detail, viewModel = viewModel)
                3 -> TeacherEvaluationTab(applicationId = applicationId, viewModel = viewModel, studentStatus = detailState.detail?.status ?: initialStatus)
            }
        }
    }
}

@Composable
private fun TeacherDetailHeaderCard(
    name: String,
    offerTitle: String?,
    companyName: String?,
    status: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {
            val initials = name
                .split(" ").filter { it.isNotBlank() }.take(2)
                .joinToString("") { it.first().uppercase() }.ifBlank { "?" }
            Text(text = initials, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            val displayOffer = if (offerTitle == "na" || offerTitle.isNullOrBlank()) "" else "Estágio: $offerTitle"
            if (displayOffer.isNotBlank()) {
                Text(
                    text = displayOffer, fontSize = 13.sp, color = Color(0xFF555555),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            val displayCompany = if (companyName == "na" || companyName.isNullOrBlank()) "" else "Empresa: $companyName"
            if (displayCompany.isNotBlank()) {
                Text(
                    text = displayCompany, fontSize = 12.sp, color = Color(0xFF777777),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
        TeacherDetailStatusBadge(status = status)
    }
}

@Composable
private fun TeacherDetailStatusBadge(status: String?) {
    val (labelRes, bgColor, textColor) = when {
        status == "accepted" || status == "active" || status == "ativo" || status == "aceite" -> Triple(R.string.teacher_status_active, Color(0xFFE8F5E9), Color(0xFF2E7D32))
        status == "pending" || status == "pendente" -> Triple(R.string.teacher_status_pending, Color(0xFFFFF9C4), Color(0xFF8D6E00))
        status == "completed" || status == "concluido" || status == "concluído" -> Triple(R.string.teacher_status_completed, Color(0xFFE3F2FD), Color(0xFF1565C0))
        else -> Triple(null, Color(0xFFF5F5F5), Color(0xFF777777))
    }
    val label = if (labelRes != null) stringResource(labelRes) else (status?.replaceFirstChar { it.uppercase() } ?: "")
    if (label.isBlank() || label == "na") return
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

// ── SUMMARY TAB ──

@Composable
private fun TeacherSummaryTab(detail: TeacherStudentDetailNonSerializable?, onMessageClick: () -> Unit) {
    if (detail == null) return
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        TeacherDetailSection(title = stringResource(R.string.section_student)) {
            TeacherDetailRow(label = stringResource(R.string.name_required).replace("*", "").trim(), value = detail.studentName)
            TeacherDetailRow(label = stringResource(R.string.email), value = detail.studentEmail ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.course), value = detail.course ?: "Não disponível")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TeacherDetailSection(title = stringResource(R.string.about_internship)) {
            TeacherDetailRow(label = stringResource(R.string.offer_title), value = detail.offerTitle ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.company_name), value = detail.companyName ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.location), value = detail.location ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.work_mode), value = detail.workMode ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.duration), value = detail.duration ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.status), value = detail.status?.replaceFirstChar { it.uppercase() } ?: "Não disponível")
            TeacherDetailRow(label = stringResource(R.string.start_date), value = detail.startDate?.substringBefore("T") ?: "Não disponível")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TeacherDetailSection(title = stringResource(R.string.task_progress)) {
            val completed = detail.completedTasks
            val total = detail.totalTasks
            val progress = if (total > 0) completed.toFloat() / total else 0f
            val percentage = if (total > 0) (progress * 100).toInt() else 0
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$completed / $total ${stringResource(R.string.status_completed).lowercase()}", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF2B2B2B), trackColor = Color(0xFFEDEDED)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$percentage%", fontSize = 12.sp, color = Color(0xFF777777))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onMessageClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.message), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── TASKS TAB ──

@Composable
private fun TeacherTasksTab(viewModel: TeacherStudentDetailViewModel) {
    val tasksState by viewModel.tasksState.collectAsState()
    var taskFilter by rememberSaveable { mutableStateOf("all") }
    
    val filteredTasks = when (taskFilter) {
        "pending" -> tasksState.tasks.filter { it.status.lowercase() in listOf("pending", "pendente", "in_progress", "em_progresso") }
        "completed" -> tasksState.tasks.filter { it.status.lowercase() in listOf("completed", "concluida", "concluída") }
        else -> tasksState.tasks
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (tasksState.isLoadingTasks) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            // Summary Card
            if (tasksState.tasks.isNotEmpty()) {
                TeacherTasksSummaryCard(tasksState)
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("all", "pending", "completed")) { filter ->
                    val label = when (filter) {
                        "all" -> stringResource(R.string.all)
                        "pending" -> stringResource(R.string.to_complete)
                        else -> stringResource(R.string.completed)
                    }
                    Surface(
                        color = if (taskFilter == filter) Color(0xFF2B2B2B) else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { taskFilter = filter }
                    ) {
                        Text(
                            text = label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 12.sp, maxLines = 1, softWrap = false,
                            fontWeight = if (taskFilter == filter) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (taskFilter == filter) Color.White else Color(0xFF333333)
                        )
                    }
                }
            }

            if (tasksState.tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ainda não existem tarefas registadas para este aluno.",
                            fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Quando forem criadas tarefas, elas aparecerão aqui.",
                            fontSize = 13.sp, color = Color(0xFF777777),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Nenhuma tarefa encontrada para este filtro.", fontSize = 14.sp, color = Color(0xFF777777))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks) { task -> TeacherTaskItem(task = task) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TeacherTasksSummaryCard(state: TeacherTasksState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.task_progress), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SummaryStatItem(label = "Total", count = state.tasks.size)
            SummaryStatItem(label = "Concluídas", count = state.completedTasksCount)
            SummaryStatItem(label = "Pendentes", count = state.pendingTasksCount)
            SummaryStatItem(label = "Em curso", count = state.inProgressTasksCount)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LinearProgressIndicator(
            progress = { state.progressPercentage / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF2B2B2B),
            trackColor = Color(0xFFEDEDED)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "${state.progressPercentage}% concluído", fontSize = 12.sp, color = Color(0xFF555555), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SummaryStatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = label, fontSize = 11.sp, color = Color(0xFF777777))
    }
}

@Composable
private fun TeacherTaskItem(task: ApplicationTaskDto) {
    val status = task.status.lowercase()
    val isCompleted = status in listOf("completed", "concluida", "concluída")
    val isInProgress = status in listOf("in_progress", "em_progresso")
    
    val statusLabel = when {
        isCompleted -> "Concluída"
        isInProgress -> "Em progresso"
        else -> "Pendente"
    }
    
    val statusColor = when {
        isCompleted -> Color(0xFF2E7D32)
        isInProgress -> Color(0xFF1976D2)
        else -> Color(0xFFF57F17)
    }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5).copy(alpha = 0.5f)).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Text(text = desc, fontSize = 13.sp, color = Color(0xFF555555), modifier = Modifier.padding(top = 4.dp))
                }
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = statusLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Priority
            task.priority?.takeIf { it.isNotBlank() }?.let { priority ->
                Icon(
                    imageVector = Icons.Default.PriorityHigh,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF777777)
                )
                Text(text = localizedPriority(priority), fontSize = 12.sp, color = Color(0xFF777777), modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            // Due Date
            task.dueDate?.takeIf { it.isNotBlank() }?.let { date ->
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF777777)
                )
                Text(text = "Prazo: ${date.substringBefore("T")}", fontSize = 12.sp, color = Color(0xFF777777), modifier = Modifier.padding(start = 4.dp))
            }
        }
        
        if (isCompleted && !task.completedAt.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF2E7D32)
                )
                Text(text = "Concluída em: ${task.completedAt.substringBefore("T")}", fontSize = 12.sp, color = Color(0xFF2E7D32), modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

// ── DOCUMENTS TAB ──

@Composable
private fun TeacherDocumentsTab(applicationId: String, detail: TeacherStudentDetailNonSerializable?, viewModel: TeacherStudentDetailViewModel) {
    val context = LocalContext.current
    
    LaunchedEffect(detail) {
        if (detail != null) {
            Log.d("TeacherDocuments", "applicationId=$applicationId cvPath=${detail.cvPath} motivationPath=${detail.motivationLetterPath}")
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text(
            text = "Documentos da candidatura",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // CV
        TeacherDocumentItem(
            name = "Curriculum Vitae (CV)",
            path = detail?.cvPath,
            onOpen = { path -> 
                viewModel.openDocument(
                    bucket = "applications",
                    path = path,
                    onSuccess = { url -> 
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Motivation Letter
        TeacherDocumentItem(
            name = "Carta de Motivação",
            path = detail?.motivationLetterPath,
            onOpen = { path -> 
                viewModel.openDocument(
                    bucket = "applications",
                    path = path,
                    onSuccess = { url -> 
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF9C4).copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF8D6E00),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Estes são os documentos submetidos pelo aluno no momento da candidatura.",
                    fontSize = 13.sp,
                    color = Color(0xFF8D6E00)
                )
            }
        }
    }
}

@Composable
private fun TeacherDocumentItem(
    name: String,
    path: String?,
    onOpen: (String) -> Unit
) {
    val isAvailable = !path.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = null,
                tint = if (isAvailable) Color(0xFF333333) else Color(0xFFBBBBBB),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAvailable) Color.Black else Color(0xFF777777)
                )
                if (!isAvailable) {
                    Text(
                        text = "Documento não disponível.",
                        fontSize = 11.sp,
                        color = Color(0xFFB00020)
                    )
                }
            }
        }
        
        if (isAvailable) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onOpen(path!!) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B2B2B))
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF2B2B2B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Visualizar Documento", color = Color(0xFF2B2B2B), fontSize = 13.sp)
            }
        }
    }
}

// ── EVALUATION TAB ──

@Composable
private fun TeacherEvaluationTab(
    applicationId: String,
    viewModel: TeacherStudentDetailViewModel,
    studentStatus: String?
) {
    val evalState by viewModel.evaluationState.collectAsState()
    
    // Check if student is active/accepted/completed
    val canEvaluate = studentStatus?.lowercase() in listOf("accepted", "active", "ativo", "aceite", "completed", "concluido", "concluído")

    LaunchedEffect(applicationId) {
        viewModel.loadEvaluation(applicationId)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        // Title
        Text(
            text = "Avaliação do Aluno",
            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (!canEvaluate) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF9C4))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Só é possível avaliar alunos acompanhados.",
                    fontSize = 15.sp,
                    color = Color(0xFF8D6E00),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
            return@Column
        }

        // Loading
        if (evalState.isLoadingEvaluation) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
            return@Column
        }

        // Error message
        evalState.errorMessage?.let { error ->
            Text(text = error, color = Color(0xFFB00020), fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
        }

        // Success message
        evalState.successMessage?.let { success ->
            Text(text = success, color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 12.dp))
        }

        if (evalState.evaluation != null && !evalState.isEditing) {
            // ── Show completed evaluation in read-only ──
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F5E9))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Avaliação concluída", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Read-only details
            TeacherEvaluationReadOnly(evalState = evalState)
            Spacer(modifier = Modifier.height(16.dp))

            // Edit button
            Button(
                onClick = { viewModel.startEditing() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
            ) {
                Icon(imageVector = Icons.Filled.Grade, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Editar Avaliação", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        } else {
            // ── Evaluation form (new or editing) ──
            TeacherEvaluationForm(
                evalState = evalState,
                onGradeChange = { viewModel.onGradeChange(it) },
                onFeedbackChange = { viewModel.onQualitativeFeedbackChange(it) },
                onStrengthsChange = { viewModel.onStrengthsChange(it) },
                onImprovementsChange = { viewModel.onImprovementsChange(it) },
                onSave = { viewModel.saveEvaluation(applicationId) },
                onCancel = { viewModel.cancelEditing() }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TeacherEvaluationReadOnly(evalState: TeacherEvaluationState) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5)).padding(16.dp)
    ) {
        Text(text = "Nota Final", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
        Text(text = evalState.grade.ifBlank { "-" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Apreciação Qualitativa", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
        Text(text = evalState.qualitativeFeedback.ifBlank { "-" }, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        
        if (evalState.strengths.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pontos Fortes", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
            Text(text = evalState.strengths, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        }
        
        if (evalState.improvements.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pontos a Melhorar", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
            Text(text = evalState.improvements, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun TeacherEvaluationForm(
    evalState: TeacherEvaluationState,
    onGradeChange: (String) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onStrengthsChange: (String) -> Unit,
    onImprovementsChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val hasExistingEvaluation = evalState.evaluation != null

    // Grade
    Text(text = "Nota Final (0-20) *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = evalState.grade,
        onValueChange = onGradeChange,
        placeholder = { Text("Ex: 16") },
        singleLine = true,
        isError = evalState.gradeError != null,
        supportingText = evalState.gradeError?.let { err ->
            { Text(text = err, color = Color(0xFFB00020), fontSize = 12.sp) }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Qualitative feedback
    Text(text = "Apreciação Qualitativa *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = evalState.qualitativeFeedback,
        onValueChange = onFeedbackChange,
        placeholder = { Text("Escreva um comentário sobre o desempenho do aluno...") },
        minLines = 3, maxLines = 5,
        isError = evalState.feedbackError != null,
        supportingText = evalState.feedbackError?.let { err ->
            { Text(text = err, color = Color(0xFFB00020), fontSize = 12.sp) }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Strengths
    Text(text = "Pontos Fortes", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = evalState.strengths,
        onValueChange = onStrengthsChange,
        placeholder = { Text("Principais competências demonstradas...") },
        minLines = 2, maxLines = 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Improvements
    Text(text = "Pontos a Melhorar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = evalState.improvements,
        onValueChange = onImprovementsChange,
        placeholder = { Text("Áreas onde o aluno pode evoluir...") },
        minLines = 2, maxLines = 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Save button
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B)),
        enabled = !evalState.isSavingEvaluation
    ) {
        if (evalState.isSavingEvaluation) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Icon(imageVector = Icons.Filled.Grade, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            val buttonLabel = if (hasExistingEvaluation) "Atualizar Avaliação" else "Guardar Avaliação"
            Text(text = buttonLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }

    // Cancel button when editing
    if (hasExistingEvaluation) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B2B2B))
        ) {
            Text(text = stringResource(R.string.cancel), fontSize = 14.sp, color = Color(0xFF2B2B2B))
        }
    }
}

// ── COMMON COMPONENTS ──

@Composable
private fun TeacherDetailSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5).copy(alpha = 0.5f)).padding(16.dp)
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun TeacherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF777777))
        Text(
            text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}
