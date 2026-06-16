package com.example.nextstep.ui.screens.teacher

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.ui.utils.AppStatus
import com.example.nextstep.ui.utils.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherRequestDetailScreen(
    applicationId: String,
    onBackClick: () -> Unit,
    viewModel: TeacherRequestDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showRejectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(applicationId) {
        viewModel.loadRequestDetail(applicationId)
    }

    // Handle Action Errors (like document opening or update failures)
    LaunchedEffect(state.actionError) {
        state.actionError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearActionError()
        }
    }

    if (state.isActionSuccess) {
        LaunchedEffect(Unit) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.request_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                }
                state.loadError != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.loadError ?: "",
                            color = Color.Red,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadRequestDetail(applicationId) }) {
                            Text(stringResource(R.string.try_again))
                        }
                    }
                }
                state.request != null -> {
                    val request = state.request!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Aluno Section
                        SectionHeader(title = stringResource(R.string.section_student))
                        DetailItem(label = stringResource(R.string.student_name_label), value = request.studentName)
                        DetailItem(label = stringResource(R.string.email), value = request.studentEmail)
                        request.course?.let { DetailItem(label = stringResource(R.string.course), value = it) }
                        val location = listOfNotNull(request.city, request.country).joinToString(", ")
                        if (location.isNotBlank()) DetailItem(label = stringResource(R.string.location), value = location)

                        HorizontalDivider(color = Color(0xFFF5F5F5))

                        // Oferta Section
                        SectionHeader(title = stringResource(R.string.section_offer))
                        DetailItem(label = stringResource(R.string.offer_title), value = request.offerTitle)
                        DetailItem(label = stringResource(R.string.company_name), value = request.companyName)
                        request.location?.let { DetailItem(label = stringResource(R.string.location), value = it) }
                        request.workMode?.let { DetailItem(label = stringResource(R.string.work_mode), value = it) }
                        request.duration?.let { DetailItem(label = stringResource(R.string.duration), value = it) }
                        request.description?.let {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text(text = stringResource(R.string.description), fontSize = 12.sp, color = Color.Gray)
                                Text(text = it, fontSize = 14.sp, color = Color.Black)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF5F5F5))

                        // Documentos Section
                        SectionHeader(title = stringResource(R.string.section_documents))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DocumentButton(
                                label = stringResource(R.string.cv),
                                enabled = !request.cvPath.isNullOrBlank(),
                                onClick = {
                                    request.cvPath?.let { path ->
                                        viewModel.openDocument(path) { url ->
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, context.getString(R.string.student_application_document_open_error), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            )
                            DocumentButton(
                                label = stringResource(R.string.motivation_letter),
                                enabled = !request.motivationLetterPath.isNullOrBlank(),
                                onClick = {
                                    request.motivationLetterPath?.let { path ->
                                        viewModel.openDocument(path) { url ->
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, context.getString(R.string.student_application_document_open_error), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Actions
                        if (request.status.lowercase() == AppStatus.PENDING) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showRejectDialog = true },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                    enabled = !state.isUpdating
                                ) {
                                    Text(stringResource(R.string.reject))
                                }
                                Button(
                                    onClick = { viewModel.acceptRequest(applicationId) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TeacherUiColors.YellowAccent, contentColor = Color.Black),
                                    enabled = !state.isUpdating
                                ) {
                                    if (state.isUpdating) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                                    } else {
                                        Text(stringResource(R.string.accept))
                                    }
                                }
                            }
                        } else {
                            Surface(
                                color = if (request.status.lowercase() == AppStatus.ACCEPTED) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (request.status.lowercase() == AppStatus.ACCEPTED) stringResource(R.string.request_accepted) else stringResource(R.string.request_rejected),
                                    modifier = Modifier.padding(16.dp),
                                    color = if (request.status.lowercase() == AppStatus.ACCEPTED) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text(stringResource(R.string.reject_request_title)) },
            text = { Text(stringResource(R.string.reject_request_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRejectDialog = false
                        viewModel.rejectRequest(applicationId)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(stringResource(R.string.reject))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DocumentButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
    }
}
