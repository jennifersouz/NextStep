package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Profile / User DTOs ──────────────────────────────────────────────────────

@Serializable
data class AdminProfileDto(
    val id: String,
    val email: String? = null,
    val role: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val phone: String? = null,

    // Default null — não usar true aqui.
    // Se o Supabase devolver null (por RLS ou coluna omitida), não queremos
    // que o Kotlin assuma que o utilizador está ativo.
    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("archived_at")
    val archivedAt: String? = null,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null
) {
    /** Arquivado se archived_at estiver preenchido — independente de isActive. */
    val isArchived: Boolean get() = archivedAt != null
}

@Serializable
data class AdminCreateUserRequest(
    val email: String,
    val password: String,
    val role: String,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val phone: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("student_number")
    val studentNumber: String? = null,

    val course: String? = null,

    @SerialName("academic_year")
    val academicYear: Int? = null,

    @SerialName("education_institution")
    val educationInstitution: String? = null,

    val department: String? = null,

    @SerialName("institution_profile_id")
    val institutionProfileId: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,

    val description: String? = null
)

// ── Company DTOs ─────────────────────────────────────────────────────────────

@Serializable
data class AdminCompanyDto(
    val id: String,

    @SerialName("profile_id")
    val profileId: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,
    val description: String? = null,
    val phone: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("offers_count")
    val offersCount: Int = 0,

    @SerialName("archived_at")
    val archivedAt: String? = null,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null
) {
    val isArchived: Boolean get() = archivedAt != null
}

// ── Other admin DTOs ─────────────────────────────────────────────────────────

@Serializable
data class AdminOfferDto(
    val id: String,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    @SerialName("company_id")
    val companyId: String? = null
)

@Serializable
data class AdminApplicationDto(
    val id: String,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String? = null
)

@Serializable
data class AdminTeacherEvaluationDto(
    val id: String,
    val status: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null
)

@Serializable
data class AdminAdvisorEvaluationDto(
    val id: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null
)

// ── Update DTOs ──────────────────────────────────────────────────────────────

@Serializable
data class AdminProfileUpdateDto(
    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val phone: String? = null,
    val role: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class AdminCompanyUpdateDto(
    @SerialName("company_name")
    val companyName: String? = null,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,
    val description: String? = null,
    val phone: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * DTO específico para edição de empresa pelo Admin.
 * Não contém: id, profile_id, archived_at, archived_by, archive_reason.
 */
@Serializable
data class AdminCompanyEditRequest(
    @SerialName("company_name")
    val companyName: String,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,
    val phone: String? = null,
    val description: String? = null,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("updated_at")
    val updatedAt: String
)

// ── Status update DTOs ───────────────────────────────────────────────────────

/**
 * Usado para reativar ou desativar utilizadores (profiles).
 * Envia apenas is_active e updated_at — sem archived_by, archived_at, id, email, role.
 */
@Serializable
data class UserActiveStatusUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class UserArchiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = false,

    @SerialName("archived_at")
    val archivedAt: String,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class CompanyActiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class CompanyDeactivateUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = false,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class CompanyReactivateUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("archived_at")
    val archivedAt: String? = null,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class CompanyArchiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = false,

    @SerialName("archived_at")
    val archivedAt: String,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class ProfileActiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class ProfileArchiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = false,

    @SerialName("archived_at")
    val archivedAt: String,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class OfferActiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("updated_at")
    val updatedAt: String
)

/**
 * DTO específico para desativar ofertas ao arquivar empresa.
 * A tabela offers NÃO tem coluna updated_at — apenas is_active.
 */
@Serializable
data class OfferDeactivateUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = false
)

// ── Offer detail ─────────────────────────────────────────────────────────────

@Serializable
data class AdminCompanyOfferDto(
    val id: String,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    val title: String? = null,
    val location: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    val description: String? = null,
    val area: String? = null,

    @SerialName("work_mode")
    val workMode: String? = null,

    val duration: String? = null,
    val vacancies: Int? = null,
    val requirements: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

// ApplicationTaskDto and ApplicationMessageDto are defined in
// their own files: ApplicationTaskDto.kt and ApplicationMessageDto.kt
