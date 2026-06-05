package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyAdvisorDto

@Composable
fun CompanyAdvisorsScreen(
    onAddClick: () -> Unit,
    onAdvisorClick: (String) -> Unit,
    viewModel: CompanyAdvisorsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadAdvisors()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            CompanyAdvisorsContent(
                state = state,
                onAddClick = onAddClick,
                onAdvisorClick = onAdvisorClick,
                onSearchChange = viewModel::onSearchChange,
                onSortClick = viewModel::toggleSort
            )
        }
    }
}

@Composable
private fun CompanyAdvisorsContent(
    state: CompanyAdvisorsUiState,
    onAddClick: () -> Unit,
    onAdvisorClick: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 28.dp,
            bottom = 110.dp
        )
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.manage_advisors),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDFA52),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = stringResource(R.string.add_advisor),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            CompanyAdvisorSearchBar(
                value = state.searchQuery,
                onValueChange = onSearchChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSortClick,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (state.sortAscending) {
                        stringResource(R.string.sort_ascending)
                    } else {
                        stringResource(R.string.sort_descending)
                    },
                    color = Color.Black,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.size(4.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF8A8A8A),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
        }

        if (state.filteredAdvisors.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_advisors),
                    color = Color(0xFF8A8A8A),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp)
                )
            }
        } else {
            items(
                items = state.filteredAdvisors,
                key = { advisor ->
                    advisor.id
                }
            ) { advisor ->
                CompanyAdvisorListItem(
                    advisor = advisor,
                    onClick = {
                        onAdvisorClick(advisor.id)
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun CompanyAdvisorSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = Color(0xFF7A7A7A),
                fontSize = 17.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF7A7A7A),
                modifier = Modifier.size(28.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(10.dp)),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF4F4F4),
            unfocusedContainerColor = Color(0xFFF4F4F4),
            disabledContainerColor = Color(0xFFF4F4F4),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun CompanyAdvisorListItem(
    advisor: CompanyAdvisorDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompanyAdvisorAvatar(
            name = advisor.name,
            size = 58
        )

        Spacer(modifier = Modifier.size(18.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = advisor.name,
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!advisor.department.isNullOrBlank()) {
                Text(
                    text = advisor.department,
                    color = Color(0xFF8A8A8A),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CompanyAdvisorAvatar(
    name: String,
    size: Int
) {
    val initials = name
        .split(" ")
        .filter { part ->
            part.isNotBlank()
        }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank {
            "?"
        }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFFFDFA52))
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.Black,
            fontSize = if (size >= 58) 18.sp else 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}