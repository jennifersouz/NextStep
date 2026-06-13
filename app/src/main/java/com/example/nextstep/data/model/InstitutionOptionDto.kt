package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa uma instituição carregada da tabela [profiles]
 * para ser usada no dropdown de seleção ao criar um Docente.
 *
 * Filtro aplicado no repositório:
 *  - role in ('institution', 'instituicao')
 *  - is_active = true
 *  - archived_at is null
 */
@Serializable
data class InstitutionOptionDto(
    val id: String,
    val email: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val role: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("archived_at")
    val archivedAt: String? = null
) {
    /**
     * Nome a mostrar no dropdown.
     * Prioridade: "firstName lastName" > firstName > email > "Instituição sem nome"
     */
    val displayName: String
        get() {
            val first = firstName?.takeIf { it.isNotBlank() }
            val last = lastName?.takeIf { it.isNotBlank() }
            return when {
                first != null && last != null -> "$first $last"
                first != null -> first
                email != null -> email
                else -> "Instituição sem nome"
            }
        }
}
