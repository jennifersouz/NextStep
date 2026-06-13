package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInternshipDetailScreen(
    internshipId: String,
    onBackClick: () -> Unit,
    onChatClick: (String, String) -> Unit,
    onSearchAdvisorClick: () -> Unit,
    viewModel: StudentInternshipDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(internshipId) {
        viewModel.loadDetail(internshipId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estágios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = Color.Red
                    )
                }
                uiState.internship != null -> {
                    InternshipDetailContent(
                        internship = uiState.internship!!,
                        tasks = uiState.tasks,
                        onChatClick = onChatClick,
                        onSearchAdvisorClick = onSearchAdvisorClick
                    )
                }
            }
        }
    }
}

@Composable
fun InternshipDetailContent(
    internship: StudentSubmittedApplicationDto,
    tasks: List<AdvisorTaskListItemDto>,
    onChatClick: (String, String) -> Unit,
    onSearchAdvisorClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F4F4))
                    .border(1.dp, Color(0xFFE5E5E5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = internship.companyName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(internship.companyName, color = Color.Gray, fontSize = 14.sp)
                Text(internship.offerTitle, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Advisors Section
        Text("Orientadores", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Company Advisor
        AdvisorItem(
            organization = internship.companyName,
            name = internship.advisorName ?: "A aguardar atribuição",
            onChatClick = if (internship.advisorProfileId != null) {
                { onChatClick(internship.id, internship.advisorName ?: "") }
            } else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Academic Advisor
        if (internship.teacherProfileId != null) {
            AdvisorItem(
                organization = internship.institutionName ?: "Instituição",
                name = internship.teacherName ?: "Orientador Académico",
                onChatClick = { onChatClick(internship.id, internship.teacherName ?: "") }
            )
        } else {
            Column {
                Text("Instituição", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSearchAdvisorClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Procurar", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (internship.status == "completed") {
            EvaluationsSection(internship)
        } else {
            TasksSection(tasks)
        }
    }
}

@Composable
fun AdvisorItem(
    organization: String,
    name: String,
    onChatClick: (() -> Unit)?
) {
    Column {
        Text(organization, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9D9D9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, fontWeight = FontWeight.Medium, fontSize = 16.sp, modifier = Modifier.weight(1f))
            if (onChatClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp).clickable { onChatClick() }
                )
            }
        }
    }
}

@Composable
fun TasksSection(tasks: List<AdvisorTaskListItemDto>) {
    Text("Tarefas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (tasks.isEmpty()) {
        Text("Sem tarefas atribuídas.", color = Color.Gray)
    } else {
        tasks.forEach { task ->
            TaskCard(task)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TaskCard(task: AdvisorTaskListItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.status == "completed", onCheckedChange = null, enabled = false)
            Spacer(modifier = Modifier.width(8.dp))
            Text(task.title, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun EvaluationsSection(internship: StudentSubmittedApplicationDto) {
    Text("Avaliação Orientadores", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))

    EvaluationRow(name = internship.advisorName ?: "Orientador Empresa", grade = internship.companyAdvisorGrade ?: "18")
    Spacer(modifier = Modifier.height(16.dp))
    EvaluationRow(name = internship.teacherName ?: "Orientador Académico", grade = internship.academicAdvisorGrade ?: "18")

    Spacer(modifier = Modifier.height(32.dp))
    Text("Avaliação Final", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Text(text = "18", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Text(text = " / 20", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun EvaluationRow(name: String, grade: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFD9D9D9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, modifier = Modifier.weight(1f))
        
        Surface(
            modifier = Modifier.border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Text(text = grade, fontWeight = FontWeight.Medium)
        }
        Text(text = " / 20", color = Color.Gray, modifier = Modifier.padding(start = 8.dp), fontSize = 14.sp)
    }
}
