package com.example.nextstep.ui.screens.company

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyOfferDto
import com.example.nextstep.ui.components.AppFilterChipsRow
import com.example.nextstep.ui.utils.Formatters

@Composable
fun CompanyOffersScreen(
    onOfferClick: (String) -> Unit,
    onInternStudentsClick: () -> Unit = {},
    refreshKey: Int = 0,
    viewModel: CompanyOffersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(refreshKey) {
        viewModel.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "As minhas ofertas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        // Search bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchChange,
            placeholder = {
                Text(
                    text = "Pesquisar por título, área, localização...",
                    color = Color(0xFF8A8A8A),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = Color(0xFF8A8A8A)
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpar",
                            tint = Color(0xFF8A8A8A)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { focusManager.clearFocus() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE1E1E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        AppFilterChipsRow(
            filters = OfferFilter.entries.toList(),
            selectedFilter = state.selectedFilter,
            labelProvider = { it.label },
            onFilterSelected = { viewModel.onFilterChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessageRes != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(24.dp),
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

            state.filteredOffers.isEmpty() -> {
                CompanyOffersEmptyState(
                    hasSearch = state.searchQuery.isNotBlank(),
                    filter = state.selectedFilter
                )
            }

            else -> {
                if (isLandscape) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = state.filteredOffers,
                            key = { it.id }
                        ) { offer ->
                            CompanyOfferCard(
                                offer = offer,
                                onClick = { onOfferClick(offer.id) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = state.filteredOffers,
                            key = { it.id }
                        ) { offer ->
                            CompanyOfferCard(
                                offer = offer,
                                onClick = { onOfferClick(offer.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyOfferCard(
    offer: CompanyOfferDto,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = offer.title.orEmpty(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            val isArchived = offer.archivedAt != null
            when {
                isArchived -> OfferStatusBadge(
                    text = "Arquivada",
                    bgColor = Color(0xFFF3F3F3),
                    textColor = Color(0xFF8A8A8A)
                )
                else -> OfferStatusBadge(isActive = offer.isActive == true)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CompanyOfferDetailRow(label = "Área", value = Formatters.formatOfferArea(offer.area))
        CompanyOfferDetailRow(label = "Localização", value = offer.location.orEmpty())
        CompanyOfferDetailRow(
            label = "Regime",
            value = Formatters.formatWorkMode(offer.workMode)
        )
        CompanyOfferDetailRow(label = "Duração", value = offer.duration.orEmpty())
        CompanyOfferDetailRow(label = "Vagas", value = offer.vacancies?.toString().orEmpty())
    }
}

@Composable
fun CompanyOfferDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value.ifBlank { "—" },
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun OfferStatusBadge(isActive: Boolean) {
    val text = if (isActive) "Ativa" else "Inativa"
    val bgColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFBE9E7)
    val textColor = if (isActive) Color(0xFF138A36) else Color(0xFFB00020)

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun OfferStatusBadge(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun CompanyOffersEmptyState(
    hasSearch: Boolean,
    filter: OfferFilter
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasSearch) {
                Text(
                    text = "Nenhum resultado encontrado",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tenta alterar os termos da pesquisa.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A),
                    textAlign = TextAlign.Center
                )
            } else if (filter == OfferFilter.ARCHIVED) {
                Text(
                    text = "Nenhuma oferta arquivada",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Quando removeres ofertas, elas aparecerão aqui.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A),
                    textAlign = TextAlign.Center
                )
            } else if (filter == OfferFilter.INACTIVE) {
                Text(
                    text = "Nenhuma oferta inativa",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Quando desativares ofertas, elas aparecerão aqui.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A),
                    textAlign = TextAlign.Center
                )
            } else if (filter == OfferFilter.ACTIVE) {
                Text(
                    text = "Nenhuma oferta ativa",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Quando ativares ofertas, elas aparecerão aqui.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Ainda não existem ofertas publicadas.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Começa por criar uma nova oferta de estágio.",
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
