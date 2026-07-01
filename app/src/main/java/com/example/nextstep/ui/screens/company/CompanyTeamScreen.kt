package com.example.nextstep.ui.screens.company

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R

enum class CompanyTeamTab {
    EMPLOYEES, INTERNS
}

@Composable
fun CompanyTeamScreen(
    onAddEmployeeClick: () -> Unit,
    onInternStudentClick: (String) -> Unit = {},
    onInternMessageClick: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(CompanyTeamTab.EMPLOYEES) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.company_team_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CompanyTeamTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        if (selectedTab == CompanyTeamTab.EMPLOYEES) {
            Button(
                onClick = onAddEmployeeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_employee),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        when (selectedTab) {
            CompanyTeamTab.EMPLOYEES -> {
                CompanyEmployeesScreen()
            }

            CompanyTeamTab.INTERNS -> {
                CompanyInternStudentsScreen(
                    onStudentClick = onInternStudentClick,
                    onMessageClick = onInternMessageClick
                )
            }
        }
    }
}

@Composable
private fun CompanyTeamTabRow(
    selectedTab: CompanyTeamTab,
    onTabSelected: (CompanyTeamTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        CompanyTeamTabItem(
            text = stringResource(R.string.employees),
            isSelected = selectedTab == CompanyTeamTab.EMPLOYEES,
            onClick = { onTabSelected(CompanyTeamTab.EMPLOYEES) },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(6.dp))

        CompanyTeamTabItem(
            text = stringResource(R.string.interns),
            isSelected = selectedTab == CompanyTeamTab.INTERNS,
            onClick = { onTabSelected(CompanyTeamTab.INTERNS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CompanyTeamTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) Color(0xFFFDFA52) else Color(0xFFF3F3F3)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
    }
}
