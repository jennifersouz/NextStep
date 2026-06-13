package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.nextstep.data.model.AdminCompanyDto

@Composable
fun AdminCompaniesScreen(
    viewModel: AdminCompaniesViewModel = viewModel(),
    onCompanyClick: (AdminCompanyDto) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCompanies()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title
        Text(
            text = "Empresas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        AdminSearchBar(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = stringResource(R.string.search_company),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        AdminCompaniesFilterChips(
            selectedFilter = state.selectedFilter,
            onFilterClick = { viewModel.onFilterChange(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = Color(0xFFB00020),
                        fontSize = 15.sp
                    )
                }
            }

            state.filteredCompanies.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma empresa encontrada.",
                        fontSize = 15.sp,
                        color = Color(0xFF777777)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.filteredCompanies) { company ->
                        AdminCompanyListItem(
                            company = company,
                            onClick = { onCompanyClick(company) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(96.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCompaniesFilterChips(
    selectedFilter: AdminCompaniesFilter,
    onFilterClick: (AdminCompaniesFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AdminCompaniesFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFFFDFA52) else Color(0xFFF5F5F5))
                    .clickable { onFilterClick(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter.label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AdminCompanyListItem(
    company: AdminCompanyDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with company initial
        val initial = company.companyName?.firstOrNull()?.uppercase() ?: "E"

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Business,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = company.companyName ?: "Empresa",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val details = listOfNotNull(
                    company.businessArea,
                    company.location
                ).joinToString(" · ")

                if (details.isNotBlank()) {
                    Text(
                        text = details,
                        fontSize = 13.sp,
                        color = Color(0xFF777777),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Offer count if available
                company.offersCount?.let { count ->
                    Text(
                        text = "$count oferta(s)",
                        fontSize = 12.sp,
                        color = Color(0xFF555555),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Status badge: Archived > Active > Inactive
                val statusLabel: String
                val statusColor: Color
                val statusBg: Color
                if (company.isArchived) {
                    statusLabel = "Arquivada"
                    statusColor = Color(0xFF6D4C41)
                    statusBg = Color(0xFFEFEBE9)
                } else if (company.isActive == true) {
                    statusLabel = "Ativa"
                    statusColor = Color(0xFF2E7D32)
                    statusBg = Color(0xFFE8F5E9)
                } else {
                    statusLabel = "Inativa"
                    statusColor = Color(0xFFC62828)
                    statusBg = Color(0xFFFFEBEE)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Detalhes",
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(20.dp)
        )
    }
}