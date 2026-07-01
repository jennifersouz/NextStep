package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyEmployeeInviteDisplayDto
import com.example.nextstep.ui.components.AppFilterChipsRow

@Composable
fun CompanyEmployeesScreen(
    viewModel: CompanyEmployeesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadEmployees()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    color = Color(0xFF8A8A8A),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = Color(0xFF8A8A8A)
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color.Black,
                cursorColor = Color.Black
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Search
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppFilterChipsRow(
            filters = CompanyEmployeeFilter.entries,
            selectedFilter = state.selectedEmployeeFilter,
            labelProvider = { filter -> stringResource(filter.labelRes) },
            onFilterSelected = { viewModel.onFilterChange(it) },
            contentPadding = PaddingValues(0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                state.errorMessageRes != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(state.errorMessageRes!!),
                            color = Color(0xFFB00020),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                state.filteredEmployees.isEmpty() && state.employees.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_employees),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.add_employees_description),
                                fontSize = 14.sp,
                                color = Color(0xFF8A8A8A),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                state.filteredEmployees.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_employees_filter),
                            fontSize = 16.sp,
                            color = Color(0xFF8A8A8A),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.filteredEmployees,
                            key = { it.id }
                        ) { employee ->
                            CompanyEmployeeCard(employee = employee)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanyEmployeeCard(
    employee: CompanyEmployeeInviteDisplayDto
) {
    val hasName = !employee.firstName.isNullOrBlank() || !employee.lastName.isNullOrBlank()

    val displayTitle: String
    val displaySubtitle: String?

    if (hasName) {
        displayTitle = employee.displayName
        displaySubtitle = if (employee.status == "pending") employee.email else employee.department
    } else {
        displayTitle = employee.email
        displaySubtitle = stringResource(R.string.invite_sent)
    }

    val initials = if (hasName) {
        employee.displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    } else {
        null
    }

    val badgeLabelRes = when (employee.status) {
        "active" -> R.string.status_active
        "pending" -> R.string.pending
        "inactive" -> R.string.status_inactive
        else -> null
    }

    val badgeBg = when (employee.status) {
        "active" -> Color(0xFFE8F5E9)
        "pending" -> Color(0xFFFFF4CC)
        "inactive" -> Color(0xFFF1F1F1)
        else -> Color.Transparent
    }

    val badgeTextColor = when (employee.status) {
        "active" -> Color(0xFF2E7D32)
        "pending" -> Color(0xFF8A6D00)
        "inactive" -> Color(0xFF666666)
        else -> Color.Transparent
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (initials != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B2B2B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF3CD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = stringResource(R.string.pending_invite),
                        tint = Color(0xFF7A5D00),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (displaySubtitle != null) {
                    Text(
                        text = displaySubtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF666666),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (badgeLabelRes != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = stringResource(badgeLabelRes),
                        color = badgeTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (employee.status != "pending" && displaySubtitle == employee.department) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = employee.email,
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
