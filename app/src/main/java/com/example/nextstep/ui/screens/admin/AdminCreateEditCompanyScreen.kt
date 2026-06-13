package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyDto

@Composable
fun AdminCreateEditCompanyScreen(
    existingCompany: AdminCompanyDto? = null,
    onBackClick: () -> Unit = {},
    onSave: (
        companyName: String,
        nif: String?,
        businessArea: String?,
        location: String?,
        phone: String?,
        description: String?,
        isActive: Boolean
    ) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val isEditing = existingCompany != null

    // Usar rememberSaveable para sobreviver a recomposições.
    // LaunchedEffect sincroniza com a empresa real quando ela muda.
    var companyName by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.companyName ?: "")
    }
    var nif by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.nif ?: "")
    }
    var businessArea by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.businessArea ?: "")
    }
    var location by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.location ?: "")
    }
    var phone by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.phone ?: "")
    }
    var description by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.description ?: "")
    }
    // isActive: carregar o valor real da empresa — nunca false por defeito
    var isActive by rememberSaveable(existingCompany?.id) {
        mutableStateOf(existingCompany?.isActive == true)
    }

    // Garantir sincronização se a empresa vier depois do primeiro render
    LaunchedEffect(existingCompany?.id, existingCompany?.isActive) {
        if (existingCompany != null) {
            companyName = existingCompany.companyName ?: ""
            nif = existingCompany.nif ?: ""
            businessArea = existingCompany.businessArea ?: ""
            location = existingCompany.location ?: ""
            phone = existingCompany.phone ?: ""
            description = existingCompany.description ?: ""
            isActive = existingCompany.isActive == true
        }
    }

    var nameError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_label),
                    tint = Color.Black
                )
            }
            Text(
                text = if (isEditing) stringResource(R.string.editing_company_title) else stringResource(R.string.new_company_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Company name (required)
            Text(
                text = stringResource(R.string.company_name_field),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = companyName,
                onValueChange = {
                    companyName = it
                    nameError = false
                },
                placeholder = { Text(stringResource(R.string.company_name_placeholder), color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color.Black
                ),
                singleLine = true,
                isError = nameError
            )
            if (nameError) {
                Text(
                    text = stringResource(R.string.company_name_required_error),
                    color = Color(0xFFC62828),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NIF
            Text(
                text = stringResource(R.string.nif_field),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = nif,
                onValueChange = { nif = it },
                placeholder = { Text(stringResource(R.string.nif_placeholder), color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Business area
            Text(
                text = stringResource(R.string.business_area_field),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = businessArea,
                onValueChange = { businessArea = it },
                placeholder = { Text(stringResource(R.string.business_area_placeholder), color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            Text(
                text = stringResource(R.string.location_field),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text(stringResource(R.string.location_placeholder), color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            Text(
                text = stringResource(R.string.phone_field),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text(stringResource(R.string.phone_placeholder_example), color = Color(0xFF999999)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = stringResource(R.string.description_field),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text(stringResource(R.string.description_placeholder), color = Color(0xFF999999)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estado ativo — apenas em modo edição
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.company_active_label),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = if (isActive) stringResource(R.string.company_can_publish)
                                   else stringResource(R.string.company_cannot_publish),
                            fontSize = 12.sp,
                            color = Color(0xFF777777)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    // Switch usa o valor real da empresa — não um estado local independente
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFDFA52),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCCCCCC)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = {
                    if (companyName.isBlank()) {
                        nameError = true
                    } else {
                        onSave(
                            companyName.trim(),
                            nif.trim().ifBlank { null },
                            businessArea.trim().ifBlank { null },
                            location.trim().ifBlank { null },
                            phone.trim().ifBlank { null },
                            description.trim().ifBlank { null },
                            isActive
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isEditing) stringResource(R.string.save_changes_button) else stringResource(R.string.create_company_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
